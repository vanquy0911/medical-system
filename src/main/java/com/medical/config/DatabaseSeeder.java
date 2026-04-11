package com.medical.config;

import com.medical.model.Specialization;
import com.medical.model.User;
import com.medical.model.Doctor;
import com.medical.model.Role;
import com.medical.repository.SpecializationRepository;
import com.medical.repository.UserRepository;
import com.medical.repository.DoctorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final SpecializationRepository specializationRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (specializationRepository.count() <= 1) {
            List<Specialization> specializations = Arrays.asList(
                    Specialization.builder()
                            .name("Nội Khoa")
                            .description("Khám và điều trị các bệnh lý nội khoa chung")
                            .build(),
                    Specialization.builder()
                            .name("Ngoại Khoa")
                            .description("Khám và phẫu thuật các loại bệnh lý ngoại khoa")
                            .build(),
                    Specialization.builder()
                            .name("Sản Phụ Khoa")
                            .description("Chăm sóc sức khỏe phụ nữ và thai sản")
                            .build(),
                    Specialization.builder()
                            .name("Tai Mũi Họng")
                            .description("Khám và điền trị bệnh lý tai, mũi, họng")
                            .build(),
                    Specialization.builder()
                            .name("Răng Hàm Mặt")
                            .description("Chăm sóc sức khỏe răng miệng, nha khoa")
                            .build(),
                    Specialization.builder()
                            .name("Mắt (Nhãn Khoa)")
                            .description("Khám và điều trị các bệnh lý về mắt")
                            .build(),
                    Specialization.builder()
                            .name("Da Liễu")
                            .description("Khám và điều trị các bệnh lý về da, tóc, móng")
                            .build(),
                    Specialization.builder()
                            .name("Thần Kinh")
                            .description("Khám và điều trị các bệnh lý hệ thần kinh")
                            .build(),
                    Specialization.builder()
                            .name("Tim Mạch")
                            .description("Khám và điều trị các bệnh lý tim mạch, huyết áp")
                            .build(),
                    Specialization.builder()
                            .name("Tiêu Hóa - Gan Mật")
                            .description("Khám và điều trị các bệnh lý tiêu hóa, dạ dày, gan mật")
                            .build(),
                    Specialization.builder()
                            .name("Cơ Xương Khớp")
                            .description("Khám và điều trị các bệnh về cơ, xương, khớp")
                            .build()
            );

            // Save only if they don't already exist by name
            for (Specialization spec : specializations) {
                if (specializationRepository.findByName(spec.getName()).isEmpty()) {
                    specializationRepository.save(spec);
                }
            }
        }
        
        // Seed a test Doctor account
        if (!userRepository.existsByUsername("bacsi")) {
            User doctorUser = User.builder()
                .username("bacsi")
                .password(passwordEncoder.encode("123"))
                .email("bacsi@example.com")
                .phoneNumber("0987654321")
                .role(Role.DOCTOR)
                .enabled(true)
                .build();
            userRepository.save(doctorUser);
            
            Specialization noiKhoa = specializationRepository.findByName("Nội Khoa").orElse(null);
            
            Doctor newDoctor = Doctor.builder()
                .user(doctorUser)
                .specialization(noiKhoa)
                .build();
            doctorRepository.save(newDoctor);
        }
    }
}
