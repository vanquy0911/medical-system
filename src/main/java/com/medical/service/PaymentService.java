package com.medical.service;

import com.medical.model.Appointment;
import com.medical.model.Payment;
import com.medical.model.PaymentMethod;
import com.medical.model.PaymentStatus;
import com.medical.dto.PaymentDto;
import com.medical.repository.AppointmentRepository;
import com.medical.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AppointmentRepository appointmentRepository;

    private static final Double DEFAULT_CONSULTATION_FEE = 300000.0;

    public List<PaymentDto> getAllPayments() {
        return paymentRepository.findAllByOrderByPaymentDateDesc()
                .stream()
                .map(this::mapToDto)
                .collect(java.util.stream.Collectors.toList());
    }

    private PaymentDto mapToDto(Payment payment) {
        return PaymentDto.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .paymentDate(payment.getPaymentDate())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .appointment(PaymentDto.AppointmentSummary.builder()
                        .id(payment.getAppointment().getId())
                        .patient(PaymentDto.PatientSummary.builder()
                                .fullName(payment.getAppointment().getPatient().getFullName())
                                .build())
                        .appointmentTime(payment.getAppointment().getAppointmentTime().toString())
                        .build())
                .build();
    }

    @Transactional
    public Payment confirmPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Giao dịch không tồn tại."));
        
        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            throw new RuntimeException("Giao dịch này đã được hoàn thành trước đó.");
        }

        payment.setStatus(PaymentStatus.COMPLETED);
        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment processCodPayment(Long appointmentId, String username) {
        // 1. Kiểm tra lịch hẹn
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Lịch hẹn không tồn tại."));

        // 2. Kiểm tra quyền sở hữu (Bệnh nhân chỉ được thanh toán lịch của mình)
        if (!appointment.getPatient().getUser().getUsername().equals(username)) {
            throw new RuntimeException("Bạn không có quyền thực hiện thanh toán cho lịch hẹn này.");
        }

        // 3. Kiểm tra xem đã có bản ghi thanh toán chưa
        paymentRepository.findByAppointmentId(appointmentId).ifPresent(p -> {
            throw new RuntimeException("Lịch hẹn này đã có thông tin thanh toán.");
        });

        // 4. Tạo bản ghi thanh toán COD
        Payment payment = Payment.builder()
                .appointment(appointment)
                .amount(DEFAULT_CONSULTATION_FEE)
                .method(PaymentMethod.COD)
                .status(PaymentStatus.PENDING)
                .build();

        return paymentRepository.save(payment);
    }
}
