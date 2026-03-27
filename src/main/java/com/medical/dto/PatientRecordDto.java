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
public class PatientRecordDto {
    private String date;
    private String time;
    private String doctor;
    private String diagnosis;
    private List<PrescriptionItemDto> prescriptions;
}
