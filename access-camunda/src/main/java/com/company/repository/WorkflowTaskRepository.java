package com.company.repository;

import com.company.entity.WorkflowTask;
import com.company.enums.WorkflowTaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkflowTaskRepository extends JpaRepository<WorkflowTask, Long> {
    Page<WorkflowTask> findByStatus(WorkflowTaskStatus status, Pageable pageable);
    Optional<WorkflowTask> findByJobKey(Long jobKey);
}