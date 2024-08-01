package com.virgo.todoapp.controller;

import com.virgo.todoapp.entity.enums.Role;
import com.virgo.todoapp.service.AuthenticationService;
import com.virgo.todoapp.service.UserService;
import com.virgo.todoapp.utils.dto.ChangeRoleDTO;
import com.virgo.todoapp.utils.dto.RegisterRequestDTO;
import com.virgo.todoapp.utils.dto.RegisterResponseDTO;
import com.virgo.todoapp.utils.dto.UserResponseDTO;
import com.virgo.todoapp.utils.response.UserPaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@RestControllerAdvice
@Tag(name = "User", description = "User management APIs")
public class UserController {

    private final UserService userService;


    @GetMapping("/admin/users")
    public UserPaginationResponse<?> findAll(@PageableDefault Pageable pageable, @RequestParam(required = false) String name) {
        return new UserPaginationResponse<>(userService.getAll(pageable, name));
    }

    @GetMapping("/admin/users/{id}")
    public UserResponseDTO findById(@PathVariable Integer id) {
        return userService.getById(id);
    }

    @PatchMapping("/admin/users/{id}/role")
//    @Secured("ROLE_SUPER_ADMIN")
    public UserResponseDTO changeUserRole(
            @PathVariable Integer id,
            @RequestBody @Valid ChangeRoleDTO request
    ) {
        return userService.changeUserRole(id, Role.valueOf(request.getRole()));
    }

    @PostMapping("/admin/super-admin")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponseDTO register(@RequestBody @Valid RegisterRequestDTO request) {
        return userService.createSU(request);
    }
}
