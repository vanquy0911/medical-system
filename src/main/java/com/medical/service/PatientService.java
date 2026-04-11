package com.medical.service;

import com.medical.dto.PatientProfileDto;
import com.medical.model.Patient;
import com.medical.model.User;
import com.medical.repository.PatientRepository;
import com.medical.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    private Patient getOrCreatePatient(String username) {
        return patientRepository.findByUserUsername(username)
                .orElseGet(() -> {
                    User user = userRepository.findByUsername(username)
                            .orElseThrow(() -> new RuntimeException("User not found: " + username));
                    Patient newPatient = Patient.builder()
                            .user(user)
                            .fullName(user.getUsername() != null ? user.getUsername() : "Chưa cập nhật")
                            .build();
                    return patientRepository.save(newPatient);
                });
    }

    @Transactional
    public PatientProfileDto getProfileByUsername(String username) {
        Patient patient = getOrCreatePatient(username);
        
        return PatientProfileDto.builder()
                .fullName(patient.getFullName())
                .email(patient.getUser().getEmail())
                .phoneNumber(patient.getUser().getPhoneNumber())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender())
                .address(patient.getAddress())
                .avatarUrl(patient.getAvatarUrl())
                .build();
    }

    @Transactional
    public PatientProfileDto updateProfile(String username, PatientProfileDto payload) {
        Patient patient = getOrCreatePatient(username);
        
        // Update Patient entity
        if (payload.getFullName() != null) {
            patient.setFullName(payload.getFullName());
        }
        patient.setDateOfBirth(payload.getDateOfBirth());
        patient.setGender(payload.getGender());
        patient.setAddress(payload.getAddress());

        // Update User entity linked to the Patient
        if (payload.getPhoneNumber() != null) {
            patient.getUser().setPhoneNumber(payload.getPhoneNumber());
        }
        if (payload.getEmail() != null) {
            patient.getUser().setEmail(payload.getEmail());
        }

        patientRepository.save(patient);

        return PatientProfileDto.builder()
                .fullName(patient.getFullName())
                .email(patient.getUser().getEmail())
                .phoneNumber(patient.getUser().getPhoneNumber())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender())
                .address(patient.getAddress())
                .avatarUrl(patient.getAvatarUrl())
                .build();
    }

    @Transactional
    public void updateAvatarUrl(String username, String avatarUrl) {
        Patient patient = getOrCreatePatient(username);
        patient.setAvatarUrl(avatarUrl);
        patientRepository.save(patient);
    }
}
