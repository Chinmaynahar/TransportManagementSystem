package com.TMS.ManagementService.exceptions.GenericExceptions;

public class InvalidStatusTransistionException extends RuntimeException{
      public InvalidStatusTransistionException(String message){
          super(message);
      }
}
