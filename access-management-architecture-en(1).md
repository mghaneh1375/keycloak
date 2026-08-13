# Access Management — Keycloak Plugin + Camunda 8

## 1. Project Goal

This project implements an access-request workflow around Keycloak and Camunda 8.

A user creates an access request in Keycloak. The Keycloak plugin persists the request and publishes an event through RabbitMQ. The Camunda application consumes the event, starts a Zeebe workflow, creates an application-owned approval task, and exposes REST APIs for an administrator to inspect, approve, or reject the request.

After approval, Camunda publishes an access-grant command through RabbitMQ. The Keycloak plugin consumes it and applies the requested grant.

The MVP supports:

- Role grants
- Resource grants
- Access-request creation
- Zeebe workflow execution
- Application-owned workflow tasks
- Administrator task APIs
- Approval/rejection
- RabbitMQ integration
- Flyway migrations
- JWT-based authentication for Camunda APIs
- Keycloak Authorization Services

Camunda 8 is used instead of Camunda 7. The current MVP uses Zeebe only; Tasklist and Identity are not required.

---

## 2. Final Architecture

```text
User / Portal
      |
      | Keycloak JWT
      v
+------------------------+
| Keycloak                |
| access-management       |
| Custom Access SPI       |
+-----------+------------+
            |
            | AccessRequestCreatedEvent
            v
+------------------------+
| RabbitMQ                |
| access.exchange         |
+-----------+------------+
            |
            v
+------------------------+
| access-camunda          |
|                         |
| RabbitMQ Consumer       |
| WorkflowService         |
| Zeebe Client            |
| Job Workers             |
| REST Task API           |
| MySQL + Flyway          |
+-----------+------------+
            |
            | AccessGrantRequestedEvent
            v
+------------------------+
| RabbitMQ                |
+-----------+------------+
            |
            v
+------------------------+
| Keycloak Plugin         |
| AccessGrantConsumer     |
| RoleGrantHandler        |
| ResourceGrantHandler    |
+------------------------+
```

RabbitMQ is intentionally used at both integration boundaries. This keeps Keycloak and the workflow application loosely coupled and prevents approval from depending on a synchronous Keycloak HTTP call.

---

## 3. Why Zeebe Only?

The project originally used Camunda 7 concepts such as `TaskController`, delegates, Camunda 7 task tables, and process-engine configuration.

The architecture was migrated to Camunda 8.

For the MVP we do not need:

- Camunda 8 Tasklist
- Camunda 8 Identity
- Camunda 7 process-engine tables
- Camunda 7 history tables

Zeebe executes the BPMN workflow. Application-specific workflow state is stored in our own database.

---

## 4. Application-Owned Workflow Data

The Camunda application owns its workflow tables.

### `workflow_instance`

Stores the application-level representation of a Zeebe process instance:

- request ID
- process instance key
- process definition ID
- workflow status
- creation timestamp

### `workflow_task`

Stores the human approval task exposed by our REST API:

- workflow instance
- Zeebe job key
- task name
- task status
- creation timestamp
- decision information

No Camunda 7 `ACT_*` tables are required for this design.

---

## 5. Database Provisioning and Flyway

Flyway manages tables.

After the database exists, application startup runs Flyway and creates:

```text
flyway_schema_history
workflow_instance
workflow_task
...
```

The responsibility boundary is therefore:

```docker
MySQL / deployment
    -> creates database

Flyway
    -> creates and updates tables
```

If MySQL is provisioned by Docker Compose, `MYSQL_DATABASE=camunda` can create the database during first-time initialization.

---

## 6. Keycloak Plugin

The Keycloak plugin is a Keycloak SPI, not a Spring application.

It uses Keycloak's lifecycle and APIs and therefore does not use Spring annotations such as `@Component`.

The plugin contains:

- REST resource for access requests
- Access request service/repository
- Flyway migration
- RabbitMQ producer/consumer
- Access grant service
- Grant-handler interface
- `RoleGrantHandler`
- `ResourceGrantHandler`

The plugin obtains the current authenticated user from the Keycloak session instead of trusting a requester ID supplied by the HTTP client.

---

## 7. Access Request Flow

### 7.1 User creates a request

The user obtains a Keycloak token and calls:

```http
POST /realms/access-management/access/create
Authorization: Bearer <USER_TOKEN>
Content-Type: application/json
```

Role example:

```json
{
  "targetType": "ROLE",
  "targetId": "manager",
  "reason": "Test role access"
}
```

The plugin:

1. Authenticates the current user.
2. Persists `access_request`.
3. Publishes `AccessRequestCreatedEvent`.

### 7.2 RabbitMQ

The event is delivered through RabbitMQ, using the access-created messaging flow.

### 7.3 Camunda

The Camunda consumer receives the event and starts:

```text
bpmnProcessId = access-request
```

using the Zeebe client.

The resulting process instance is persisted in `workflow_instance`.

### 7.4 Approval job

The BPMN process contains a job worker with type:

