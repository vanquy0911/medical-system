package com.medical.service;

import com.medical.dto.AdminDoctorCreateDto;
import com.medical.dto.UserAdminDto;
import com.medical.model.*;
import com.medical.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final SpecializationRepository specializationRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileUploadService fileUploadService;

    public String saveCertificateFile(MultipartFile file) {
        return fileUploadService.saveCertificate(file);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getDashboardStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalDoctors", doctorRepository.count());
        stats.put("totalPatients", patientRepository.count());
        stats.put("totalAppointments", appointmentRepository.count());
        return stats;
    }

    @Transactional(readOnly = true)
    public List<UserAdminDto> getAllPatients() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.USER)
                .map(this::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserAdminDto> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .map(doctor -> {
                    User user = doctor.getUser();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    String createdAtStr = user.getCreatedAt() != null ? user.getCreatedAt().format(formatter) : "N/A";
                    return UserAdminDto.builder()
                            .id(user.getId()) // Giao diện đang dùng ID của User
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .phone(user.getPhoneNumber())
                            .role(user.getRole().name())
                            .enabled(user.isEnabled())
                            .createdAt(createdAtStr)
                            .leaveStartDate(doctor.getLeaveStartDate() != null ? doctor.getLeaveStartDate().toString() : null)
                            .leaveEndDate(doctor.getLeaveEndDate() != null ? doctor.getLeaveEndDate().toString() : null)
                            .biography(doctor.getBiography())
                            .education(doctor.getEducation())
                            .achievements(doctor.getAchievements())
                            .hospital(doctor.getHospital())
                            .rating(doctor.getRating())
                            .reviewCount(doctor.getReviewCount())
                            .certificateUrls(new java.util.ArrayList<>(doctor.getCertificateUrls()))
                            .build();
                })
                .collect(Collectors.toList());
    }

    private UserAdminDto mapToUserDto(User user) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String createdAtStr = user.getCreatedAt() != null ? user.getCreatedAt().format(formatter) : "N/A";
        return UserAdminDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhoneNumber())
                .role(user.getRole().name())
                .enabled(user.isEnabled())
                .createdAt(createdAtStr)
                .build();
    }

    @Transactional
    public void toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getRole() == Role.ADMIN) {
            throw new RuntimeException("Cannot toggle ADMIN account status");
        }
        
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    @Transactional
    public void createDoctorAccount(AdminDoctorCreateDto request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        Specialization specialization = specializationRepository.findById(request.getSpecializationId())
                .orElseThrow(() -> new RuntimeException("Specialization not found"));

        User doctorUser = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .phoneNumber(request.getPhone())
                .role(Role.DOCTOR)
                .enabled(true)
                .build();

        doctorUser = userRepository.save(doctorUser);

        Doctor doctor = Doctor.builder()
                .user(doctorUser)
                .fullName(request.getFullName())
                .specialization(specialization)
                .biography(request.getBiography())
                .education(request.getEducation())
                .achievements(request.getAchievements())
                .hospital(request.getHospital() != null ? request.getHospital() : "Hệ thống Phòng Khám Xanh")
                .rating(5.0)
                .reviewCount(0)
                .build();

        doctorRepository.save(doctor);
    }

    @Transactional
    public Specialization addSpecialization(Specialization request) {
        if (specializationRepository.findByName(request.getName()).isPresent()) {
            throw new RuntimeException("Chuyên khoa này đã tồn tại!");
        }
        return specializationRepository.save(request);
    }

    @Transactional
    public Specialization updateSpecialization(Long id, Specialization request) {
        Specialization spec = specializationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyên khoa!"));
        spec.setName(request.getName());
        spec.setDescription(request.getDescription());
        return specializationRepository.save(spec);
    }

    @Transactional
    public void deleteSpecialization(Long id) {
        // Có thể check xem có bác sĩ nào đang thụ thuộc chuyên khoa này hay không.
        if (doctorRepository.countBySpecializationId(id) > 0) {
            throw new RuntimeException("Không thể xóa vì đang có bác sĩ thuộc chuyên khoa này.");
        }
        specializationRepository.deleteById(id);
    }

    @Transactional
    public void assignDoctorLeave(Long doctorId, String startDate, String endDate) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        
        if (startDate == null || endDate == null || startDate.trim().isEmpty() || endDate.trim().isEmpty()) {
            doctor.setLeaveStartDate(null);
            doctor.setLeaveEndDate(null);
        } else {
            doctor.setLeaveStartDate(java.time.LocalDate.parse(startDate));
            doctor.setLeaveEndDate(java.time.LocalDate.parse(endDate));
        }
        doctorRepository.save(doctor);
    }

    @Transactional
    public void updateDoctorAccount(Long id, AdminDoctorCreateDto request) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        
        User user = doctor.getUser();
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhone());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        userRepository.save(user);

        Specialization spec = specializationRepository.findById(request.getSpecializationId())
                .orElseThrow(() -> new RuntimeException("Specialization not found"));

        doctor.setFullName(request.getFullName());
        doctor.setSpecialization(spec);
        doctor.setBiography(request.getBiography());
        doctor.setEducation(request.getEducation());
        doctor.setAchievements(request.getAchievements());
        doctor.setHospital(request.getHospital());
        
        doctorRepository.save(doctor);
    }

    @Transactional
    public void deleteDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        
        User user = doctor.getUser();
        doctorRepository.delete(doctor);
        userRepository.delete(user);
    }

    @Transactional
    public void addCertificate(Long doctorId, String certificateUrl) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        doctor.getCertificateUrls().add(certificateUrl);
        doctorRepository.save(doctor);
    }

    @Transactional
    public void removeCertificate(Long doctorId, String certificateUrl) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        doctor.getCertificateUrls().remove(certificateUrl);
        doctorRepository.save(doctor);
    }
}
