package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import ru.yandex.practicum.filmorate.validator.ReleaseDateCheck;

import java.time.LocalDate;
import java.util.List;

/**
 * Film.
 */
@Value
@Builder(toBuilder = true)
public class Film {

    Long id;
    Long likes;

    @NotBlank(message = "Название не может быть пустым")
    String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    String description;

    @ReleaseDateCheck
    LocalDate releaseDate;

    @NotNull(message = "Продолжительность фильма должна быть положительным числом")
    @PositiveOrZero(message = "Продолжительность фильма должна быть положительным числом")
    Integer duration;

    Mpa mpa;

    List<Genre> genres;
}
