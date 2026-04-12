-- Dumping data for table medical_system_db.appointments: ~0 rows (approximately)
INSERT INTO `appointments` (`id`, `appointment_time`, `status`, `doctor_id`, `patient_id`, `symptoms`) VALUES
	(1, '2026-10-14 01:00:00.000000', 'PENDING', 1, 1, ''),
	(2, '2026-04-13 09:00:00.000000', 'PENDING', 1, 2, 'Sot cao'),
	(3, '2026-04-12 10:00:00.000000', 'PENDING', 1, 1, 'Sot'),
	(4, '2026-04-14 08:00:00.000000', 'PENDING', 1, 1, ''),
	(5, '2026-04-13 08:00:00.000000', 'PENDING', 1, 3, ''),
	(6, '2026-04-12 08:30:00.000000', 'PENDING', 1, 3, ''),
	(7, '2026-04-18 08:30:00.000000', 'PENDING', 1, 3, 'Bệnh nhân bị đau bụng cấp tính, cần bác sĩ kiểm tra gấp'),
	(8, '2026-04-18 08:30:00.000000', 'COMPLETED', 2, 3, 'Bệnh nhân bị đau bụng cấp tính, cần bác sĩ kiểm tra gấp'),
	(9, '2026-04-15 15:30:00.000000', 'COMPLETED', 2, 4, 'ho'),
	(10, '2026-04-12 15:30:00.000000', 'PENDING', 2, 3, '');

