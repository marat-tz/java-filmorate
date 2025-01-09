package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;

public interface FilmGenreStorage {

    Collection<Genre> findAll();

    Collection<Genre> findByFilmId(Long id);

    void addGenresInFilmGenres(Film film, Long newId);

    List<Genre> getListGenreFromDbGenres(Long filmId);

}
