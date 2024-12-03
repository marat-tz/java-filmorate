package ru.yandex.practicum.filmorate.exception;

import lombok.Value;

@Value
public class ErrorResponse {
    String error;
    String description;
}
