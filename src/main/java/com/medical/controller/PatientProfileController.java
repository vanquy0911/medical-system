package com.medical.controller;

import com.medical.dto.PatientProfileDto;
import com.medical.service.PatientService;
import com.medical.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/patients/profile")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class PatientProfileController {

    private final PatientService patientService;
    private final com.medical.service.MedicalRecordService medicalRecordService;
    private final FileUploadService fileUploadService;

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

    @PostMapping("/avatar")
    public ResponseEntity<Map<String, String>> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        String username = authentication.getName();
        String avatarUrl = fileUploadService.saveAvatar(file);
        patientService.updateAvatarUrl(username, avatarUrl);
        return ResponseEntity.ok(Map.of("avatarUrl", avatarUrl));
    }

    @DeleteMapping("/avatar")
    public ResponseEntity<Void> deleteAvatar(Authentication authentication) {
        String username = authentication.getName();
        patientService.updateAvatarUrl(username, null);
        return ResponseEntity.ok().build();
    }
}

