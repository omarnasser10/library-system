package com.library.exception;

import com.library.dto.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(
            EmailAlreadyExistsException ex) {

        ErrorResponse response=new ErrorResponse(ex.getMessage(),409, LocalDateTime.now());
        return new ResponseEntity<>(
                response,
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(
            InvalidCredentialsException ex){

        ErrorResponse response=new ErrorResponse(ex.getMessage(),401, LocalDateTime.now());
        return new ResponseEntity<>(
                response,
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {

            errors.put(
                    error.getField(),
                    error.getDefaultMessage()
            );
        });

        return new ResponseEntity<>(
                errors,
                HttpStatus.BAD_REQUEST
        );
    }
}
