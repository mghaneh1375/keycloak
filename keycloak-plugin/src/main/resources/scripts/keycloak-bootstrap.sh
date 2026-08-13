#!/usr/bin/env bash
set -e

KEYCLOAK_URL="${KEYCLOAK_URL:-http://localhost:8080}"
REALM="access-management"

ADMIN_USER="${ADMIN_USER:-admin}"
ADMIN_PASSWORD="${ADMIN_PASSWORD:-admin}"

TEST_USER="${TEST_USER:-test-user}"
TEST_USER_PASSWORD="${TEST_USER_PASSWORD:-123456}"

TEST_ADMIN="${TEST_ADMIN:-test-admin}"
TEST_ADMIN_PASSWORD="${TEST_ADMIN_PASSWORD:-123456}"

CLIENT_ID="portal"
CLIENT_SECRET="portal-secret"

ADMIN_TOKEN=$(curl -s -X POST \
  "$KEYCLOAK_URL/realms/master/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=$ADMIN_USER" \
  -d "password=$ADMIN_PASSWORD" \
  -d "grant_type=password" \
  -d "client_id=admin-cli" \
  | sed -n 's/.*"access_token":"\([^"]*\)".*/\1/p')

if [ -z "$ADMIN_TOKEN" ]; then
  echo "Failed to obtain admin token"
  exit 1
fi

echo "Creating realm..."
curl -s -o /dev/null -w "%{http_code}\n" \
  -X POST "$KEYCLOAK_URL/admin/realms" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"realm\":\"$REALM\",\"enabled\":true}" || true

echo "Disabling VERIFY_PROFILE..."
curl -s -X PUT \
  "$KEYCLOAK_URL/admin/realms/$REALM/authentication/required-actions/VERIFY_PROFILE" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "alias": "VERIFY_PROFILE",
    "name": "Verify Profile",
    "providerId": "VERIFY_PROFILE",
    "enabled": false,
    "defaultAction": false,
    "priority": 10,
    "config": {}
  }' || true

echo "Creating manager role..."
curl -s -X POST \
  "$KEYCLOAK_URL/admin/realms/$REALM/roles" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"manager"}' || true

echo "Creating ADMIN role..."
curl -s -X POST \
  "$KEYCLOAK_URL/admin/realms/$REALM/roles" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"ADMIN"}' || true

echo "Creating test user..."
curl -s -X POST \
  "$KEYCLOAK_URL/admin/realms/$REALM/users" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"username\":\"$TEST_USER\",
    \"enabled\":true,
    \"email\":\"test@example.com\",
    \"emailVerified\":true,
    \"requiredActions\":[],
    \"credentials\":[{
      \"type\":\"password\",
      \"value\":\"$TEST_USER_PASSWORD\",
      \"temporary\":false
    }]
  }" || true

echo "Creating test admin..."
curl -s -X POST \
  "$KEYCLOAK_URL/admin/realms/$REALM/users" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"username\":\"$TEST_ADMIN\",
    \"enabled\":true,
    \"email\":\"test-admin@example.com\",
    \"emailVerified\":true,
    \"requiredActions\":[],
    \"credentials\":[{
      \"type\":\"password\",
      \"value\":\"$TEST_ADMIN_PASSWORD\",
      \"temporary\":false
    }]
  }" || true

echo "Creating portal client..."
curl -f -s -X POST \
  "$KEYCLOAK_URL/admin/realms/$REALM/clients" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"clientId\":\"$CLIENT_ID\",
    \"name\":\"Test Portal\",
    \"enabled\":true,
    \"protocol\":\"openid-connect\",
    \"publicClient\":false,
    \"secret\":\"$CLIENT_SECRET\",
    \"standardFlowEnabled\":true,
    \"directAccessGrantsEnabled\":true,
    \"serviceAccountsEnabled\":true,
    \"authorizationServicesEnabled\":true
  }" || true

echo "Finding portal internal client ID..."
PORTAL_CLIENT_ID=$(curl -s \
  "$KEYCLOAK_URL/admin/realms/$REALM/clients?clientId=$CLIENT_ID" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  | sed -n 's/.*"id":"\([^"]*\)".*/\1/p' | head -n 1)

if [ -z "$PORTAL_CLIENT_ID" ]; then
  echo "Could not find portal internal client ID"
  exit 1
fi

echo "Portal internal client ID: $PORTAL_CLIENT_ID"

echo "Creating report resource..."
RESOURCE_RESPONSE=$(curl -s -X POST \
  "$KEYCLOAK_URL/admin/realms/$REALM/clients/$PORTAL_CLIENT_ID/authz/resource-server/resource" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name":"report",
    "displayName":"report",
    "ownerManagedAccess":false,
    "uris":[],
    "attributes":{}
  }')

echo "Resource response: $RESOURCE_RESPONSE"

echo
cat <<INFO
==========================================
Bootstrap completed
==========================================
Realm:              $REALM
Client:             $CLIENT_ID
Client internal ID: $PORTAL_CLIENT_ID
Test user:          $TEST_USER
Test admin:         $TEST_ADMIN
Resource:           report

Use the returned resource _id as targetId for RESOURCE requests.
Use the portal internal client ID as resourceServerId when the handler uses findById().
INFO