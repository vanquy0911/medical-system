package com.medical.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequestDto {
    private Long doctorId;
    private String appointmentDate; // YYYY-MM-DD
    private String appointmentTime; // HH:mm
    private String symptoms;
}
