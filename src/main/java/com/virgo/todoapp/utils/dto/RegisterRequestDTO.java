package com.virgo.todoapp.utils.dto;

import com.virgo.todoapp.entity.enums.Gender;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDTO {

    @NotBlank
    @NotNull
    @NotEmpty
    private String username;

    @NotBlank
    @NotNull
    @NotEmpty
    @Email
    private String email;

    @NotBlank
    @NotNull
    @NotEmpty
    @Length(min = 6, message = "password min 6 char")
    private String password;

    //disabled role request, sudah dihandle di service
//    private Role role;
    @Nullable
    private Gender gender;
    @Nullable
    private String address;
    @Nullable
    private String mobileNumber;

}
