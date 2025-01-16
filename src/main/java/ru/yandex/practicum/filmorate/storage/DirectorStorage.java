package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface DirectorStorage {

    Collection<Director> findAll();

    Director getById(Long id);

    Director create(Director director);

    Director update(Director director);

    List<Director> getDirectorsByFilm(Long filmId);

    void delete(Long id);

    void updateDirectorsByFilm(Film film);

    void deleteDirectorsByFilm(Film film);

    void addDirectorsByFilm(Film film);

    void addDirectorsByFilm(Film film, long filmId);
}
