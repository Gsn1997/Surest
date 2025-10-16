package com.surest_member_managemant.exception;

public class UnauthorizedException extends RuntimeException {


    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}