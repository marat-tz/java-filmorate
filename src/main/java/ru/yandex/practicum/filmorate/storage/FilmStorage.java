package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    Collection<Film> findAll();

    Film findById(Long id);

    Film create(Film film);

    Film update(Film newFilm);

    List<Film> getFilmsByDirector(Long directorId, String sortBy);

}
