package com.medical.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.dto.ExaminationRequestDto;
import com.medical.dto.PatientRecordDto;
import com.medical.dto.PrescriptionItemDto;
import com.medical.model.*;
import com.medical.repository.AppointmentRepository;
import com.medical.repository.DoctorRepository;
import com.medical.repository.MedicalRecordRepository;
import com.medical.repository.PrescriptionRepository;
import com.medical.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional(readOnly = true)
    public List<PatientRecordDto> getPatientRecords(String username, Long patientId) {
        // We might want to check if the doctor has the right to view this patient, but for now we'll allow
        List<MedicalRecord> records = medicalRecordRepository.findByAppointmentPatientIdOrderByAppointmentAppointmentTimeDesc(patientId);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        return records.stream().map(record -> {
            List<PrescriptionItemDto> prescriptions = new ArrayList<>();
            prescriptionRepository.findByMedicalRecordId(record.getId()).ifPresent(prescription -> {
                try {
                    prescriptions.addAll(objectMapper.readValue(prescription.getMedicineList(), new TypeReference<List<PrescriptionItemDto>>() {}));
                } catch (JsonProcessingException e) {
                    // fall back or ignore
                }
            });

            return PatientRecordDto.builder()
                    .date(record.getAppointment().getAppointmentTime().format(dateFormatter))
                    .time(record.getAppointment().getAppointmentTime().format(timeFormatter))
                    .doctor(record.getAppointment().getDoctor().getFullName())
                    .diagnosis(record.getDiagnosis())
                    .prescriptions(prescriptions)
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public com.medical.dto.PatientHistoryResponse getPatientHistory(String username, Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        List<PatientRecordDto> records = getPatientRecords(username, patientId);

        return com.medical.dto.PatientHistoryResponse.builder()
                .patientId(patient.getId())
                .fullName(patient.getFullName())
                .phone(patient.getUser() != null ? patient.getUser().getPhoneNumber() : "")
                .dob(patient.getDateOfBirth() != null ? patient.getDateOfBirth().toString() : "")
                .records(records)
                .build();
    }

    @Transactional
    public void saveExamination(String username, Long appointmentId, ExaminationRequestDto payload) {
        Doctor doctor = doctorRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Appointment appointment = appointmentRepository.findByIdAndDoctorId(appointmentId, doctor.getId())
                .orElseThrow(() -> new RuntimeException("Appointment not found or unauthorized"));

        // Create MedicalRecord
        MedicalRecord medicalRecord = MedicalRecord.builder()
                .appointment(appointment)
                .diagnosis(payload.getDiagnosis())
                .notes(payload.getNotes())
                .symptoms("") // placeholder
                .build();
        
        medicalRecord = medicalRecordRepository.save(medicalRecord);

        // Convert medicines to JSON and save Prescription
        if (payload.getMedicines() != null && !payload.getMedicines().isEmpty()) {
            try {
                String medicineJson = objectMapper.writeValueAsString(payload.getMedicines());
                Prescription prescription = Prescription.builder()
                        .medicalRecord(medicalRecord)
                        .medicineList(medicineJson)
                        .build();
                prescriptionRepository.save(prescription);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error processing medicines", e);
            }
        }

        // Update Appointment status
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);
    }
}
