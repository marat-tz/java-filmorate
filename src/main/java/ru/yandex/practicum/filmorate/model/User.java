package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.validator.NotSpaces;

import java.time.LocalDate;

@Value
@Builder(toBuilder = true)
public class User {
    @NotNull(message = "Id должен быть указан")
    Long id;

    @NotNull(message = "Электронная почта не может быть пустой")
    @Email(message = "Электронная почта должна соответствовать шаблону name@domain.xx")
    String email;

    @NotNull(message = "Логин не может быть пустым")
    @NotSpaces
    String login;

    String name;

    @PastOrPresent(message = "Дата рождения должна быть не позже текущего момента")
    LocalDate birthday;
}