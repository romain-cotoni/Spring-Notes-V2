package com.project.notes_v2.exception;

import org.springframework.http.HttpStatus;

import java.io.Serial;

public class AlreadyExistException extends CustomException {

    @Serial
    private static final long serialVersionUID = -1298050199827901419L;

    public AlreadyExistException(String field) {
        super();
        this.httpStatus = HttpStatus.CONFLICT;
        this.message = "AlreadyExistException: " + field + " already exist";
    }
}
