package com.medical.repository;

import com.medical.model.WorkSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Long> {
    List<WorkSchedule> findByDoctorIdAndWorkDateBetweenOrderByWorkDateAsc(Long doctorId, LocalDate startDate, LocalDate endDate);
    List<WorkSchedule> findByDoctorIdAndWorkDate(Long doctorId, LocalDate workDate);
    Optional<WorkSchedule> findByIdAndDoctorId(Long id, Long doctorId);
}
