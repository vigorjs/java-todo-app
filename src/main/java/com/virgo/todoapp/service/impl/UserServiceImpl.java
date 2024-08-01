package com.virgo.todoapp.service.impl;

import com.virgo.todoapp.config.advisers.exception.NotFoundException;
import com.virgo.todoapp.entity.enums.Role;
import com.virgo.todoapp.entity.meta.User;
import com.virgo.todoapp.repo.UserRepository;
import com.virgo.todoapp.service.UserService;
import com.virgo.todoapp.utils.dto.ChangeRoleDTO;
import com.virgo.todoapp.utils.dto.RegisterRequestDTO;
import com.virgo.todoapp.utils.dto.RegisterResponseDTO;
import com.virgo.todoapp.utils.dto.UserResponseDTO;
import com.virgo.todoapp.utils.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<UserResponseDTO> getAll(Pageable pageable, String name) {
        Specification<User> spec = UserSpecification.getSpecification(name);
        return userRepository.findAll(spec, pageable).map(this::convertToDTO);
    }

    @Override
    public UserResponseDTO getById(Integer id) {
        return convertToDTO(userRepository.findById(id).orElseThrow( () -> new NotFoundException("User not Found") ));
    }

    @Override
    public UserResponseDTO changeUserRole(Integer id, Role role) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not Found"));
        user.setRole(role);
        userRepository.save(user);
        return convertToDTO(user);
    }

    @Override
    public RegisterResponseDTO createSU(RegisterRequestDTO request) {
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .address(request.getAddress())
                .mobileNumber(request.getMobileNumber())
                .gender(request.getGender() != null ? request.getGender() : null)
                .role(Role.SUPER_ADMIN)
                .build();
        var savedUser = userRepository.save(user);
//        var jwtToken = jwtService.generateToken(user);
//        var refreshToken = jwtService.generateRefreshToken(user);
//        saveUserToken(savedUser, jwtToken);
        return RegisterResponseDTO.builder()
//                .id(savedUser.getId().toString())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .build();
    }

    public UserResponseDTO convertToDTO(User user){
        return UserResponseDTO.builder()
                .id(user.getId().toString())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
