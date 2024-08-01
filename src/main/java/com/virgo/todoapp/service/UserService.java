package com.virgo.todoapp.service;

import com.virgo.todoapp.entity.enums.Role;
import com.virgo.todoapp.utils.dto.ChangeRoleDTO;
import com.virgo.todoapp.utils.dto.RegisterRequestDTO;
import com.virgo.todoapp.utils.dto.RegisterResponseDTO;
import com.virgo.todoapp.utils.dto.UserResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserResponseDTO> getAll(Pageable pageable, String name);
    UserResponseDTO getById(Integer id);
    UserResponseDTO changeUserRole(Integer id, Role role);
    RegisterResponseDTO createSU(RegisterRequestDTO request);
}
