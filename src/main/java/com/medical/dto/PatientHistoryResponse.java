package com.medical.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class PatientHistoryResponse {
    private Long patientId;
    private String fullName;
    private String phone;
    private String dob;
    private List<PatientRecordDto> records;
}
