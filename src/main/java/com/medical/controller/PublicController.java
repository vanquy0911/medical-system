package com.medical.controller;

import com.medical.dto.DoctorCardDto;
import com.medical.dto.SpecializationDto;
import com.medical.service.AppointmentService;
import com.medical.service.DoctorService;
import com.medical.service.SpecializationService;
import com.medical.repository.AppointmentRepository;
import com.medical.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicController {

    private final DoctorService doctorService;
    private final SpecializationService specializationService;
    private final AppointmentService appointmentService;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    @GetMapping("/specializations")
    public ResponseEntity<List<SpecializationDto>> getSpecializations() {
        return ResponseEntity.ok(specializationService.getAllSpecializations());
    }

    @GetMapping("/doctors")
    public ResponseEntity<List<DoctorCardDto>> getDoctors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long specializationId) {
        return ResponseEntity.ok(doctorService.searchDoctors(name, specializationId));
    }

    @GetMapping("/doctors/{id}")
    public ResponseEntity<DoctorCardDto> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @GetMapping("/doctors/{id}/profile")
    public ResponseEntity<?> getDoctorProfileById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getDoctorProfileById(id));
    }

    @GetMapping("/doctors/{id}/available-slots")
    public ResponseEntity<List<String>> getAvailableSlots(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(appointmentService.getAvailableTimeSlots(id, date));
    }

    @GetMapping("/debug-appointments-all")
    public ResponseEntity<?> debugAppointmentsAll() {
        return ResponseEntity.ok(appointmentRepository.findAll().stream()
                .map(a -> a.getDoctor().getId() + " | " + a.getAppointmentTime() + " | " + a.getStatus())
                .toList());
    }

    @GetMapping("/debug-users")
    public ResponseEntity<?> debugUsers() {
        return ResponseEntity.ok(userRepository.findAll().stream()
                .map(u -> u.getUsername() + " | Role: " + u.getRole() + " | ID: " + u.getId() + " | enabled: " + u.isEnabled())
                .toList());
    }
}
