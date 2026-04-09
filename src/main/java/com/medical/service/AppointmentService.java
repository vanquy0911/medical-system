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

import com.medical.dto.BookingRequestDto;
import com.medical.model.AppointmentStatus;
import com.medical.model.Patient;
import com.medical.repository.PatientRepository;
import com.medical.model.Shift;
import com.medical.model.WorkSchedule;
import com.medical.repository.WorkScheduleRepository;

@Service
@RequiredArgsConstructor
public class AppointmentService {

        private final AppointmentRepository appointmentRepository;
        private final DoctorRepository doctorRepository;
        private final PatientRepository patientRepository;
        private final WorkScheduleRepository workScheduleRepository;

        // Định nghĩa khung giờ khung giờ làm việc cố định
        private static final LocalTime MORNING_START = LocalTime.of(7, 30);
        private static final LocalTime MORNING_END = LocalTime.of(11, 30);
        private static final LocalTime AFTERNOON_START = LocalTime.of(13, 30);
        private static final LocalTime AFTERNOON_END = LocalTime.of(17, 30);

        @Transactional
        public void createAppointment(String username, BookingRequestDto request) {
                Patient patient = patientRepository.findByUserUsername(username)
                                .orElseThrow(() -> new RuntimeException(
                                                "Patient profile not found for user: " + username));

                Doctor doctor = doctorRepository.findById(request.getDoctorId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Doctor not found with id: " + request.getDoctorId()));

                LocalDateTime appointmentTime = LocalDateTime.of(
                                LocalDate.parse(request.getAppointmentDate()),
                                LocalTime.parse(request.getAppointmentTime()));

                // 1. Kiểm tra xem giờ đặt có nằm trong khung giờ làm việc không và thuộc ca nào
                Shift requiredShift = determineShift(appointmentTime.toLocalTime());

                // 2. Kiểm tra xem bác sĩ có đăng ký ca đó vào ngày đó không
                List<WorkSchedule> schedules = workScheduleRepository.findByDoctorIdAndWorkDate(doctor.getId(),
                                appointmentTime.toLocalDate());
                boolean isWorking = schedules.stream().anyMatch(s -> s.getShift() == requiredShift);

                if (!isWorking) {
                        throw new RuntimeException("Bác sĩ không có lịch làm việc vào ca này (" +
                                        (requiredShift == Shift.MORNING ? "Sáng" : "Chiều") + ").");
                }

                // 3. Kiểm tra trùng lịch (đã có)
                if (appointmentRepository.findByDoctorIdAndAppointmentTimeBetweenOrderByAppointmentTimeAsc(
                                doctor.getId(), appointmentTime, appointmentTime).size() > 0) {
                        throw new RuntimeException("Khung giờ này đã có người đặt.");
                }

                Appointment appointment = Appointment.builder()
                                .patient(patient)
                                .doctor(doctor)
                                .appointmentTime(appointmentTime)
                                .status(AppointmentStatus.PENDING)
                                .build();

                appointmentRepository.save(appointment);
        }

        private Shift determineShift(LocalTime time) {
                if (!time.isBefore(MORNING_START) && !time.isAfter(MORNING_END)) {
                        return Shift.MORNING;
                } else if (!time.isBefore(AFTERNOON_START) && !time.isAfter(AFTERNOON_END)) {
                        return Shift.AFTERNOON;
                } else {
                        throw new RuntimeException(
                                        "Giờ đặt lịch phải nằm trong khung giờ làm việc (Sáng: 07:30-11:30, Chiều: 13:30-17:30).");
                }
        }

        @Transactional(readOnly = true)
        public List<AppointmentDto> getAppointmentsToday(String username) {
                Doctor doctor = doctorRepository.findByUserUsername(username)
                                .orElseThrow(() -> new RuntimeException("Doctor not found"));

                LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
                LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

                List<Appointment> appointments = appointmentRepository
                                .findByDoctorIdAndAppointmentTimeBetweenOrderByAppointmentTimeAsc(
                                                doctor.getId(), startOfDay, endOfDay);

                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

                return appointments.stream().map(app -> AppointmentDto.builder()
                                .id(app.getId())
                                .time(app.getAppointmentTime().format(timeFormatter))
                                .patientName(app.getPatient().getFullName())
                                .phone(app.getPatient().getUser().getPhoneNumber())
                                // Assuming reason exists in MedicalRecord or we map it from somewhere else,
                                // maybe MedicalRecord's symptoms if available
                                // If not available in Appointment, we might just mock it or add it later. The
                                // entity Appointment doesn't have reason.
                                // We'll return empty or generic reason as placeholder
                                .reason("Khám bệnh theo lịch trình")
                                .status(app.getStatus().name().toLowerCase())
                                .build()).collect(Collectors.toList());
        }
}
