package com.glucode.taskmanager.exception;

import com.glucode.taskmanager.task.TaskNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.glucode.taskmanager.weather.WeatherApiException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(TaskNotFoundException.class)
        public ResponseEntity<ApiError> handleTaskNotFound(
                        TaskNotFoundException exception) {
                return buildResponse(
                                HttpStatus.NOT_FOUND,
                                exception.getMessage());
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiError> handleValidationErrors(
                        MethodArgumentNotValidException exception) {
                String message = exception.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(error -> error.getDefaultMessage())
                                .findFirst()
                                .orElse("Invalid request.");

                return buildResponse(HttpStatus.BAD_REQUEST, message);
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ApiError> handleIllegalArgument(
                        IllegalArgumentException exception) {
                return buildResponse(
                                HttpStatus.BAD_REQUEST,
                                exception.getMessage());
        }

        private ResponseEntity<ApiError> buildResponse(
                        HttpStatus status,
                        String message) {
                ApiError error = new ApiError(
                                Instant.now(),
                                status.value(),
                                status.getReasonPhrase(),
                                message);

                return ResponseEntity.status(status).body(error);
        }

        @ExceptionHandler(WeatherApiException.class)
        public ResponseEntity<ApiError> handleWeatherApiException(
                        WeatherApiException exception) {

                return buildResponse(
                                HttpStatus.SERVICE_UNAVAILABLE,
                                exception.getMessage());
        }
}