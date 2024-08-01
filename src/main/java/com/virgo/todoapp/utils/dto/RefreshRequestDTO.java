package com.virgo.todoapp.utils.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshRequestDTO {
    @NotNull(message = "refresh token cant be nulllllllosssssssssss")
    @NotBlank(message = "refresh token cant be nulllllllosssssssssss")
    @NotEmpty(message = "refresh token cant be nulllllllosssssssssss")
    private String refreshToken;
}
