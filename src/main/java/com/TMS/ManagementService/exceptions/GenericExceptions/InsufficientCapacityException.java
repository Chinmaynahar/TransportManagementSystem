package com.TMS.ManagementService.exceptions.GenericExceptions;

public class InsufficientCapacityException extends RuntimeException{
    public InsufficientCapacityException(String message) {
        super(message);
    }
}
