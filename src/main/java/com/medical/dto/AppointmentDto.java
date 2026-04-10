package com.medical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDto {
    private Long id;
    private String date;
    private String time;
    private String doctorName;
    private String specialtyName;
    private String patientName;
    private String phone;
    private String reason;
    private String status; // 'pending', 'confirmed', 'completed', 'canceled'
}