```text
manager-approval
```

The worker creates a row in `workflow_task` and does not automatically complete the job because the administrator must make the decision.

---

## 8. Administrator APIs

The Camunda application exposes:

```http
GET  /tasks
GET  /tasks/{id}
POST /tasks/{id}/approve
POST /tasks/{id}/reject
```

Example:

```http
POST /tasks/1/approve
Authorization: Bearer <ADMIN_TOKEN>
Content-Type: application/json
```

```json
{
  "comment": "Approved"
}
```

The reject endpoint uses the same pattern.

Administrator endpoints are protected by Spring Security and Keycloak JWT roles.

A normal user must receive `403 Forbidden` when attempting approval/rejection.

---

## 9. Approval and Grant Flow

```text
Admin
  |
  | POST /tasks/{id}/approve
  v
TaskService
  |
  +-- update workflow_task
  +-- update workflow_instance
  |
  v
Zeebe
  |
  v
AccessGrantRequestedEvent
  |
  v
RabbitMQ
  |
  v
Keycloak AccessGrantConsumer
  |
  v
AccessGrantService
  |
  +-- RoleGrantHandler
  +-- ResourceGrantHandler
  +-- future grant handlers
```

The grant handler is selected according to the requested target type.

---

## 10. Grant Handler Design

The grant mechanism is interface-based so additional grant types can be added without putting all business logic into one service.

```text
AccessGrantService
       |
       v
GrantHandler
       |
       +-- RoleGrantHandler
       +-- ResourceGrantHandler
       +-- future handlers
```

### Role grant

Assigns a Keycloak role to the requester.

### Resource grant

Works with Keycloak Authorization Services. A resource server is associated with a Keycloak client.

For APIs using `findById()`, the event must contain the internal IDs expected by the Keycloak stores.

---

## 11. Resource Grant Example

In the Keycloak UI:

```text
Clients
  -> portal
    -> Authorization
      -> Resources
```

Example resource:

```json
{
  "name": "report",
  "owner": {
    "id": "30acd918-50f3-49fc-84f7-91511923735b",
    "name": "portal"
  },
  "ownerManagedAccess": false,
  "displayName": "report",
  "attributes": {},
  "_id": "55b05702-c19e-4e1f-95a3-c0de8f35ccd8",
  "uris": [],
  "icon_uri": ""
}
```

Here:

```text
Client / Resource Server internal ID:
30acd918-50f3-49fc-84f7-91511923735b

Resource internal ID:
55b05702-c19e-4e1f-95a3-c0de8f35ccd8
```

A resource grant can therefore contain:

```json
{
  "targetType": "RESOURCE",
  "targetId": "55b05702-c19e-4e1f-95a3-c0de8f35ccd8",
  "resourceServerId": "30acd918-50f3-49fc-84f7-91511923735b"
}
```

After approval, verify the resulting Authorization Services policy/permission in:

```text
Clients
  -> portal
    -> Authorization
      -> Resources
      -> Policies
      -> Permissions
```

Then perform an actual authorization request using the user's token.

---

## 12. Flyway in the Keycloak Plugin

Migration must happen during plugin initialization.

The desired lifecycle is:

```text
Keycloak startup
    -> plugin initialization
       -> Flyway migration

POST /create
    -> repository
       -> database
```

The plugin is not Spring-based, so the migration must be wired through an appropriate Keycloak SPI/provider lifecycle mechanism.

---

## 13. Security

### Keycloak

The plugin determines the current authenticated user from the Keycloak session.

### Camunda

The Camunda application is an OAuth2 Resource Server.

JWTs are validated against the Keycloak realm. Keycloak roles must be mapped into Spring Security authorities.

For example:

```text
Keycloak JWT:
realm_access.roles = ["ADMIN"]

Spring Security:
ROLE_ADMIN
```

can protect administrator operations.

Security verification:

```text
test-user token
    -> POST /tasks/{id}/approve
    -> 403 Forbidden
```

and:

```text
test-admin token
    -> POST /tasks/{id}/approve
    -> 200 OK
```

---

# 14. Automatic Keycloak Bootstrap

A plain `curl` shell script is included so a fresh environment does not need to be configured manually through the Keycloak UI.

The script creates the base test data:

- `access-management` realm
- `manager` role
- `ADMIN` role
- `test-user`
- `test-admin`
- passwords
- `portal` confidential client
- `portal-secret`
- `report` Authorization Services resource
- `VERIFY_PROFILE` required-action configuration needed for password-token testing

Save it as:

```text
scripts/keycloak-bootstrap.sh
```

Use the accompanying script file supplied with this documentation.

The script intentionally uses plain `curl` and `sed`; it does not require `jq`.

Make it executable:

```bash
chmod +x scripts/keycloak-bootstrap.sh
```

Run it:

```bash
./scripts/keycloak-bootstrap.sh
```

The script also obtains the internal client ID for `portal` and creates the `report` resource through Keycloak Authorization Services.

---

