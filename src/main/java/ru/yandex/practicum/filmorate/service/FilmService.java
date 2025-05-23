package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {

    Film create(Film film);

    Film update(Film film);

    void delete(Long id);

    List<Film> findAll();

    Film findById(Long id);

    void addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);

    List<Film> getPopularFilms(Long count, Long genreId, Long year);

    List<Film> getFilmsByDirector(Long directorId, String sortBy);

    List<Film> getFilmsByDirectorAndOrByTitle(String query, String by);

    List<Film> getCommonFilms(Long userId, Long friendId);
}
