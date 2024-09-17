package com.project.notes_v2.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    /*
     * 200 OK: The request was successful.
     * 201 Created: A new resource has been created successfully.
     * 400 Bad Request: The request was malformed.
     * 401 Unauthorized: the client must authenticate itself to get the response.
     * 403 Forbidden: If the user does not have permission to share the note.
     * 404 Not Found: The requested resource does not exist.
     * 409 Conflict: conflict with the current state of the target resource.
     * 500 Internal Server Error: An unexpected error occurred.
     */


    /**
     * Validator failed on entity field exception
     * @param exception MethodArgumentNotValidException
     * @return HTTP status bad request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach( error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }


    /**
     * @param exception ArgumentException
     * @return HTTP STATUS CODE 400 Bad Request
     */
    @ExceptionHandler(ArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleArgumentException(ArgumentException exception) {
        return setResponseEntity(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    /**
     * @param exception AssociationException
     * @return HTTP STATUS CODE 409 Conflict
     */
    @ExceptionHandler(AssociationException.class)
    public ResponseEntity<Map<String, Object>> handleAssociationException(AssociationException exception) {
        return setResponseEntity(HttpStatus.CONFLICT, exception.getMessage());
    }

    /**
     * @param exception AlreadyExistException
     * @return HTTP STATUS CODE 409 Conflict
     */
    @ExceptionHandler(AlreadyExistException.class)
    public ResponseEntity<Map<String, Object>> handleAlreadyExistException(AlreadyExistException exception) {
        return setResponseEntity(HttpStatus.CONFLICT, exception.getMessage());
    }

    /**
     * @param exception DeleteException
     * @return HTTP STATUS CODE 500 Internal Server Error
     */
    @ExceptionHandler(DeleteException.class)
    public ResponseEntity<Map<String, Object>> handleDeleteException(DeleteException exception) {
        return setResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    /**
     * @param exception FailedRequestException
     * @return HTTP STATUS CODE 500 Internal Server Error
     */
    @ExceptionHandler(FailedRequestException.class)
    public ResponseEntity<Map<String, Object>> handleFailedRequestException(FailedRequestException exception) {
        return setResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    /**
     * @param exception NotFoundException
     * @return HTTP STATUS CODE 404 NOT_FOUND
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFoundException(NotFoundException exception) {
        return setResponseEntity(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    /**
     * @param exception SaveException
     * @return HTTP STATUS CODE 500 Internal Server Error
     */
    @ExceptionHandler(SaveException.class)
    public ResponseEntity<Map<String, Object>> handleSaveException(SaveException exception) {
        return setResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    /**
     * @param exception ShareException
     * @return HTTP STATUS CODE 409 Conflict
     */
    @ExceptionHandler(ShareException.class)
    public ResponseEntity<Map<String, Object>> handleShareException(ShareException exception) {
        return setResponseEntity(HttpStatus.CONFLICT, exception.getMessage());
    }

    /**
     * @param exception UnauthenticatedException
     * @return HTTP STATUS CODE 401 Unauthorized
     */
    @ExceptionHandler(UnauthenticatedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthenticatedException(UnauthenticatedException exception) {
        return setResponseEntity(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    /**
     * @param exception UnauthorizedException
     * @return HTTP STATUS CODE 401 Unauthorized
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedException(UnauthorizedException exception) {
        return setResponseEntity(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    /**
     * @param exception UpdateException
     * @return HTTP STATUS CODE 500 Internal Server Error
     */
    @ExceptionHandler(UpdateException.class)
    public ResponseEntity<Map<String, Object>> handleUpdateException(UpdateException exception) {
        return setResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    private ResponseEntity<Map<String, Object>> setResponseEntity(HttpStatus httpStatus, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", httpStatus.value());
        response.put("message", message);
        return ResponseEntity.status(httpStatus).body(response);
    }

}
