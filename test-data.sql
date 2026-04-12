-- Hướng dẫn: Chạy các lệnh này TRONG HEIDISQL/PHPMyAdmin SAU KHI bật Server Spring Boot lần đầu tiên 
-- (để Spring tự tạo các bảng rỗng trước).

-- SỬA LỖI ENUM (CHẠY 3 DÒNG NÀY TRƯỚC)
ALTER TABLE users MODIFY COLUMN role VARCHAR(50);
ALTER TABLE payments MODIFY COLUMN method VARCHAR(50);
ALTER TABLE payments MODIFY COLUMN status VARCHAR(50);

-- 1. Giả sử mật khẩu là 'khang123' đã mã hóa BCrypt (mã hóa chuẩn Spring Security)
INSERT INTO users (username, password, email, phone_number, role, enabled, created_at) 
VALUES ('dr.khang', '$2a$10$wY96OqXYM7vB/9kIhYh3H.gDq3B.9ySjO.R6h1g/t/Vf8iGk0RzOq', 'khang@phongkham.com', '0901234567', 'DOCTOR', 1, NOW());

-- 2. Thêm chuyên khoa
INSERT INTO specializations (name, description) VALUES ('Nhi Khoa', 'Khám chữa bệnh cho trẻ em');

-- 3. Liên kết User vừa tạo sang bảng Doctor (Chú ý user_id và specialization_id phải khớp)
INSERT INTO doctors (user_id, specialization_id, full_name, experience_years, avatar_url)
VALUES (
    (SELECT id FROM users WHERE username = 'dr.khang'), 
    (SELECT id FROM specializations WHERE name = 'Nhi Khoa'),
    'BS. Nguyễn Trọng Khang',
    5,
    'https://ui-avatars.com/api/?name=Khang&background=1E6BFF&color=fff'
);
