package com.medical.controller;

import com.medical.dto.DoctorCardDto;
import com.medical.dto.SpecializationDto;
import com.medical.service.DoctorService;
import com.medical.service.SpecializationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicController {

    private final DoctorService doctorService;
    private final SpecializationService specializationService;

    @GetMapping("/specializations")
    public ResponseEntity<List<SpecializationDto>> getSpecializations() {
        return ResponseEntity.ok(specializationService.getAllSpecializations());
    }

    @GetMapping("/doctors")
    public ResponseEntity<List<DoctorCardDto>> getDoctors(@RequestParam(required = false) Long specializationId) {
        if (specializationId != null) {
            return ResponseEntity.ok(doctorService.getDoctorsBySpecialization(specializationId));
        }
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }
}