-- Dumping data for table medical_system_db.doctors: ~3 rows (approximately)
INSERT INTO `doctors` (`id`, `avatar_url`, `experience_years`, `full_name`, `specialization_id`, `user_id`, `leave_end_date`, `leave_start_date`, `achievements`, `biography`, `education`, `hospital`, `rating`, `review_count`) VALUES
	(1, '/uploads/avatars/6099b740-80c0-4418-8159-fe1bea6aaf4f.jpg', 6, 'BS. Nguyễn Trọng Khang', 1, 1, '2026-04-15', '2026-04-12', NULL, NULL, NULL, NULL, NULL, NULL),
	(2, '/uploads/avatars/32e7587e-4bc5-4711-a5be-33a5e17291a3.jpg', 2, 'BS. Văn Tấn Quý', 2, 7, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
	(3, '/uploads/avatars/33556bda-11d0-456c-8835-853f410375d8.jpg', 2, 'BS.Huỳnh Trung Hậu', 9, 12, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- Dumping data for table medical_system_db.doctor_certificates: ~0 rows (approximately)

-- Dumping data for table medical_system_db.medical_records: ~0 rows (approximately)
INSERT INTO `medical_records` (`id`, `diagnosis`, `notes`, `symptoms`, `appointment_id`) VALUES
	(1, 'sadsad', 'ádsad', '', 8),
	(2, 'ho', '', '', 9);

-- Dumping data for table medical_system_db.patients: ~0 rows (approximately)
INSERT INTO `patients` (`id`, `address`, `date_of_birth`, `full_name`, `gender`, `user_id`, `avatar_url`) VALUES
	(1, NULL, '2021-02-14', 'benhnhantest', NULL, 5, '/uploads/avatars/9ea4b1bd-8677-4a6e-9f59-0652d0e67974.jpg'),
	(2, NULL, NULL, 'user_test_2024', NULL, 8, NULL),
	(3, NULL, '2019-06-19', 'benhnhantest2', 'male', 9, '/uploads/avatars/6982c13a-4c78-48b7-9393-fc2b6b9126cd.jpg'),
	(4, NULL, NULL, 'Văn Tấn Quý', NULL, 10, NULL);

-- Dumping data for table medical_system_db.payments: ~0 rows (approximately)

-- Dumping data for table medical_system_db.prescriptions: ~0 rows (approximately)
INSERT INTO `prescriptions` (`id`, `medicine_list`, `prescribed_date`, `medical_record_id`) VALUES
	(1, '[{"name":"pad","quantity":5,"usage":"s s"},{"name":"sadas","quantity":1,"usage":"ád"},{"name":"ádsad","quantity":1,"usage":"sdasd"}]', '2026-04-12 02:46:01.238702', 1),
	(2, '[{"name":"âsdas","quantity":1,"usage":"đá"}]', '2026-04-12 02:58:49.701470', 2);

-- Dumping data for table medical_system_db.reviews: ~0 rows (approximately)

-- Dumping data for table medical_system_db.specializations: ~0 rows (approximately)
INSERT INTO `specializations` (`id`, `description`, `name`) VALUES
	(1, 'Khám chữa bệnh cho trẻ em', 'Nhi Khoa'),
	(2, 'Khám và điều trị các bệnh lý nội khoa chung', 'Nội Khoa'),
	(3, 'Khám và phẫu thuật các loại bệnh lý ngoại khoa', 'Ngoại Khoa'),
	(4, 'Chăm sóc sức khỏe phụ nữ và thai sản', 'Sản Phụ Khoa'),
	(5, 'Khám và điền trị bệnh lý tai, mũi, họng', 'Tai Mũi Họng'),
	(6, 'Chăm sóc sức khỏe răng miệng, nha khoa', 'Răng Hàm Mặt'),
	(7, 'Khám và điều trị các bệnh lý về mắt', 'Mắt (Nhãn Khoa)'),
	(8, 'Khám và điều trị các bệnh lý về da, tóc, móng', 'Da Liễu'),
	(9, 'Khám và điều trị các bệnh lý hệ thần kinh', 'Thần Kinh'),
	(10, 'Khám và điều trị các bệnh lý tim mạch, huyết áp', 'Tim Mạch'),
	(11, 'Khám và điều trị các bệnh lý tiêu hóa, dạ dày, gan mật', 'Tiêu Hóa - Gan Mật'),
	(12, 'Khám và điều trị các bệnh về cơ, xương, khớp', 'Cơ Xương Khớp');

-- Dumping data for table medical_system_db.users: ~10 rows (approximately)
INSERT INTO `users` (`id`, `created_at`, `email`, `enabled`, `password`, `phone_number`, `role`, `username`) VALUES
	(1, '2026-03-28 01:58:20.000000', 'khang@phongkham.com', b'1', '$2a$10$YlLwzohBNK4l4CZsR2htfeyTam/cL3o5L5Fu//a.N5EgVAT2DFC7e', '0901234567', 'DOCTOR', 'dr.khang'),
	(4, '2026-04-11 14:51:30.529939', 'testuser99@gmail.com', b'1', '$2a$10$AENL.C71o.G8HZld7XSzwOxHo1fawgOKaYjm7K.wuTFuPB3MQkWZu', NULL, 'USER', 'testuser99'),
	(5, '2026-04-11 14:52:12.863436', 'benhnhan1@gmail.com', b'1', '$2a$10$1AF5ZWTtr.IjIv50cmtPc.s9VAQqpB4e2xz84d7sJsUJrlTYKRnYa', '+84987670571', 'USER', 'benhnhantest'),
	(6, '2026-04-11 15:24:53.680628', 'test@example.com', b'1', '$2a$10$Y9Kpu9T9RI4dGT73YRmdQenWDmAl6ERCrVEzPO2NvyyliKDD7RIFy', NULL, 'USER', 'testuser123'),
	(7, '2026-04-12 00:37:34.523464', 'bacsi@example.com', b'1', '$2a$10$jomTtKaCocJgo1rz92XpjOno6SkMWux/g9cymKqIyZAlYKx7JyFRS', '0987654321', 'DOCTOR', 'bacsi'),
	(8, '2026-04-12 00:52:46.092880', 'user_test_2024@example.com', b'1', '$2a$10$Lse6gGE.VmDmGF01d0eIJ.m.VTARAcOt2BtW1WFMT/n94hqdh..GC', NULL, 'USER', 'user_test_2024'),
	(9, '2026-04-12 02:03:26.301759', 'benhnhan2@gmail.com', b'1', '$2a$10$XDhdrP8dXLh9K7/8TF7S8O6HJmP72Y20Ox2kLPBRevZ8SxF1DQs/.', '234324234324', 'USER', 'benhnhantest2'),
	(10, '2026-04-12 02:53:52.011929', 'quy@gmail.com', b'1', '$2a$10$1ngg1RYCOAwYhoi1QGlpTeaMmS5sGpAIiSvK7PPC1fa8Z5mPkiOuO', NULL, 'USER', 'Văn Tấn Quý'),
	(11, '2026-04-12 04:21:26.509571', 'admin@system.local', b'1', '$2a$10$AY6/IsECuowXUpqjjpD5hecWO993852tPLe9HAgK7BIJ/gWbnBdEG', '0000000000', 'ADMIN', 'admin'),
	(12, '2026-04-12 04:36:22.758012', 'hau@gmail.com', b'1', '$2a$10$uFKYCZ7kKWndd5yyfbnwqeP.o7txZqmK6vkg86VYib3yy14HIoWB2', '123456789', 'DOCTOR', 'Huỳnh Trung Hậu');

-- Dumping data for table medical_system_db.verification_token: ~2 rows (approximately)
INSERT INTO `verification_token` (`id`, `expiry_date`, `token`, `user_id`) VALUES
	(1, '2026-04-12 14:51:30.555399', '0135e4e6-4e82-4f4a-9af1-d2bd49cd8a4a', 4),
	(2, '2026-04-12 14:52:12.867524', '3b86ff43-8d3f-4b59-b7ff-fba5ee0ca0e6', 5),
	(3, '2026-04-12 15:24:53.721849', 'a0226210-0fc5-4ff0-b8d5-894a04276cd8', 6),
	(4, '2026-04-13 00:52:46.104679', '02367e35-8474-4d1d-bf95-a320133ad2a9', 8),
	(5, '2026-04-13 02:03:26.372304', 'c8434f87-dee0-4ca6-9590-366d703a9b80', 9),
	(6, '2026-04-13 02:53:52.015467', '9771fef8-b487-4561-b9d5-e0e290e513b5', 10);

-- Dumping data for table medical_system_db.work_schedules: ~0 rows (approximately)
INSERT INTO `work_schedules` (`id`, `shift`, `work_date`, `doctor_id`) VALUES
	(1, 'MORNING', '2026-10-14', 1),
	(2, 'MORNING', '2026-04-13', 1),
	(3, 'MORNING', '2026-04-12', 1),
	(4, 'MORNING', '2026-04-14', 1),
	(5, 'MORNING', '2026-04-18', 1),
	(6, 'MORNING', '2026-04-18', 2),
	(7, 'AFTERNOON', '2026-04-10', 1),
	(8, 'AFTERNOON', '2026-04-15', 2),
	(10, 'MORNING', '2026-04-17', 2),
	(11, 'AFTERNOON', '2026-04-17', 2),
	(12, 'AFTERNOON', '2026-04-12', 1),
	(13, 'MORNING', '2026-04-13', 2);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
