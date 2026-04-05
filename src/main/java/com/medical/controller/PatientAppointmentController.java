package com.medical.controller;

import com.medical.dto.BookingRequestDto;
import com.medical.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patients/appointments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PATIENT')")
public class PatientAppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/book")
    public ResponseEntity<String> bookAppointment(@RequestBody BookingRequestDto request, Authentication authentication) {
        String username = authentication.getName();
        appointmentService.createAppointment(username, request);
        return ResponseEntity.ok("Đặt lịch thành công! Đang chờ phòng khám xác nhận.");
    }
}
