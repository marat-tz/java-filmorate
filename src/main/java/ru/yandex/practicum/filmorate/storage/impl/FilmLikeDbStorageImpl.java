package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mappers.FilmRowMappers;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.FilmLikeStorage;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


@Slf4j
@Component
public class FilmLikeDbStorageImpl implements FilmLikeStorage {

    private final JdbcTemplate jdbcTemplate;

    private final FeedStorage feedStorage;

    private final FilmRowMappers filmRowMappers;

    public FilmLikeDbStorageImpl(JdbcTemplate jdbcTemplate, @Lazy FilmRowMappers filmRowMappers,
                                 @Lazy FeedStorage feedStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmRowMappers = filmRowMappers;
        this.feedStorage = feedStorage;
    }

    @Override
    public Long getLikesById(Long id) {
        final String filmLikesQuery = "SELECT COUNT(*) FROM film_like WHERE film_id = ?";
        return jdbcTemplate.queryForObject(filmLikesQuery, Long.class, id);
    }

    @Override
    public void addLike(long filmId, long userId) {
        log.info("Попытка пользователя {} добавить лайк фильму {}", userId, filmId);

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
                    feedStorage.create(userId, EventType.LIKE, Operation.ADD, filmId);
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
                feedStorage.create(userId, EventType.LIKE, Operation.REMOVE, filmId);
                log.info("Пользователь с id = {} удалил свой лайк фильму с id = {}", userId, filmId);
            } else {
                log.error("Ошибка во время удаления лайка фильму {}", filmId);
                throw new ValidationException("Ошибка во время удаления лайка фильму " + filmId);
            }
        }
    }

    @Override
    public List<Film> getPopularFilms(Long count, Long genreId, Long year) {

        if (Objects.nonNull(genreId) && Objects.nonNull(year)) {
            return getPopularFilmsGenreYear(count, genreId, year);

        } else if (Objects.isNull(genreId) && Objects.nonNull(year)) {
            return getPopularFilmsYear(count, year);

        } else if (Objects.nonNull(genreId)) {
            return getPopularFilmsGenre(count, genreId);
        }

        log.info("Получение популярных фильмов в количестве {}", count);

        final String filmLikesQueryCount = "SELECT f.id, f.name, f.description, f.releaseDate, f.duration, f.mpa_id " +
                "FROM films AS f " +
                "LEFT OUTER JOIN film_like AS fl ON f.id = fl.film_id " +
                "GROUP BY f.id " +
                "ORDER BY COUNT(fl.film_id) DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(filmLikesQueryCount, filmRowMappers::mapRowToFilm, count)
                .stream().toList();
    }

    private List<Film> getPopularFilmsGenre(Long count, Long genreId) {
        log.info("Получение популярных фильмов с фильтрацией по жанру {} в количестве {}", genreId, count);

        final String filmLikesQueryGenres = "SELECT f.id, f.name, f.description, f.releaseDate, f.duration, f.mpa_id " +
                "FROM films AS f " +
                "RIGHT OUTER JOIN film_like AS fl ON f.id = fl.film_id  " +
                "RIGHT OUTER JOIN film_genre AS fg ON f.id = fg.film_id " +
                "WHERE fg.genre_id = ? " +
                "GROUP BY f.id " +
                "ORDER BY COUNT(fl.film_id) DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(filmLikesQueryGenres, filmRowMappers::mapRowToFilm,
                    genreId, count).stream().toList();
    }

    private List<Film> getPopularFilmsGenreYear(Long count, Long genreId, Long year) {
        log.info("Получение популярных фильмов c фильтрацией по жанру {} и году {} в количестве {}",
                genreId, year, count);

        final String filmLikesQueryGenreYear = "SELECT f.id, f.name, f.description, f.releaseDate, f.duration, f.mpa_id " +
                "FROM films AS f " +
                "RIGHT OUTER JOIN film_like AS fl ON f.id = fl.film_id " +
                "RIGHT OUTER JOIN film_genre AS fg ON f.id = fg.film_id " +
                "WHERE fg.genre_id = ? AND EXTRACT(YEAR FROM f.releaseDate) = ? " +
                "GROUP BY f.id " +
                "ORDER BY COUNT(fl.film_id) DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(filmLikesQueryGenreYear, filmRowMappers::mapRowToFilm,
                    genreId, year, count).stream().toList();
    }

    private List<Film> getPopularFilmsYear(Long count, Long year) {
        log.info("Получение популярных фильмов с фильрацией по году {} в количестве {}", year, count);

        final String filmLikesQueryYear = "SELECT f.id, f.name, f.description, f.releaseDate, f.duration, f.mpa_id " +
                "FROM films AS f " +
                "RIGHT OUTER JOIN film_like AS fl ON f.id = fl.film_id " +
                "WHERE EXTRACT(YEAR FROM f.releaseDate) = ? " +
                "GROUP BY f.id " +
                "ORDER BY COUNT(fl.film_id) DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(filmLikesQueryYear, filmRowMappers::mapRowToFilm,
                year, count).stream().toList();
    }

    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        log.info("Получение списка общих фильмов, лайкнутых пользователями с ID {} и {}", userId, friendId);

        final String filmCommonLikes = "SELECT f.id, f.name, f.description, f.releaseDate, f.duration, f.mpa_id " +
                "FROM films f " +
                "INNER JOIN (SELECT user1.film_id " +
                "    FROM (SELECT film_id FROM film_like WHERE user_id = ?) user1 " +
                "        INNER JOIN (SELECT film_id FROM film_like WHERE user_id = ?) user2 " +
                "        on user1.film_id = user2.film_id) l " +
                "ON l.film_id = f.id " +
                "LEFT JOIN (SELECT film_id, COUNT(user_id) AS count_like FROM film_like GROUP BY film_id) likes " +
                "ON f.id = likes.film_id " +
                "ORDER BY count_like DESC";

        return jdbcTemplate.query(filmCommonLikes, filmRowMappers::mapRowToFilm, userId,
                friendId).stream().toList();
    }

    @Override
    public Collection<Film> getRecommendations(Long userId) {
        log.info("Получение рекомендации для пользователя с ID {}", userId);

        String query = "SELECT f.id, f.name, f.description, f.releaseDate, f.duration, f.mpa_id FROM films f " +
                "INNER JOIN \n" +
                "(\n" +
                "Select film_id from FILM_LIKE f_l\n" +
                "inner join (\n" +
                "  Select \n" +
                "    user_id2.user_id\n" +
                "  from ( Select film_id from FILM_LIKE where user_id = ?) user_id1\n" +
                "  left join FILM_LIKE  user_id2\n" +
                "  on user_id1.film_id = user_id2.film_id and user_id2.user_id <> ?\n" +
                "  group by user_id2.user_id \n" +
                "  Order by count(user_id2.film_id) DESC\n" +
                "  Limit 1\n" +
                ") curr_user\n" +
                "on f_l.user_id = curr_user.user_id\n" +
                "\n" +
                "EXCEPT\n" +
                "\n" +
                "Select film_id from FILM_LIKE where user_id = ?\n" +
                ") ff\n" +
                "on f.id = ff.film_id";

        return jdbcTemplate.query(query, filmRowMappers::mapRowToFilm, userId, userId, userId).stream().toList();
    }
}

