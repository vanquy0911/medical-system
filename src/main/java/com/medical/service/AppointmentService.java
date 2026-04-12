package com.medical.service;

import com.medical.dto.AppointmentDto;
import com.medical.model.Appointment;
import com.medical.model.Doctor;
import com.medical.repository.AppointmentRepository;
import com.medical.repository.DoctorRepository;
import com.medical.repository.WorkScheduleRepository;
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

                LocalDate date = LocalDate.parse(request.getAppointmentDate());
                LocalDateTime appointmentTime;
                if (request.getAppointmentTime().contains("Sáng")) {
                    appointmentTime = LocalDateTime.of(date, LocalTime.of(8, 0));
                } else if (request.getAppointmentTime().contains("Chiều")) {
                    appointmentTime = LocalDateTime.of(date, LocalTime.of(13, 30));
                } else {
                    // Fallback for old explicit times
                    LocalTime time = LocalTime.parse(request.getAppointmentTime());
                    appointmentTime = LocalDateTime.of(date, time);
                }

                // 1. Kiểm tra xem giờ đặt có nằm trong khung giờ làm việc không và thuộc ca nào
                Shift requiredShift = determineShift(appointmentTime.toLocalTime());

                // 2. Kiểm tra xem bác sĩ có đăng ký ca đó vào ngày đó không
                List<WorkSchedule> schedules = workScheduleRepository.findByDoctorIdAndWorkDate(doctor.getId(),
                                appointmentTime.toLocalDate());
                boolean isWorking = schedules.stream().anyMatch(s -> s.getShift() == requiredShift);

                if (!isWorking) {
                        WorkSchedule newSchedule = WorkSchedule.builder()
                                        .doctor(doctor)
                                        .workDate(appointmentTime.toLocalDate())
                                        .shift(requiredShift)
                                        .build();
                        workScheduleRepository.save(newSchedule);
                }

                // Capacity check per logic limit
                long overlappingCount = appointmentRepository.findByDoctorIdAndAppointmentTimeBetweenOrderByAppointmentTimeAsc(
                        doctor.getId(),
                        appointmentTime.toLocalDate().atStartOfDay(),
                        appointmentTime.toLocalDate().atTime(LocalTime.MAX)
                ).stream()
                 .filter(a -> a.getStatus() != AppointmentStatus.CANCELED && a.getAppointmentTime().isEqual(appointmentTime))
                 .count();

                if (overlappingCount >= 10) {
                    throw new RuntimeException("Ca khám này đã đạt giới hạn tối đa (10 bệnh nhân). Vui lòng chọn ca khác.");
                }

                Appointment appointment = Appointment.builder()
                                .patient(patient)
                                .doctor(doctor)
                                .appointmentTime(appointmentTime)
                                .symptoms(request.getSymptoms())
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

                // Fetch ALL upcoming appointments starting from the beginning of today
                List<Appointment> appointments = appointmentRepository
                                .findByDoctorIdAndAppointmentTimeGreaterThanEqualOrderByAppointmentTimeAsc(
                                                doctor.getId(), startOfDay);

                return appointments.stream().map(this::mapToDto).collect(Collectors.toList());
        }

        @Transactional(readOnly = true)
        public List<String> getAvailableTimeSlots(Long doctorId, LocalDate date) {
            List<String> availableSlots = new java.util.ArrayList<>();
            
            Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
            if (doctor == null) return availableSlots;

            // Check if Doctor is on Leave
            if (doctor.getLeaveStartDate() != null && doctor.getLeaveEndDate() != null) {
                if (!date.isBefore(doctor.getLeaveStartDate()) && !date.isAfter(doctor.getLeaveEndDate())) {
                    return availableSlots; // Doctor is on leave, no slots!
                }
            }

            // Check Doctor's registered shifts for this date
            List<WorkSchedule> schedules = workScheduleRepository.findByDoctorIdAndWorkDate(doctorId, date);
            if (schedules.isEmpty()) {
                return availableSlots; // No shifts registered today
            }

            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
            
            List<Appointment> bookedAppointments = appointmentRepository
                            .findByDoctorIdAndAppointmentTimeBetweenOrderByAppointmentTimeAsc(
                                            doctorId, startOfDay, endOfDay);
            
            boolean worksMorning = schedules.stream().anyMatch(s -> s.getShift() == Shift.MORNING);
            boolean worksAfternoon = schedules.stream().anyMatch(s -> s.getShift() == Shift.AFTERNOON);

            if (worksMorning) {
                long morningCount = bookedAppointments.stream()
                    .filter(a -> a.getStatus() != AppointmentStatus.CANCELED && a.getAppointmentTime().toLocalTime().equals(LocalTime.of(8, 0)))
                    .count();
                if (morningCount < 10) {
                    availableSlots.add("Ca Sáng (còn " + (10 - morningCount) + " chỗ)");
                }
            }

            if (worksAfternoon) {
                long afternoonCount = bookedAppointments.stream()
                    .filter(a -> a.getStatus() != AppointmentStatus.CANCELED && a.getAppointmentTime().toLocalTime().equals(LocalTime.of(13, 30)))
                    .count();
                if (afternoonCount < 10) {
                    availableSlots.add("Ca Chiều (còn " + (10 - afternoonCount) + " chỗ)");
                }
            }
            return availableSlots;
        }

        @Transactional(readOnly = true)
        public List<AppointmentDto> getMyAppointments(String username) {
                List<Appointment> appointments = appointmentRepository
                                .findByPatientUserUsernameOrderByAppointmentTimeDesc(username);
                return appointments.stream().map(this::mapToDto).collect(Collectors.toList());
        }

        private AppointmentDto mapToDto(Appointment app) {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

                String diagnosisData = null;
                String prescriptionData = null;
                Long priceData = 300000L; // Mock baseline price

                if (app.getMedicalRecord() != null) {
                    diagnosisData = app.getMedicalRecord().getDiagnosis();
                    if (app.getMedicalRecord().getPrescription() != null) {
                        prescriptionData = app.getMedicalRecord().getPrescription().getMedicineList();
                    }
                }

                return AppointmentDto.builder()
                                .id(app.getId())
                                .date(app.getAppointmentTime().format(dateFormatter))
                                .time(app.getAppointmentTime().format(timeFormatter))
                                .doctorName(app.getDoctor().getFullName())
                                .specialtyName(app.getDoctor().getSpecialization() != null
                                                ? app.getDoctor().getSpecialization().getName()
                                                : "Đa khoa")
                                .patientName(app.getPatient().getFullName())
                                .phone(app.getPatient().getUser().getPhoneNumber())
                                .reason(app.getSymptoms() != null ? app.getSymptoms() : "Khám bệnh")
                                .symptoms(app.getSymptoms())
                                .status(app.getStatus().name().toLowerCase())
                                .diagnosis(diagnosisData)
                                .prescription(prescriptionData)
                                .price(priceData)
                                .build();
        }

        @Transactional
        public void cancelAppointment(Long id, String username) {
                Appointment appointment = appointmentRepository.findByIdAndPatientUserUsername(id, username)
                                .orElseThrow(() -> new RuntimeException("Lịch hẹn không tồn tại hoặc bạn không có quyền hủy."));

                if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
                        throw new RuntimeException("Không thể hủy lịch hẹn đã hoàn thành.");
                }

                appointment.setStatus(AppointmentStatus.CANCELED);
                appointmentRepository.save(appointment);
        }

        @Transactional
        public void rescheduleAppointment(Long id, String username, BookingRequestDto request) {
                Appointment appointment = appointmentRepository.findByIdAndPatientUserUsername(id, username)
                                .orElseThrow(() -> new RuntimeException("Lịch hẹn không tồn tại hoặc bạn không có quyền đổi lịch."));

                if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
                        throw new RuntimeException("Không thể đổi lịch hẹn đã hoàn thành.");
                }

                Doctor doctor = doctorRepository.findById(request.getDoctorId())
                                .orElseThrow(() -> new RuntimeException("Doctor not found"));

                LocalDateTime newTime = LocalDateTime.of(
                                LocalDate.parse(request.getAppointmentDate()),
                                LocalTime.parse(request.getAppointmentTime()));

                // Logic kiểm tra ca trực và trùng lịch (tương tự createAppointment)
                Shift requiredShift = determineShift(newTime.toLocalTime());
                List<WorkSchedule> schedules = workScheduleRepository.findByDoctorIdAndWorkDate(doctor.getId(),
                                newTime.toLocalDate());
                boolean isWorking = schedules.stream().anyMatch(s -> s.getShift() == requiredShift);

                if (!isWorking) {
                        // Tự động Add lịch làm việc 
                        WorkSchedule newSchedule = WorkSchedule.builder()
                                        .doctor(doctor)
                                        .workDate(newTime.toLocalDate())
                                        .shift(requiredShift)
                                        .build();
                        workScheduleRepository.save(newSchedule);
                }

                // Kiểm tra trùng lịch (loại trừ chính nó)
                List<Appointment> conflicts = appointmentRepository.findByDoctorIdAndAppointmentTimeBetweenOrderByAppointmentTimeAsc(
                                doctor.getId(), newTime, newTime);
                if (conflicts.stream().anyMatch(a -> !a.getId().equals(id))) {
                        throw new RuntimeException("Khung giờ này đã có người đặt.");
                }

                appointment.setDoctor(doctor);
                appointment.setAppointmentTime(newTime);
                appointment.setStatus(AppointmentStatus.PENDING); // Reset về chờ xác nhận khi đổi lịch
                appointmentRepository.save(appointment);
        }
}