# 15. Getting Test Tokens

### Normal user

```bash
curl -s -X POST \
  "http://localhost:8080/realms/access-management/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=portal" \
  -d "client_secret=portal-secret" \
  -d "username=test-user" \
  -d "password=123456"
```

### Administrator

```bash
curl -s -X POST \
  "http://localhost:8080/realms/access-management/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=portal" \
  -d "client_secret=portal-secret" \
  -d "username=test-admin" \
  -d "password=123456"
```

Copy the `access_token` from the response.

---

# 16. End-to-End Role Test

### Create request

```bash
curl -X POST \
  "http://localhost:8080/realms/access-management/access/create" \
  -H "Authorization: Bearer <USER_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "targetType":"ROLE",
    "targetId":"manager",
    "reason":"Test role access"
  }'
```

### Verify workflow

Check:

```text
RabbitMQ
  -> access.created

Zeebe
  -> access-request
  -> manager-approval

MySQL
  -> workflow_instance
  -> workflow_task
```

### List tasks

```bash
curl \
  "http://localhost:8082/tasks" \
  -H "Authorization: Bearer <ADMIN_TOKEN>"
```

### Get task details

```bash
curl \
  "http://localhost:8082/tasks/<TASK_ID>" \
  -H "Authorization: Bearer <ADMIN_TOKEN>"
```

### Approve

```bash
curl -X POST \
  "http://localhost:8082/tasks/<TASK_ID>/approve" \
  -H "Authorization: Bearer <ADMIN_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "comment":"Approved for testing"
  }'
```

### Verify Keycloak

Inspect the user in Keycloak and verify that the requested `manager` role has been assigned.

---

# 17. End-to-End Resource Test

The bootstrap script creates:

```text
portal
  |
  +-- Authorization Services
       |
       +-- report
```

The actual resource ID is generated by Keycloak. Use that ID as `targetId` and the `portal` internal client ID as `resourceServerId` when the handler uses `findById()`.

Example:

```json
{
  "targetType": "RESOURCE",
  "targetId": "<RESOURCE_INTERNAL_ID>",
  "resourceServerId": "<PORTAL_CLIENT_INTERNAL_ID>",
  "reason": "Test resource access"
}
```

The workflow is:

```text
Keycloak
  -> RabbitMQ
  -> Camunda
  -> Zeebe
  -> manager-approval
  -> Admin approve
  -> RabbitMQ
  -> Keycloak
  -> ResourceGrantHandler
```

After approval, verify the Resource, Policy, and Permission in Keycloak and perform a real authorization check with the user's token.

---

# 18. Build and Deploy the Keycloak Plugin

Build:

```bash
mvn clean package
```

The JAR is:

```text
target/access-plugin-1.0.0.jar
```

Copy it into the Keycloak providers directory:

```bash
docker cp target/access-plugin-1.0.0.jar keycloak:/opt/keycloak/providers/
```

Restart Keycloak:

```bash
docker restart keycloak
```

Check logs:

```bash
docker logs -f keycloak
```

Verify that the plugin loads and that its Flyway initialization creates the expected schema/tables.

---

# 19. Build and Deploy access-camunda

Build:

```bash
mvn clean package
```

Make sure the MySQL database already exists.

On startup:

```text
Spring Boot
   |
   +-- Datasource
   +-- Flyway
   +-- RabbitMQ
   +-- Zeebe
   +-- Job Workers
```

Flyway creates the application tables.

The database itself is provisioned separately.

---

# 20. Complete Fresh-Environment Sequence

```text
1. Start MySQL
2. Create the access_camunda database
3. Start RabbitMQ
4. Start Zeebe
5. Start Keycloak
6. Build/install the access-plugin JAR
7. Restart Keycloak
8. Let plugin initialization run Flyway
9. Start access-camunda
10. Let its Flyway migrations run
11. Run keycloak-bootstrap.sh
12. Obtain a test-user token
13. Create an access request
14. Verify RabbitMQ delivery
15. Verify the Zeebe process
16. Query Camunda tasks
17. Obtain a test-admin token
18. Approve or reject the task
19. Verify the access-grant event
20. Verify the role/resource assignment in Keycloak
```

---

# 21. Final MVP Result

```text
Authenticated User
       |
       v
Keycloak Access Plugin
       |
       | access.created
       v
RabbitMQ
       |
       v
Camunda Application
       |
       v
Zeebe
       |
       v
Manager Approval Task
       |
       | approve / reject
       v
RabbitMQ
       |
       v
Keycloak Access Plugin
       |
       +--> RoleGrantHandler
       |
       +--> ResourceGrantHandler
       |
       v
Keycloak Authorization / Roles
```

The MVP is asynchronous across system boundaries, uses application-owned workflow state, uses Flyway for schema migrations, and keeps grant processing extensible through handler interfaces.

Future work can add additional grant types, retry/DLQ handling, idempotency, auditing/history, richer authorization policies, and production hardening without changing the core MVP architecture.
