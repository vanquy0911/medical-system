package com.medical.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorCardDto {
    private Long id;
    private String fullName;
    private String specializationName;
    private int experienceYears;
    private String avatarUrl;
    private java.util.List<String> availableSlots;
    private String leaveStartDate;
    private String leaveEndDate;
    private Double rating;
    private Integer reviewCount;
    private String hospital;
}
