package com.medical.controller;

import com.medical.dto.WorkScheduleDto;
import com.medical.model.Shift;
import com.medical.service.WorkScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/doctors/schedules")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorScheduleController {

    private final WorkScheduleService workScheduleService;

    @GetMapping
    public ResponseEntity<List<WorkScheduleDto>> getSchedules(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(workScheduleService.getSchedulesByDate(username, date));
    }

    @PostMapping
    public ResponseEntity<WorkScheduleDto> createSchedule(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Shift shift,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(workScheduleService.createSchedule(username, date, shift));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        workScheduleService.deleteSchedule(username, id);
        return ResponseEntity.noContent().build();
    }
}
