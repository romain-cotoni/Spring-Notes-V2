package com.project.notes_v2.exception;

import org.springframework.http.HttpStatus;

public class UnauthenticatedException extends CustomException {
    public UnauthenticatedException() {
        this.httpStatus = HttpStatus.UNAUTHORIZED;
        this.message = "Unauthenticated Exception";
    }
    /*public UnauthenticatedException() {
        this.prefix_1 = "UnauthenticatedException - ";
        this.message = "Unauthenticated session user";
    }
    public UnauthenticatedException(String message) {
        super(message);
    }*/
}
