package com.project.notes_v2.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends CustomException {
    public UnauthorizedException() {
        this.httpStatus = HttpStatus.UNAUTHORIZED;
        this.message = "Unauthorized Exception";
    }
    /*public UnauthorizedException() {
        this.prefix_1 = "UnauthorizedException - ";
        this.message = "Unauthorized session user";
    }
    public UnauthorizedException(String message) {
        super(message);
    }*/
}
