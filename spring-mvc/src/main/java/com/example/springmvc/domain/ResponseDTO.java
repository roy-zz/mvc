package com.example.springmvc.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResponseDTO {
    private String username;
    private int age;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
