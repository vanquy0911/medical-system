package com.medical.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prescriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_record_id", unique = true)
    private MedicalRecord medicalRecord;

    @Column(nullable = false)
    private LocalDateTime prescribedDate;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String medicineList;

    @PrePersist
    protected void onCreate() {
        prescribedDate = LocalDateTime.now();
    }
}
