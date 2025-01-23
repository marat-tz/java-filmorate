package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {

    List<Genre> findAll();

    List<Long> findIds();

    Optional<Genre> findById(Long id);

    Genre getNameById(Long id);

    List<Genre> getExistGenres(Film film);

}
