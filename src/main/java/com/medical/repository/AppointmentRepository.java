package com.medical.repository;

import com.medical.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctorIdAndAppointmentTimeBetweenOrderByAppointmentTimeAsc(Long doctorId,
            LocalDateTime startOfDay, LocalDateTime endOfDay);

    Optional<Appointment> findByIdAndDoctorId(Long id, Long doctorId);
}
