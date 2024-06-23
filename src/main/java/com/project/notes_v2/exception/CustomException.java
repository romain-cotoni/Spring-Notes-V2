package com.project.notes_v2.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class CustomException extends RuntimeException {
    protected String message;
    protected HttpStatus httpStatus;
    protected int id1;
    protected int id2;

    /*protected String prefix_1;
    protected String prefix_2;
    protected String message;
    protected HttpStatus httpStatus;

    public CustomException() {

    }

    public CustomException(String message) {
        super(message);
    }

    public CustomException(Integer accountId, Integer noteId) {
        this.message = this.prefix_1 +
                       this.prefix_2 +
                       "error between Account id " +
                       accountId +
                       " and Note id " +
                       noteId;

    }*/
}
