package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Value;
import ru.yandex.practicum.filmorate.validator.NotSpaces;

import java.time.LocalDate;

@Value
@Builder(toBuilder = true)
public class User {

    Long id;

    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Электронная почта должна соответствовать шаблону name@domain.xx")
    String email;

    @NotBlank(message = "Логин не может быть пустым")
    @NotSpaces
    String login;

    String name;

    @PastOrPresent(message = "Дата рождения должна быть не позже текущего момента")
    LocalDate birthday;
}
