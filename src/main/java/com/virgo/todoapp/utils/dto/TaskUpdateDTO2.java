package com.virgo.todoapp.utils.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskUpdateDTO2 {
    @NotBlank(message = "status not blank")
    @NotNull(message = "status not null")
    @Pattern(regexp = "IN_PROGRESS|PENDING|COMPLETED", message = "Invalid status")
    private String Status;
}
