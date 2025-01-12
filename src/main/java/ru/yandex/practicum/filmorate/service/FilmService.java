package ru.yandex.practicum.filmorate.service;

import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface FilmService {

    Film create(Film film);

    Film update(Film film);

    Collection<Film> findAll();

    Film findById(Long id);

    void addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);

    List<Film> getPopularFilms(Long count, Long genreId, LocalDate year);

    List<Film> getFilmsByDirector(Long directorId, String sortBy);
}
