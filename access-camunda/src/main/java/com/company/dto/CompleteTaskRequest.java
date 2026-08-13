package com.company.dto;

import lombok.Data;
import java.util.Map;

@Data
public class CompleteTaskRequest {
    private Map<String,Object> variables;
}