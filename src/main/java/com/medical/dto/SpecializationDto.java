package com.medical.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpecializationDto {
    private Long id;
    private String name;
    private String description;
}
