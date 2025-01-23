package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handlerException(final Exception e) {
        ErrorResponse response = new ErrorResponse("Unknown exception", e.getMessage());
        ResponseEntity<ErrorResponse> result = new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);

        if (e instanceof ValidationException) {
            response = new ErrorResponse("Validation exception", e.getMessage());
            result = new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

        } else if (e instanceof NotFoundException) {
            response = new ErrorResponse("Not found exception", e.getMessage());
            result = new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return result;
    }

}
