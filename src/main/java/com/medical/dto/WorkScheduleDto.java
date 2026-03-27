package com.medical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkScheduleDto {
    private Long id;
    private String time; // e.g., "08:00 - 08:30"
    private Integer maxPatients;
    private String status; // "Còn trống", "Đã đặt kín", "Chờ duyệt"
}
