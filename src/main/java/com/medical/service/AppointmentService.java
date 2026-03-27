package com.medical.service;

import com.medical.dto.AppointmentDto;
import com.medical.model.Appointment;
import com.medical.model.Doctor;
import com.medical.repository.AppointmentRepository;
import com.medical.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;

    @Transactional(readOnly = true)
    public List<AppointmentDto> getAppointmentsToday(String username) {
        Doctor doctor = doctorRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetweenOrderByAppointmentTimeAsc(
                doctor.getId(), startOfDay, endOfDay);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        return appointments.stream().map(app -> AppointmentDto.builder()
                .id(app.getId())
                .time(app.getAppointmentTime().format(timeFormatter))
                .patientName(app.getPatient().getFullName())
                .phone(app.getPatient().getUser().getPhoneNumber())
                // Assuming reason exists in MedicalRecord or we map it from somewhere else, maybe MedicalRecord's symptoms if available
                // If not available in Appointment, we might just mock it or add it later. The entity Appointment doesn't have reason.
                // We'll return empty or generic reason as placeholder
                .reason("Khám bệnh theo lịch trình") 
                .status(app.getStatus().name().toLowerCase())
                .build()).collect(Collectors.toList());
    }
}
