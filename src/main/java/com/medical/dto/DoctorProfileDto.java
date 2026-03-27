package com.medical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorProfileDto {
    private String fullName;
    private String specializationName;
    private String phoneNumber;
    private String email;
    private String biography;
    private String education;
    private Integer experienceYears;
}
