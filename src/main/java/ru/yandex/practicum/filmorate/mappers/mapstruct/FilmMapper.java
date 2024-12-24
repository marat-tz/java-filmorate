package ru.yandex.practicum.filmorate.mappers.mapstruct;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.model.Film;

@Mapper(componentModel = "spring")
public interface FilmMapper {
    Film toFilm(Film film);
}
