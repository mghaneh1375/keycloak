package com.company.controller;

import com.company.dto.DecisionRequest;
import com.company.dto.TaskDetailsResponse;
import com.company.dto.TaskResponse;
import com.company.repository.WorkflowTaskRepository;
import com.company.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final WorkflowTaskRepository workflowTaskRepository;
    private final TaskService taskService;

    @GetMapping
    public Page<TaskResponse> getTasks(Pageable pageable) {
        return taskService.getTasks(pageable);
    }

    @GetMapping("/{id}")
    public TaskDetailsResponse getTask(@PathVariable Long id) {
        return taskService.getTask(id);
    }


    @PostMapping("/{id}/approve")
    public ResponseEntity<Void> approve(
            @PathVariable Long id,
            @RequestBody DecisionRequest request
    ) {
        taskService.approve(id, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> reject(
            @PathVariable Long id,
            @RequestBody DecisionRequest request
    ) {
        taskService.reject(id, request);
        return ResponseEntity.ok().build();
    }
}