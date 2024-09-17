package com.project.notes_v2.exception;

import org.springframework.http.HttpStatus;

import java.io.Serial;

public class UnauthorizedException extends CustomException {
    @Serial
    private static final long serialVersionUID = -7867152412294957272L;

    public UnauthorizedException() {
        this.httpStatus = HttpStatus.UNAUTHORIZED;
        this.message = "Unauthorized Exception";
    }
}
