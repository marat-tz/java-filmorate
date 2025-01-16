package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service("dbFilmService")
public class DbFilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final FilmLikeStorage filmLikeStorage;

    public DbFilmServiceImpl(@Qualifier("filmDbStorage") FilmStorage filmStorage, FilmLikeStorage filmLikeStorage) {
        this.filmStorage = filmStorage;
        this.filmLikeStorage = filmLikeStorage;
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
        filmLikeStorage.addLike(filmId, userId);
    }

    @Override
    public void removeLike(long filmId, long userId) {
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
