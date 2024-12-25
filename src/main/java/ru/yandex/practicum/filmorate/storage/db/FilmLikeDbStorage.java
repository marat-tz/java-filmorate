package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class FilmLikeDbStorage implements FilmLikeStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmStorage filmStorage;

    public FilmLikeDbStorage(JdbcTemplate jdbcTemplate, @Lazy FilmStorage filmStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmStorage = filmStorage;
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
    public List<Film> getPopularFilms(Long count) {
        log.info("Получение популярных фильмов в количестве {}", count);

        if (count <= 0) {
            log.error("Число отображаемых фильмов count не может быть меньше, либо равно 0");
            throw new ValidationException("Число отображаемых фильмов count не может быть меньше, либо равно 0");
        }

        final String filmLikesQuery = "SELECT film_id, " +
                "COUNT(film_id) AS likes " +
                "FROM film_like " +
                "GROUP BY film_id " +
                "ORDER BY likes DESC ";

        List<Long> filmIds = jdbcTemplate.query(filmLikesQuery, rs -> {
            List<Long> ids = new ArrayList<>();
            while (rs.next()) {
                ids.add(rs.getLong("film_id"));
            }
            return ids;
        });

        List<Film> result = new ArrayList<>();
        List<Film> allFilms = filmStorage.findAll().stream().toList();

        allFilms.forEach(film -> {
            if (Objects.requireNonNull(filmIds).contains(film.getId())) {
                result.add(film);
            }
        });

        result.sort(new Comparator<Film>() {
            @Override
            public int compare(Film f1, Film f2) {
                Long likes1 = f1.getLikes();
                Long likes2 = f2.getLikes();

                if (Objects.isNull(likes1)) {
                    likes1 = 0L;
                }

                if (Objects.isNull(likes2)) {
                    likes2 = 0L;
                }

                return likes1.compareTo(likes2);
            }
        });

        Collections.reverse(result);
        for (Film f : result) {
            log.info("Количество лайков фильма " + f.getId() + " равно " + f.getLikes());
        }

        return result;
    }
}
