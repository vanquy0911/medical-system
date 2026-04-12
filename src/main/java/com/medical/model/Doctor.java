package com.medical.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "doctors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialization_id")
    private Specialization specialization;

    @Column(nullable = false)
    private String fullName;

    private int experienceYears;

    private String avatarUrl;

    @Column(columnDefinition = "TEXT")
    private String biography;

    @Column(columnDefinition = "TEXT")
    private String education;

    @Column(columnDefinition = "TEXT")
    private String achievements;

    private String hospital;

    private Double rating;

    private Integer reviewCount;

    @Column(name = "leave_start_date")
    private LocalDate leaveStartDate;

    @Column(name = "leave_end_date")
    private LocalDate leaveEndDate;

    @ElementCollection
    @CollectionTable(name = "doctor_certificates", joinColumns = @JoinColumn(name = "doctor_id"))
    @Column(name = "certificate_url")
    private java.util.List<String> certificateUrls = new java.util.ArrayList<>();
}
