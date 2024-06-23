package com.project.notes_v2.exception;

import org.springframework.http.HttpStatus;

public class UpdateException extends CustomException {
    public UpdateException() {
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.message = "Update Exception";
    }
}
