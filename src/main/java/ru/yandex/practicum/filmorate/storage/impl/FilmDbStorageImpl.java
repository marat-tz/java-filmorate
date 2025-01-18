package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mappers.FilmRowMappers;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.*;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Collection;

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

        KeyHolder keyHolder = new GeneratedKeyHolder();
        final long filmId;

        final String sqlQueryFilm = "INSERT INTO films(name, description, releaseDate, duration, mpa_id) " +
                "values (?, ?, ?, ?, ?)";

        // проверяем существование рейтинга в таблице mpa
        mpaStorage.getCountById(film);
        // проверяем существование жанров
        genreStorage.getExistGenres(film);

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

        directorStorage.addDirectorsByFilm(film, filmId);

        log.info("Фильм c id = {} успешно добавлен", filmId);
        return findById(filmId);
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

        log.info(sqlQuery);
        return jdbcTemplate.query(sqlQuery, filmRowMappers::mapRowToFilm, directorId).stream().toList();
    }

    @Override
    public List<Film> getFilmsByDirectorAndOrByTitle(String query, String by) {
        String sqlQuery = "SELECT f.id, f.name, f.description, f.releaseDate, f.duration, f.mpa_id FROM films f\n";

        List<String> whereQuery = new ArrayList<>();
        if (by.contains("director")) {
            sqlQuery += " left join film_director f_d\n" +
                    " On f_d.film_id = f.id\n" +
                    " LEFT JOIN directors d\n" +
                    " ON f_d.director_id = d.id\n";
            whereQuery.add(" d.name ilike '%" + query + "%' ");
        }

        if (by.contains("title")) {
            whereQuery.add(" f.name ilike '%" + query + "%' ");
        }

        if (whereQuery.isEmpty()) {
            throw new NotFoundException("Неизвестное значение переменной by = " + by);
        }

        sqlQuery += " left join (\n " +
                "   SELECT film_id, COUNT(user_id) AS likes FROM film_like\n" +
                "   Group by film_id\n" +
                ") l\n" +
                " ON l.film_id = f.id\n" +
                "Where" + String.join(" or ", whereQuery)  + "\n" +
                " order by l.likes Desc ";

        return jdbcTemplate.query(sqlQuery, filmRowMappers::mapRowToFilm);
    }
}
