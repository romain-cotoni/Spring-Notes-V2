package com.project.notes_v2.exception;

import org.springframework.http.HttpStatus;

public class AssociationException extends CustomException {

    public AssociationException() {
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.message = "Association Exception";
    }
}
