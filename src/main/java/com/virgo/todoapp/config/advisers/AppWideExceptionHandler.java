package com.virgo.todoapp.config.advisers;

import com.virgo.todoapp.config.advisers.exception.*;
import com.virgo.todoapp.utils.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.nio.file.AccessDeniedException;
import java.util.Map;

@RestControllerAdvice
@CrossOrigin
public class AppWideExceptionHandler {

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<?> handleConflictException(ConflictException e) {
        return Response.renderError("Conflict", HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(InvalidQueryParamException.class)
    public ResponseEntity<?> handleInvalidQueryParamException(InvalidQueryParamException ex) {
        return Response.renderError("Conflict", HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(NotFoundException e) {
        // Add logging
        System.err.println("NotFoundException: " + e.getMessage());
        return Response.renderError("Not Found", HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(ValidateException.class)
    public ResponseEntity<?> handleValidationException(ValidateException e) {
        // Add logging
        System.err.println("ValidateException: " + e.getMessage());
        return Response.renderError("Bad Request", HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException e) {
        // Add logging
        System.err.println("AuthenticationException: " + e.getMessage());
        return Response.renderError("Unauthorized", HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e) {
        // Add logging
        System.err.println("AccessDeniedException: " + e.getMessage());
        return Response.renderError("Forbidden", HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Add logging
        System.err.println("MethodArgumentNotValidException: " + ex.getMessage());
        return Response.renderError("Bad Request", HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        // Add logging
        System.err.println("HttpMessageNotReadableException: " + ex.getMessage());
        return Response.renderError("Bad Request", HttpStatus.BAD_REQUEST, ex.getMessage());
    }
}
