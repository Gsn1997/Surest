package com.surest_member_managemant.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String email;
}