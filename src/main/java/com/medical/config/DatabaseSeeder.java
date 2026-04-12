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
        
        // Seed a Super Admin account
        User adminUser = userRepository.findByUsername("admin").orElse(null);
        if (adminUser == null) {
            adminUser = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("123"))
                .email("admin@system.local")
                .phoneNumber("0000000000")
                .role(Role.ADMIN)
                .enabled(true)
                .build();
            userRepository.save(adminUser);
        }

        // Seed a test Doctor account
        User doctorUser = userRepository.findByUsername("bacsi").orElse(null);
        if (doctorUser == null) {
            doctorUser = User.builder()
                .username("bacsi")
                .password(passwordEncoder.encode("123"))
                .email("bacsi@example.com")
                .phoneNumber("0987654321")
                .role(Role.DOCTOR)
                .enabled(true)
                .build();
            doctorUser = userRepository.save(doctorUser);
        }

        // If the doctor user exists but doesn't have a record in doctors table, create it
        if (doctorRepository.findByUserUsername("bacsi").isEmpty()) {
            Specialization noiKhoa = specializationRepository.findByName("Nội Khoa").orElse(null);
            
            Doctor newDoctor = Doctor.builder()
                .user(doctorUser)
                .fullName("ThS. BS. Nguyễn Trọng Khang")
                .specialization(noiKhoa)
                .experienceYears(12)
                .hospital("Cơ sở Quận 1 - Phòng Khám Xanh")
                .biography("Bác sĩ Nguyễn Trọng Khang là chuyên gia hàng đầu trong lĩnh vực Nội khoa với hơn 12 năm kinh nghiệm lâm sàng. Ông từng giữ vị trí quan trọng tại các bệnh viện lớn và có nhiều công trình nghiên cứu về quản lý bệnh lý mãn tính.\n\nPhương châm làm việc của ông là 'Chữa bệnh từ tâm', luôn lắng nghe và đặt sự an tâm của bệnh nhân lên hàng đầu.")
                .education("Thạc sĩ Y khoa - Đại học Y Dược TP.HCM\nTu nghiệp chuyên sâu về Nội tổng quát tại Bệnh viện Bạch Mai.")
                .achievements("Bằng khen 'Bác sĩ trẻ tiêu biểu' năm 2020\nChứng chỉ thành viên Hội Nội khoa Việt Nam.")
                .rating(4.9)
                .reviewCount(156)
                .build();
            doctorRepository.save(newDoctor);

            // Add another doctor for variety
            User doctor2User = userRepository.findByUsername("giangvien").orElse(null);
            if (doctor2User != null) {
                 Specialization phuKhoa = specializationRepository.findByName("Sản - Phụ Khoa").orElse(null);
                 Doctor dr2 = Doctor.builder()
                        .user(doctor2User)
                        .fullName("BS. CKI Lê Thị Như Quỳnh")
                        .specialization(phuKhoa)
                        .experienceYears(15)
                        .hospital("Cơ sở Quận 7 - Phòng Khám Xanh")
                        .biography("Bác sĩ Lê Thị Như Quỳnh là chuyên gia trong lĩnh vực Sản phụ khoa với khả năng thấu hiểu tâm lý bệnh nhân tuyệt vời. Bà chuyên điều trị các bệnh lý phụ khoa phức tạp và tư vấn sức khỏe sinh sản tiền hôn nhân.")
                        .education("Bác sĩ Chuyên khoa I - Đại học Y Hà Nội\nChứng chỉ Siêu âm Sản phụ khoa chuyên sâu.")
                        .rating(5.0)
                        .reviewCount(230)
                        .build();
                 doctorRepository.save(dr2);
            }
        }
    }
}
