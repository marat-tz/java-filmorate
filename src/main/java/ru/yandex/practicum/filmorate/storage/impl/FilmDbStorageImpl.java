package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mappers.FilmRowMappers;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
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

    @Override
    public Collection<Film> findAll() {
        log.info("Выгрузка всех фильмов");
        final String sqlQuery = "SELECT id, name, description, releaseDate, duration, mpa_id FROM films";
        return jdbcTemplate.query(sqlQuery, filmRowMappers::mapRowToFilm);
    }

    @Override
    public Film findById(Long id) {
        log.info("Поиск фильма по id = {}", id);

        final String sqlQuery = "SELECT id, name, description, releaseDate, duration, mpa_id " +
                "FROM films WHERE id = ?";

        Optional<Film> resultFilm = Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery,
                filmRowMappers::mapRowToFilm, id));

        if (resultFilm.isPresent()) {
            return resultFilm.get();

        } else {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
    }

    @Override
    public Film create(Film film) {
        log.info("Добавление нового фильма: {}", film.getName());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        final long filmId;

        final String sqlQueryFilm = "INSERT INTO films(name, description, releaseDate, duration, mpa_id) " +
                "values (?, ?, ?, ?, ?)";

        // проверяем существование рейтинга в таблице mpa
        mpaStorage.getCountById(film);

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQueryFilm, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setString(3, film.getReleaseDate().toString());
            stmt.setInt(4, film.getDuration());
            stmt.setLong(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        if (Objects.nonNull(keyHolder.getKey())) {
            filmId = keyHolder.getKey().longValue();
        } else {
            throw new ValidationException("Ошибка добавления фильма в таблицу");
        }

        // кладём жанры фильма в таблицу film_genre
        filmGenreStorage.addGenresInFilmGenres(film, filmId);

        List<Genre> resultGenres = genreStorage.getExistGenres(film).stream().toList();

        log.info("Фильм c id = {} успешно добавлен", film.getId());
        return Film.builder()
                .id(filmId)
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .mpa(film.getMpa())
                .genres(resultGenres)
                .build();
    }

    @Override
    public Film update(Film newFilm) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final long filmId;

        log.info("Обновление данных фильма с id = {}", newFilm.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

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

        Film resultFilm = Film.builder()
                .id(filmId)
                .name(newFilm.getName())
                .description(newFilm.getDescription())
                .releaseDate(newFilm.getReleaseDate())
                .duration(newFilm.getDuration())
                .build();

        if (rows > 0) {
            log.info("Фильм с id = {} успешно обновлён", filmId);
            return resultFilm;

        } else {
            log.error("Ошибка обновления фильма id = {}", filmId);
            throw new NotFoundException("Ошибка обновления фильма id = " + filmId);
        }
    }
}
