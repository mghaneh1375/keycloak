package com.company.repository;

import com.company.entity.WorkflowInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkflowInstanceRepository extends JpaRepository<WorkflowInstance, Long> {
    Optional<WorkflowInstance> findByRequestId(Long requestId);
    Optional<WorkflowInstance> findByProcessInstanceKey(Long key);
}