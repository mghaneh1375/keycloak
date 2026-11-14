package com.company.accessManagement.dto;

import lombok.Data;

@Data
public class SampleDto {
    private Integer id;
    private String name;

    @Override
    public String toString() {
        return "SampleDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
