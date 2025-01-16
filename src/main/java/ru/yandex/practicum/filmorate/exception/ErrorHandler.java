package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerValidationException(final ValidationException e) {
        return new ErrorResponse("Validation exception", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlerNotFoundException(final NotFoundException e) {
        return new ErrorResponse("Not found exception", e.getMessage());
    }
}
