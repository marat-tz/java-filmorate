package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public interface FilmService {

    Film create(Film film);

    Film update(Film film);

    Collection<Film> findAll();

    Film addLike(long filmId, long userId);

    Film removeLike(long filmId, long userId);

    List<Film> getPopularFilms(Long count);
}
