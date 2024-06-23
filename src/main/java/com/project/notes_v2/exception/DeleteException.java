package com.project.notes_v2.exception;

import org.springframework.http.HttpStatus;

public class DeleteException extends CustomException {
    public DeleteException() {
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.message = "Delete Exception";
    }
}
