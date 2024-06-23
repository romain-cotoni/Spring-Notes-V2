package com.project.notes_v2.exception;

import org.springframework.http.HttpStatus;

public class FailedRequestException extends CustomException {

    public FailedRequestException() {
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.message = "Failed Request Exception";
    }

    public FailedRequestException(String msg) {
        this.message = msg;
    }

}
