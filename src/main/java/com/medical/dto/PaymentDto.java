package com.medical.dto;

import com.medical.model.PaymentMethod;
import com.medical.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDto {
    private Long id;
    private Double amount;
    private LocalDateTime paymentDate;
    private PaymentMethod method;
    private PaymentStatus status;
    
    // Thông tin lịch hẹn rút gọn để tránh vòng lặp
    private AppointmentSummary appointment;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppointmentSummary {
        private Long id;
        private PatientSummary patient;
        private String appointmentTime;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatientSummary {
        private String fullName;
    }
}
