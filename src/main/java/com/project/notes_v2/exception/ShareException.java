package com.project.notes_v2.exception;

import org.springframework.http.HttpStatus;

public class ShareException extends CustomException {
    public ShareException() {
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.message = "Share Exception";
    }

    /*public ShareException() {
        this.prefix_1 = "ShareException - ";
        this.message = "Share session user";
    }
    public ShareException(String message) {
        super(message);
    }*/
}
