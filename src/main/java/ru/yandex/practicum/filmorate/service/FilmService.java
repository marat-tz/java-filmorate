package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmService {

    Film create(Film film);

    Film update(Film film);

    Collection<Film> findAll();

    Film addLike(long filmId, long userId);

    Film removeLike(long filmId, long userId);

    List<Film> getPopularFilms(Long count);
}
