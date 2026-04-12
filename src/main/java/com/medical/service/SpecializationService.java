package com.medical.service;

import com.medical.dto.SpecializationDto;
import com.medical.repository.SpecializationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpecializationService {

    private final SpecializationRepository specializationRepository;

    @Transactional(readOnly = true)
    public List<SpecializationDto> getAllSpecializations() {
        return specializationRepository.findAll().stream()
                .map(s -> SpecializationDto.builder()
                        .id(s.getId())
                        .name(s.getName())
                        .description(s.getDescription())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public SpecializationDto createSpecialization(SpecializationDto dto) {
        com.medical.model.Specialization spec = new com.medical.model.Specialization();
        spec.setName(dto.getName());
        spec.setDescription(dto.getDescription());
        spec = specializationRepository.save(spec);
        dto.setId(spec.getId());
        return dto;
    }

    @Transactional
    public SpecializationDto updateSpecialization(Long id, SpecializationDto dto) {
        com.medical.model.Specialization spec = specializationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Specialization not found: " + id));
        spec.setName(dto.getName());
        spec.setDescription(dto.getDescription());
        spec = specializationRepository.save(spec);
        return SpecializationDto.builder()
                .id(spec.getId())
                .name(spec.getName())
                .description(spec.getDescription())
                .build();
    }

    @Transactional
    public void deleteSpecialization(Long id) {
        if (!specializationRepository.existsById(id)) {
            throw new RuntimeException("Specialization not found: " + id);
        }
        specializationRepository.deleteById(id);
    }
}
