package com.medical.service;

import com.medical.dto.PatientProfileDto;
import com.medical.model.Patient;
import com.medical.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    @Transactional(readOnly = true)
    public PatientProfileDto getProfileByUsername(String username) {
        Patient patient = patientRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Patient profile not found for user: " + username));
        
        return PatientProfileDto.builder()
                .fullName(patient.getFullName())
                .email(patient.getUser().getEmail())
                .phoneNumber(patient.getUser().getPhoneNumber())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender())
                .address(patient.getAddress())
                .build();
    }

    @Transactional
    public PatientProfileDto updateProfile(String username, PatientProfileDto payload) {
        Patient patient = patientRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));
        
        // Update Patient entity
        patient.setFullName(payload.getFullName());
        patient.setDateOfBirth(payload.getDateOfBirth());
        patient.setGender(payload.getGender());
        patient.setAddress(payload.getAddress());

        // Update User entity linked to the Patient
        patient.getUser().setPhoneNumber(payload.getPhoneNumber());
        patient.getUser().setEmail(payload.getEmail());

        patientRepository.save(patient);

        return getProfileByUsername(username);
    }
}
