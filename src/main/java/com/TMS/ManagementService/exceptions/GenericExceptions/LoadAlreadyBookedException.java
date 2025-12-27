package com.TMS.ManagementService.exceptions.GenericExceptions;

public class LoadAlreadyBookedException extends RuntimeException{
    public LoadAlreadyBookedException(String message) {
        super(message);
    }
}
