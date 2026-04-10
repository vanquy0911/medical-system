package com.medical.controller;

import com.medical.dto.PatientProfileDto;
import com.medical.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patients/profile")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PATIENT')")
public class PatientProfileController {

    private final PatientService patientService;
    private final com.medical.service.MedicalRecordService medicalRecordService;

    @GetMapping
    public ResponseEntity<PatientProfileDto> getProfile(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(patientService.getProfileByUsername(username));
    }

    @GetMapping("/history")
    public ResponseEntity<java.util.List<com.medical.dto.PatientRecordDto>> getMyHistory(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(medicalRecordService.getMyMedicalHistory(username));
    }

    @PutMapping
    public ResponseEntity<PatientProfileDto> updateProfile(
            @RequestBody PatientProfileDto payload,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(patientService.updateProfile(username, payload));
    }
}
