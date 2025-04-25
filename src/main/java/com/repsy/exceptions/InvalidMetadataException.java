package com.repsy.exceptions;

public class InvalidMetadataException extends RuntimeException{
    public InvalidMetadataException(String message){
        super(message);
    }
}
