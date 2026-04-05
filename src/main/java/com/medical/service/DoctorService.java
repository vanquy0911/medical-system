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

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final SpecializationRepository specializationRepository;

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

    private DoctorCardDto mapToCardDto(Doctor doctor) {
        return DoctorCardDto.builder()
                .id(doctor.getId())
                .fullName(doctor.getFullName())
                .specializationName(doctor.getSpecialization() != null ? doctor.getSpecialization().getName() : "Đa khoa")
                .experienceYears(doctor.getExperienceYears())
                .avatarUrl(doctor.getAvatarUrl())
                .build();
    }

    @Transactional(readOnly = true)
    public DoctorProfileDto getProfileByUsername(String username) {
        Doctor doctor = doctorRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found for user: " + username));
        
        return DoctorProfileDto.builder()
                .fullName(doctor.getFullName())
                .specializationName(doctor.getSpecialization() != null ? doctor.getSpecialization().getName() : "")
                .phoneNumber(doctor.getUser().getPhoneNumber())
                .email(doctor.getUser().getEmail())
                .experienceYears(doctor.getExperienceYears())
                // Assuming we have fields for bio/education in Doctor, but we only have experienceYears in entity
                // We'll mock bio and education as empty for now to match DTO if not available in DB
                .biography("Mock data: Biography...")
                .education("Mock data: Education...")
                .build();
    }

    @Transactional
    public DoctorProfileDto updateProfile(String username, DoctorProfileDto payload) {
        Doctor doctor = doctorRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));
        
        // Cập nhật thông tin bác sĩ
        doctor.setFullName(payload.getFullName());
        doctor.setExperienceYears(payload.getExperienceYears() != null ? payload.getExperienceYears() : doctor.getExperienceYears());

        // Cập nhật thông tin User liên kết
        doctor.getUser().setPhoneNumber(payload.getPhoneNumber());
        doctor.getUser().setEmail(payload.getEmail());

        // Ignore specialization update as per typical systems, or implement if needed
        
        doctorRepository.save(doctor);

        return getProfileByUsername(username);
    }
}
