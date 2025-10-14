package com.surest_member_managemant.dto;
import lombok.*;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    private String username;
    private String password;
}