package com.medical.service;

import com.medical.dto.AdminDashboardResponse;
import com.medical.dto.UserResponseDTO;
import com.medical.model.Role;

import java.util.List;

public interface AdminService {
    AdminDashboardResponse getDashboardStats();
    List<UserResponseDTO> getAllUsers();
    void toggleUserStatus(Long userId);
    void changeUserRole(Long userId, Role newRole);
}
