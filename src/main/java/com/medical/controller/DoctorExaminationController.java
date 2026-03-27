package com.medical.controller;

import com.medical.dto.ExaminationRequestDto;
import com.medical.dto.PatientRecordDto;
import com.medical.service.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorExaminationController {

    private final MedicalRecordService medicalRecordService;

    @GetMapping("/patients/{patientId}/history")
    public ResponseEntity<com.medical.dto.PatientHistoryResponse> getPatientHistory(@PathVariable Long patientId, Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(medicalRecordService.getPatientHistory(username, patientId));
    }

    @PostMapping("/appointments/{appointmentId}/examine")
    public ResponseEntity<Void> saveExamination(
            @PathVariable Long appointmentId,
            @RequestBody ExaminationRequestDto payload,
            Authentication authentication) {
        String username = authentication.getName();
        medicalRecordService.saveExamination(username, appointmentId, payload);
        return ResponseEntity.ok().build();
    }
}
