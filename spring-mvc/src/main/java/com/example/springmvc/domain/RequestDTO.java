package com.example.springmvc.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestDTO {
    private String username;
    private int age;
    private LocalDateTime fromAt;
    private LocalDateTime toAt;
}
