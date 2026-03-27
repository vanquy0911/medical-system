package com.medical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExaminationRequestDto {
    private String diagnosis;
    private String notes;
    private List<PrescriptionItemDto> medicines;
}
