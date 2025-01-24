package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface FilmGenreStorage {

    List<Genre> findAll();

    List<Genre> findByFilmId(Long id);

    void addGenresInFilmGenres(Film film, Long newId);

    List<Genre> getListGenreFromDbGenres(Long filmId);

}
