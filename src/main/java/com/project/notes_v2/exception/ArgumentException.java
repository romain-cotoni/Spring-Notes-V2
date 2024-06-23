package com.project.notes_v2.exception;

import org.springframework.http.HttpStatus;

public class ArgumentException extends CustomException {
    public ArgumentException() {
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.message = "Argument Exception";
    }
}
