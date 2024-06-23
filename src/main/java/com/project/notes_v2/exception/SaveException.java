package com.project.notes_v2.exception;

import org.springframework.http.HttpStatus;

public class SaveException extends CustomException {
    public SaveException() {
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.message = "Repository Save Exception";
    }
}
