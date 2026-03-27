package com.medical.controller;

import com.medical.dto.DoctorProfileDto;
import com.medical.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/doctors/profile")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorProfileController {

    private final DoctorService doctorService;

    @GetMapping
    public ResponseEntity<DoctorProfileDto> getProfile(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(doctorService.getProfileByUsername(username));
    }

    @PutMapping
    public ResponseEntity<DoctorProfileDto> updateProfile(@RequestBody DoctorProfileDto payload, Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(doctorService.updateProfile(username, payload));
    }
}
