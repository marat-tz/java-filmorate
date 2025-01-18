package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service("dbFilmService")
public class DbFilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final FilmLikeStorage filmLikeStorage;

    private final UserStorage userStorage;

    public DbFilmServiceImpl(@Qualifier("filmDbStorage") FilmStorage filmStorage, FilmLikeStorage filmLikeStorage,
                             UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.filmLikeStorage = filmLikeStorage;
        this.userStorage = userStorage;
    }

    @Override
    public Film create(Film film) {
        return filmStorage.create(film);
    }

    @Override
    public Film update(Film film) {
        return filmStorage.update(film);
    }

    @Override
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    @Override
    public Film findById(Long id) {
        return filmStorage.findById(id);
    }

    @Override
    public void addLike(long filmId, long userId) {

        if (filmStorage.findById(filmId) == null) {
            throw new NotFoundException("Фильм с id " + filmId + " не существует");
        }

        if (userStorage.findById(userId) == null) {
            throw new NotFoundException("Пользователь с id " + filmId + " не существует");
        }

        filmLikeStorage.addLike(filmId, userId);
    }

    @Override
    public void removeLike(long filmId, long userId) {

        if (filmStorage.findById(filmId) == null) {
            throw new NotFoundException("Фильм с id " + filmId + " не существует");
        }

        if (userStorage.findById(userId) == null) {
            throw new NotFoundException("Пользователь с id " + filmId + " не существует");
        }

        filmLikeStorage.removeLike(filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(Long count, Long genreId, Long year) {

        if (count <= 0) {
            log.error("Число отображаемых фильмов count не может быть меньше, либо равно 0");
            throw new ValidationException("Число отображаемых фильмов count не может быть меньше, либо равно 0");
        }

        return filmLikeStorage.getPopularFilms(count, genreId, year);

    }

    @Override
    public List<Film> getFilmsByDirector(Long directorId, String sortBy) {
        return filmStorage.getFilmsByDirector(directorId, sortBy);
    }

    @Override
    public List<Film> getFilmsByDirectorAndOrByTitle(String query, String by) {
        return filmStorage.getFilmsByDirectorAndOrByTitle(query, by);
    }
}
