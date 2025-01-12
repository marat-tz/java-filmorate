package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mappers.FilmRowMappers;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class FilmLikeDbStorageImpl implements FilmLikeStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmStorage filmStorage;

    private final FilmRowMappers filmRowMappers;

    public FilmLikeDbStorageImpl(JdbcTemplate jdbcTemplate, @Lazy FilmStorage filmStorage, @Lazy FilmRowMappers filmRowMappers) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmStorage = filmStorage;
        this.filmRowMappers = filmRowMappers;
    }

    @Override
    public Long getLikesById(Long id) {
        final String filmLikesQuery = "SELECT COUNT(*) FROM film_like WHERE film_id = ?";
        return jdbcTemplate.queryForObject(filmLikesQuery, Long.class, id);
    }

    @Override
    public void addLike(long filmId, long userId) {

        String filmQuery = "SELECT COUNT(*) FROM films WHERE id = ?";
        Long filmCount = jdbcTemplate.queryForObject(filmQuery, Long.class, filmId);

        String userQuery = "SELECT COUNT(*) FROM users WHERE id = ?";
        Long userCount = jdbcTemplate.queryForObject(userQuery, Long.class, userId);

        if (Objects.nonNull(filmCount) && filmCount > 0) {
            if (Objects.nonNull(userCount) && userCount > 0) {

                String filmLikeQuery = "INSERT INTO film_like(user_id, film_id) values (?, ?)";

                int rows = jdbcTemplate.update(connection -> {
                    PreparedStatement stmt = connection.prepareStatement(filmLikeQuery);
                    stmt.setLong(1, userId);
                    stmt.setLong(2, filmId);
                    return stmt;
                });

                if (rows > 0) {
                    log.info("Пользователь с id = {} поставил лайк фильму с id = {}", userId, filmId);
                } else {
                    log.error("Ошибка при попытке поставить лайк фильму с id = {}", filmId);
                    throw new ValidationException("Ошибка при попытке поставить лайк фильму с id = " + filmId);
                }

            } else {
                log.error("Пользователь с id = {} не найден", userId);
                throw new NotFoundException("Пользователь с id = " + userId + " не найден");
            }

        } else {
            log.error("Фильм с id = {} не найден", filmId);
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
    }

    @Override
    public void removeLike(long filmId, long userId) {
        log.info("Пользователь с id = {} пытается удалить свой лайк фильму с id = {}", userId, filmId);

        String filmLikeQuery = "SELECT user_id FROM film_like WHERE film_id = ?";
        List<Long> userIds = jdbcTemplate.queryForList(filmLikeQuery, Long.class, filmId);

        if (userIds.contains(userId)) {
            String filmLikeRemoveQuery = "DELETE FROM film_like WHERE user_id = ? AND film_id = ?";
            int rows = jdbcTemplate.update(filmLikeRemoveQuery, userId, filmId);

            if (rows > 0) {
                log.info("Пользователь с id = {} удалил свой лайк фильму с id = {}", userId, filmId);
            } else {
                log.error("Ошибка во время удаления лайка фильму {}", filmId);
                throw new ValidationException("Ошибка во время удаления лайка фильму " + filmId);
            }
        }
    }

    @Override
    public List<Film> getPopularFilms(Long count, Long genreId, Long year) {
        log.info("Получение популярных фильмов в количестве {}", count);

        if (count <= 0) {
            log.error("Число отображаемых фильмов count не может быть меньше, либо равно 0");
            throw new ValidationException("Число отображаемых фильмов count не может быть меньше, либо равно 0");
        }

        final String filmLikesQuery = "SELECT f.id, f.name, f.description, f.releaseDate, f.duration, f.mpa_id " +
                "FROM films AS f " +
                "RIGHT OUTER JOIN film_like AS fl ON f.id = fl.film_id " +
                "GROUP BY f.id " +
                "ORDER BY COUNT(f.id) DESC";

        final String filmLikesQueryTemp = "SELECT film_id, " +
                "COUNT(film_id) AS likes " +
                "FROM film_like " +
                "GROUP BY film_id " +
                "ORDER BY likes DESC ";

        return jdbcTemplate.query(filmLikesQuery, filmRowMappers::mapRowToFilm).stream().toList();
    }
}
