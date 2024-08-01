package com.virgo.todoapp.utils.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.virgo.todoapp.entity.enums.TaskStatus;
import com.virgo.todoapp.entity.meta.Category;
import com.virgo.todoapp.utils.LocalDateTimeDeserializer;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskUpdateDTO {

    @Nullable
    @NotBlank(message = "title cant be Blank")
    private String title;

    @Nullable
    @NotBlank(message = "description cant be blank")
    private String description;

    @Nullable
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
//    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}$", message = "Invalid dueDate format")
    private String dueDate;

//
//    private Date deadline;

    @Nullable
    private TaskStatus status;
//
//
//    private Integer category;

}
