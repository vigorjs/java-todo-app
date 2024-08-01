package com.virgo.todoapp.utils.response;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebResponseError<T> {
    private String error;
    private String message;
}