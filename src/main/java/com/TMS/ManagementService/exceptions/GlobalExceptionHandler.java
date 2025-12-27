package com.TMS.ManagementService.exceptions;


import com.TMS.ManagementService.exceptions.GenericExceptions.InsufficientCapacityException;
import com.TMS.ManagementService.exceptions.GenericExceptions.InvalidStatusTransistionException;
import com.TMS.ManagementService.exceptions.GenericExceptions.LoadAlreadyBookedException;
import com.TMS.ManagementService.exceptions.GenericExceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.Map;

//Handles customExceptions
@ControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Object> build(HttpStatus status, String message) {
        return new ResponseEntity<>(
                Map.of(
                        "timestamp", Instant.now().toString(),
                        "status", status.value(),
                        "error", status.getReasonPhrase(),
                        "message", message
                ),
                status
        );
    }
    @ExceptionHandler(LoadAlreadyBookedException.class)
    public ResponseEntity<Object> handle(LoadAlreadyBookedException ex){
        return build(HttpStatus.BAD_REQUEST,ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handle(ResourceNotFoundException ex){
        return build(HttpStatus.NOT_FOUND,ex.getMessage());
    }

    @ExceptionHandler(InvalidStatusTransistionException.class)
    public ResponseEntity<Object> handle(InvalidStatusTransistionException ex){
        return build(HttpStatus.BAD_REQUEST,ex.getMessage());
    }

    @ExceptionHandler(InsufficientCapacityException.class)
    public ResponseEntity<Object> handle(InsufficientCapacityException ex){
        return build(HttpStatus.BAD_REQUEST,ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handle(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handle(MethodArgumentNotValidException ex){
        return build(HttpStatus.BAD_REQUEST,ex.getMessage());
    }
}