package com.company.accessManagement.exception;

public class DuplicateRequestException extends RuntimeException{

    public DuplicateRequestException(){
        super("You already have a pending request.");
    }

}