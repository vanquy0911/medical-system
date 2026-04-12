package com.medical.controller;

import com.medical.dto.AdminDoctorCreateDto;
import com.medical.dto.UserAdminDto;
import com.medical.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/patients")
    public ResponseEntity<List<UserAdminDto>> getAllPatients() {
        return ResponseEntity.ok(adminService.getAllPatients());
    }

    @GetMapping("/doctors")
    public ResponseEntity<List<UserAdminDto>> getAllDoctors() {
        return ResponseEntity.ok(adminService.getAllDoctors());
    }

    @PostMapping("/doctors")
    public ResponseEntity<?> createDoctorAccount(@RequestBody AdminDoctorCreateDto request) {
        try {
            adminService.createDoctorAccount(request);
            return ResponseEntity.ok(Map.of("message", "Doctor account created successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/users/{id}/toggle-status")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long id) {
        try {
            adminService.toggleUserStatus(id);
            return ResponseEntity.ok(Map.of("message", "User status updated"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/specializations")
    public ResponseEntity<?> addSpecialization(@RequestBody com.medical.model.Specialization request) {
        try {
            return ResponseEntity.ok(adminService.addSpecialization(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/specializations/{id}")
    public ResponseEntity<?> updateSpecialization(@PathVariable Long id, @RequestBody com.medical.model.Specialization request) {
        try {
            return ResponseEntity.ok(adminService.updateSpecialization(id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/specializations/{id}")
    public ResponseEntity<?> deleteSpecialization(@PathVariable Long id) {
        try {
            adminService.deleteSpecialization(id);
            return ResponseEntity.ok(Map.of("message", "Đã xóa chuyên khoa"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/doctors/{id}/assign-leave")
    public ResponseEntity<?> assignDoctorLeave(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            adminService.assignDoctorLeave(id, request.get("startDate"), request.get("endDate"));
            return ResponseEntity.ok(Map.of("message", "Đã cập nhật kỳ nghỉ phép cho bác sĩ."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/doctors/{id}")
    public ResponseEntity<?> updateDoctor(@PathVariable Long id, @RequestBody AdminDoctorCreateDto request) {
        try {
            adminService.updateDoctorAccount(id, request);
            return ResponseEntity.ok(Map.of("message", "Doctor updated successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/doctors/{id}")
    public ResponseEntity<?> deleteDoctor(@PathVariable Long id) {
        try {
            adminService.deleteDoctor(id);
            return ResponseEntity.ok(Map.of("message", "Doctor deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/doctors/{id}/certificates")
    public ResponseEntity<?> uploadCertificate(
            @PathVariable Long id,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        try {
            String url = adminService.saveCertificateFile(file); // I need to implement this helper in AdminService
            adminService.addCertificate(id, url);
            return ResponseEntity.ok(Map.of("certificateUrl", url));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/doctors/{id}/certificates")
    public ResponseEntity<?> deleteCertificate(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            adminService.removeCertificate(id, request.get("certificateUrl"));
            return ResponseEntity.ok(Map.of("message", "Certificate removed"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
