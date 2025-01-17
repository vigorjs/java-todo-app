package com.virgo.todoapp.utils.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;

public class Response {
    public static <T> ResponseEntity<?> renderJSON(T data, String message, HttpStatus httpStatus) {
        WebResponse<T> response = WebResponse.<T>builder()
                .message(message)
                .status(httpStatus.value())
                .data(data)
                .build();
        return ResponseEntity.status(httpStatus).body(response);
    }

    public static <T> ResponseEntity<?> renderJSON(T data, String message) {
        return renderJSON(data, message, HttpStatus.OK);
    }

    public static <T> ResponseEntity<?> renderJSON(T data) {
        return renderJSON(data, "Success");
    }

    public static <T> ResponseEntity<?> renderError(String message, HttpStatus httpStatus, String errors) {
        WebResponseError<T> response = WebResponseError.<T>builder()
                .message(message)
                .error(errors)
                .build();
        return ResponseEntity.status(httpStatus).body(response);
    }
}

