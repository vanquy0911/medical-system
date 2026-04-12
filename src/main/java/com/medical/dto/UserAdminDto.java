package com.medical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAdminDto {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String role;
    private boolean enabled;
    private String createdAt;
    private String leaveStartDate;
    private String leaveEndDate;
    private String biography;
    private String education;
    private String achievements;
    private String hospital;
    private Double rating;
    private Integer reviewCount;
    private java.util.List<String> certificateUrls;
}
