package com.medical.controller;

import com.medical.dto.DoctorProfileDto;
import com.medical.service.DoctorService;
import com.medical.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/doctors/profile")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorProfileController {

    private final DoctorService doctorService;
    private final FileUploadService fileUploadService;

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

    @PostMapping("/avatar")
    public ResponseEntity<Map<String, String>> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        String username = authentication.getName();
        String avatarUrl = fileUploadService.saveAvatar(file);
        doctorService.updateAvatarUrl(username, avatarUrl);
        return ResponseEntity.ok(Map.of("avatarUrl", avatarUrl));
    }

    @DeleteMapping("/avatar")
    public ResponseEntity<Void> deleteAvatar(Authentication authentication) {
        String username = authentication.getName();
        doctorService.updateAvatarUrl(username, null);
        return ResponseEntity.ok().build();
    }
}

