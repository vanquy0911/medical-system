package com.medical.service;

import com.medical.dto.WorkScheduleDto;
import com.medical.model.Doctor;
import com.medical.model.Shift;
import com.medical.model.WorkSchedule;
import com.medical.repository.DoctorRepository;
import com.medical.repository.WorkScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkScheduleService {

    private final WorkScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;

    @Transactional(readOnly = true)
    public List<WorkScheduleDto> getSchedulesByDate(String username, LocalDate date) {
        Doctor doctor = doctorRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));
        
        List<WorkSchedule> schedules = scheduleRepository.findByDoctorIdAndWorkDate(doctor.getId(), date);
        
        return schedules.stream().map(s -> WorkScheduleDto.builder()
                .id(s.getId())
                .time(s.getShift() == Shift.MORNING ? "08:00 - 12:00" : "13:00 - 17:00")
                .maxPatients(10) // Fixed max slots for mock mapping
                .status("Còn trống") // Fixed status for mock mapping
                .build()).collect(Collectors.toList());
    }

    @Transactional
    public WorkScheduleDto createSchedule(String username, LocalDate date, Shift shift) {
        Doctor doctor = doctorRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));
        
        // Kiểm tra trùng lắp
        boolean exists = scheduleRepository.findByDoctorIdAndWorkDate(doctor.getId(), date)
                .stream().anyMatch(s -> s.getShift() == shift);
        
        if (exists) {
            throw new RuntimeException("Schedule already exists for this shift");
        }

        WorkSchedule schedule = WorkSchedule.builder()
                .doctor(doctor)
                .workDate(date)
                .shift(shift)
                .build();
        
        schedule = scheduleRepository.save(schedule);
        
        return WorkScheduleDto.builder()
                .id(schedule.getId())
                .time(shift == Shift.MORNING ? "08:00 - 12:00" : "13:00 - 17:00")
                .maxPatients(10)
                .status("Còn trống")
                .build();
    }

    @Transactional
    public void deleteSchedule(String username, Long scheduleId) {
        Doctor doctor = doctorRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        
        WorkSchedule schedule = scheduleRepository.findByIdAndDoctorId(scheduleId, doctor.getId())
                .orElseThrow(() -> new RuntimeException("Schedule not found or unauthorized"));
        
        scheduleRepository.delete(schedule);
    }
}
