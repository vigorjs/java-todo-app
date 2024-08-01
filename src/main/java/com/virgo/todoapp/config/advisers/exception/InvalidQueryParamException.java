package com.virgo.todoapp.config.advisers.exception;

public class InvalidQueryParamException extends RuntimeException {
    public InvalidQueryParamException(String message) {
        super(message);
    }
}