package com.medical.controller;

import com.medical.dto.AppointmentDto;
import com.medical.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/doctors/appointments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorAppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping("/recent")
    public ResponseEntity<List<AppointmentDto>> getRecentAppointments(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(appointmentService.getAppointmentsToday(username));
    }
}
