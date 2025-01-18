package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mappers.FilmRowMappers;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;


import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorageImpl implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final FilmRowMappers filmRowMappers;
    private final FilmGenreStorage filmGenreStorage;
    private final DirectorStorage directorStorage;

    @Override
    public Collection<Film> findAll() {
        log.info("Выгрузка всех фильмов");
        final String sqlQuery = "SELECT id, name, description, releaseDate, duration, mpa_id FROM films";
        return jdbcTemplate.query(sqlQuery, filmRowMappers::mapRowToFilm);
    }

    @Override
    public Film findById(Long id) {
        log.info("Поиск фильма по id = {}", id);

        Optional<Film> resultFilm;

        final String sqlQuery = "SELECT id, name, description, releaseDate, duration, mpa_id " +
                "FROM films WHERE id = ?";

        try {
            resultFilm = Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery,
                    filmRowMappers::mapRowToFilm, id));
        } catch (EmptyResultDataAccessException e) {
            resultFilm = Optional.empty();
        }

        if (resultFilm.isPresent()) {
            return resultFilm.get();

        } else {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
    }

    @Override
    public Film create(Film film) {
        log.info("Добавление нового фильма: {}", film.getName());
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final long filmId;

        final String sqlQueryFilm = "INSERT INTO films(name, description, releaseDate, duration, mpa_id) " +
                "values (?, ?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQueryFilm, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setString(3, film.getReleaseDate().toString());
            stmt.setInt(4, film.getDuration());
            if (mpaStorage.getCountById(film) != 0) {
                stmt.setLong(5, film.getMpa().getId());
            } else {
                stmt.setNull(5, 0);
            }
            return stmt;
        }, keyHolder);

        if (Objects.nonNull(keyHolder.getKey())) {
            filmId = keyHolder.getKey().longValue();
        } else {
            throw new ValidationException("Ошибка добавления фильма в таблицу");
        }

        // кладём жанры фильма в таблицу film_genre
        filmGenreStorage.addGenresInFilmGenres(film, filmId);

        directorStorage.addDirectorsByFilm(film);

        log.info("Фильм c id = {} успешно добавлен", filmId);
        return findById(filmId);
    }

    @Override
    public Film update(Film newFilm) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final long filmId;

        log.info("Обновление данных фильма с id = {}", newFilm.getId());

        String sqlQuery = "UPDATE films SET " +
                "name = ?, description = ?, releaseDate = ?, duration = ? " +
                "where id = ?";

        int rows = jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, newFilm.getName());
            stmt.setString(2, newFilm.getDescription());
            stmt.setString(3, newFilm.getReleaseDate().toString());
            stmt.setInt(4, newFilm.getDuration());
            stmt.setLong(5, newFilm.getId());
            return stmt;
        }, keyHolder);

        if (Objects.nonNull(keyHolder.getKey())) {
            filmId = keyHolder.getKey().longValue();
        } else {
            throw new NotFoundException("Ошибка обновления фильма");
        }

        if (rows > 0) {
            log.info("Фильм с id = {} успешно обновлён", filmId);
            directorStorage.updateDirectorsByFilm(newFilm);
            return findById(filmId);

        } else {
            log.error("Ошибка обновления фильма id = {}", filmId);
            throw new NotFoundException("Ошибка обновления фильма id = " + filmId);
        }
    }

    @Override
    public List<Film> getFilmsByDirector(Long directorId, String sortBy) {
        String sqlQuery = "SELECT f.id, f.name, f.description, f.releaseDate, f.duration, f.mpa_id " +
                "FROM film_director f_d " +
                "LEFT JOIN films f " +
                "    ON f_d.film_id = f.id ";

        if (sortBy.equals("year")) {
            sqlQuery += "WHERE director_id = ? " +
                    " Order by f.releaseDate";
        } else if (sortBy.equals("likes")) {
            sqlQuery += " LEFT JOIN (Select film_id, count(*) as likes from film_like group by film_id) f_l " +
                    " ON f_l.film_id = f.id " +
                    " WHERE director_id = ? " +
                    " Order by f_l.likes DESC";
        } else {
            throw new ValidationException("Неизвестная сортировка");
        }

        return jdbcTemplate.query(sqlQuery, filmRowMappers::mapRowToFilm, directorId).stream().toList();
    }
}
