package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NegativeOrZero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.validator.ReleaseDateCheck;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    @NotNull(message = "Id должен быть указан")
    Long id;

    @NotNull(message = "Название не может быть пустым")
    @NotBlank(message = "Название не может состоять из пробелов")
    String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    String description;

    @ReleaseDateCheck
    LocalDate releaseDate;

    @NotNull(message = "Продолжительность фильма должна быть положительным числом")
    @PositiveOrZero(message = "Продолжительность фильма должна быть положительным числом")
    Integer duration;
}
