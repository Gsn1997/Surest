package com.surest_member_managemant.dto;

import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiError {
    private int status;
    private String error;
    private String message;
    private String path;
    private Instant timestamp;
}