package com.medical.service;

import com.medical.dto.DoctorProfileDto;
import com.medical.model.Doctor;
import com.medical.model.Specialization;
import com.medical.repository.DoctorRepository;
import com.medical.repository.SpecializationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import com.medical.dto.DoctorCardDto;

import org.springframework.context.annotation.Lazy;
import java.time.LocalDate;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final SpecializationRepository specializationRepository;
    private final AppointmentService appointmentService;

    public DoctorService(DoctorRepository doctorRepository, 
                         SpecializationRepository specializationRepository,
                         @Lazy AppointmentService appointmentService) {
        this.doctorRepository = doctorRepository;
        this.specializationRepository = specializationRepository;
        this.appointmentService = appointmentService;
    }

    @Transactional(readOnly = true)
    public List<DoctorCardDto> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .map(this::mapToCardDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DoctorCardDto> getDoctorsBySpecialization(Long specializationId) {
        return doctorRepository.findBySpecializationId(specializationId).stream()
                .map(this::mapToCardDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DoctorCardDto> searchDoctors(String name, Long specializationId) {
        List<Doctor> doctors;
        
        if (name != null && !name.isEmpty() && specializationId != null) {
            doctors = doctorRepository.findBySpecializationIdAndFullNameContainingIgnoreCase(specializationId, name);
        } else if (name != null && !name.isEmpty()) {
            doctors = doctorRepository.findByFullNameContainingIgnoreCase(name);
        } else if (specializationId != null) {
            doctors = doctorRepository.findBySpecializationId(specializationId);
        } else {
            doctors = doctorRepository.findAll();
        }

        return doctors.stream()
                .map(this::mapToCardDto)
                .collect(Collectors.toList());
    }

    private DoctorCardDto mapToCardDto(Doctor doctor) {
        return DoctorCardDto.builder()
                .id(doctor.getId())
                .fullName(doctor.getFullName())
                .specializationName(doctor.getSpecialization() != null ? doctor.getSpecialization().getName() : "Đa khoa")
                .experienceYears(doctor.getExperienceYears())
                .avatarUrl(doctor.getAvatarUrl())
                .availableSlots(appointmentService.getAvailableTimeSlots(doctor.getId(), LocalDate.now()))
                .leaveStartDate(doctor.getLeaveStartDate() != null ? doctor.getLeaveStartDate().toString() : null)
                .leaveEndDate(doctor.getLeaveEndDate() != null ? doctor.getLeaveEndDate().toString() : null)
                .rating(doctor.getRating() != null ? doctor.getRating() : 5.0)
                .reviewCount(doctor.getReviewCount() != null ? doctor.getReviewCount() : 0)
                .hospital(doctor.getHospital() != null ? doctor.getHospital() : "Hệ thống Phòng Khám Xanh")
                .build();
    }

    @Transactional(readOnly = true)
    public DoctorProfileDto getProfileByUsername(String username) {
        Doctor doctor = doctorRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found for user: " + username));
        
        return mapToProfileDto(doctor);
    }

    @Transactional(readOnly = true)
    public DoctorCardDto getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        return mapToCardDto(doctor);
    }

    @Transactional(readOnly = true)
    public DoctorProfileDto getDoctorProfileById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        return mapToProfileDto(doctor);
    }

    private DoctorProfileDto mapToProfileDto(Doctor doctor) {
        return DoctorProfileDto.builder()
                .fullName(doctor.getFullName())
                .specializationName(doctor.getSpecialization() != null ? doctor.getSpecialization().getName() : "")
                .phoneNumber(doctor.getUser().getPhoneNumber())
                .email(doctor.getUser().getEmail())
                .experienceYears(doctor.getExperienceYears())
                .avatarUrl(doctor.getAvatarUrl())
                .biography(doctor.getBiography())
                .education(doctor.getEducation())
                .achievements(doctor.getAchievements())
                .hospital(doctor.getHospital() != null ? doctor.getHospital() : "Hệ thống Phòng Khám Xanh")
                .rating(doctor.getRating() != null ? doctor.getRating() : 5.0)
                .reviewCount(doctor.getReviewCount() != null ? doctor.getReviewCount() : 0)
                .leaveStartDate(doctor.getLeaveStartDate() != null ? doctor.getLeaveStartDate().toString() : null)
                .leaveEndDate(doctor.getLeaveEndDate() != null ? doctor.getLeaveEndDate().toString() : null)
                .certificateUrls(new java.util.ArrayList<>(doctor.getCertificateUrls()))
                .build();
    }

    @Transactional
    public DoctorProfileDto updateProfile(String username, DoctorProfileDto payload) {
        Doctor doctor = doctorRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));
        
        doctor.setFullName(payload.getFullName());
        doctor.setExperienceYears(payload.getExperienceYears() != null ? payload.getExperienceYears() : doctor.getExperienceYears());
        doctor.getUser().setPhoneNumber(payload.getPhoneNumber());
        doctor.getUser().setEmail(payload.getEmail());
        doctor.setBiography(payload.getBiography());
        doctor.setEducation(payload.getEducation());
        
        doctorRepository.save(doctor);
        return mapToProfileDto(doctor);
    }

    @Transactional
    public void updateAvatarUrl(String username, String avatarUrl) {
        Doctor doctor = doctorRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));
        doctor.setAvatarUrl(avatarUrl);
        doctorRepository.save(doctor);
    }
}
