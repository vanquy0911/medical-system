package com.medical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDoctorCreateDto {
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String phone;
    private Long specializationId;
    private String biography;
    private String education;
    private String achievements;
    private String hospital;
    private java.util.List<String> certificateUrls;
}
