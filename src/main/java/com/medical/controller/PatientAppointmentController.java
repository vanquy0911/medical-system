package com.medical.controller;

import com.medical.dto.AppointmentDto;
import com.medical.dto.BookingRequestDto;
import com.medical.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patients/appointments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class PatientAppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping("/my")
    public ResponseEntity<List<AppointmentDto>> getMyAppointments(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(appointmentService.getMyAppointments(username));
    }

    @PostMapping("/book")
    public ResponseEntity<?> bookAppointment(@RequestBody BookingRequestDto request, Authentication authentication) {
        try {
            String username = authentication.getName();
            appointmentService.createAppointment(username, request);
            return ResponseEntity.ok("Đặt lịch thành công! Đang chờ phòng khám xác nhận.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<String> cancelAppointment(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        appointmentService.cancelAppointment(id, username);
        return ResponseEntity.ok("Đã hủy lịch hẹn thành công.");
    }

    @PutMapping("/{id}/reschedule")
    public ResponseEntity<?> rescheduleAppointment(
            @PathVariable Long id,
            @RequestBody BookingRequestDto request,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            appointmentService.rescheduleAppointment(id, username, request);
            return ResponseEntity.ok("Đổi lịch thành công! Đang chờ phòng khám xác nhận lại.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
