package com.medical.controller;

import com.medical.dto.SpecializationDto;
import com.medical.service.SpecializationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/specializations")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminSpecializationController {

    private final SpecializationService specializationService;

    @PostMapping
    public ResponseEntity<SpecializationDto> createSpecialization(@RequestBody SpecializationDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(specializationService.createSpecialization(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpecializationDto> updateSpecialization(@PathVariable Long id, @RequestBody SpecializationDto dto) {
        return ResponseEntity.ok(specializationService.updateSpecialization(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSpecialization(@PathVariable Long id) {
        specializationService.deleteSpecialization(id);
        return ResponseEntity.ok(Map.of("message", "Xóa chuyên khoa thành công."));
    }
}
