package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class Review {
    @Setter
    Long reviewId;
    @NotBlank
    String content;
    Boolean isPositive;
    @NotNull
    Long userId;
    @NotNull
    Long filmId;
    Integer useful;
}
