package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

// Напишите в DAO соответствующие
// мапперы и методы, позволяющие сохранять фильмы в базу данных и получать их из неё.
@Slf4j
@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmMapper mapper;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmMapper mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
    }

    @Override
    public Collection<Film> findAll() {
        String sqlQuery = "SELECT id, name, description, releaseDate, duration from films";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Optional<Film> findById(Long id) {
        String sqlQuery = "SELECT id, name, description, releaseDate, duration " +
                "from films where id = ?";

        Optional<Film> resultFilm = Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery,
                this::mapRowToFilm, id));

        if (resultFilm.isPresent()) {
            return resultFilm;

        } else {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
    }

    @Override
    public Film create(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final Long filmId;

        log.info("Добавление нового фильма: {}", film.getName());

        String sqlQueryFilm = "INSERT INTO films(name, description, releaseDate, duration, mpa_id) " +
                "values (?, ?, ?, ?, ?)";

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
            throw new NotFoundException("Ошибка добавления фильма в таблицу");
        }

        String sqlQueryGenreIds = "SELECT id from genres where id = ?";
        List<Long> existIdGenres = new ArrayList<>();
        Long genreId;

        if (Objects.nonNull(film.getGenres())) {
            // TODO: вместо цикла с запросом, нужно один раз получить все данные из таблицы с жанрами и пройтись по ней
            for (Genre genre : film.getGenres()) {
                try {
                    genreId = jdbcTemplate.queryForObject(sqlQueryGenreIds, Long.class, genre.getId());
                } catch (EmptyResultDataAccessException e) {
                    genreId = null;
                }

                if (Objects.nonNull(genreId)) {
                    existIdGenres.add(genreId);
                    // TODO: если айдишка существует, то добавить в film_genres
                }
            }

            if (existIdGenres.isEmpty()) {
                throw new ValidationException("Жанры с указанным id не существуют");
            }
        }

        String sqlQueryMpaIds = "SELECT id from mpa";
        List<Long> mpaIdList = jdbcTemplate.queryForList(sqlQueryMpaIds, Long.class);
        if (Objects.nonNull(film.getMpa()) && !mpaIdList.contains(film.getMpa().getId())) {
            throw new ValidationException("MPA с указанным id не существует");
        }

        log.info("Фильм c id = {} успешно добавлен", film.getId());
        return Film.builder()
                .id(filmId)
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .mpa(film.getMpa())
                .genres(film.getGenres())
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

    @Override
    public void addLike(long filmId, long userId) {
        //List<Long> filmCount = jdbcTemplate.query(filmQuery, ResultSet::getLong);
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
        List<Long> filmCount = jdbcTemplate.queryForList(filmLikeQuery, Long.class, filmId);

        if (filmCount.contains(userId)) {
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

        String filmLikesQuery = "SELECT film_id, " +
                "COUNT(film_id) AS likes " +
                "FROM film_like " +
                "GROUP BY film_id " +
                "ORDER BY likes DESC ";

        List<Long> filmIds = jdbcTemplate.queryForObject(filmLikesQuery, this::mapRowToLikes);
        List<Film> result = new ArrayList<>();

        // TODO: заполнить список через стрим с количеством count записей
        // вместо findById использовать findAll
        for (Long id : filmIds) {
            if (findById(id).isPresent()) {
                result.add(findById(id).get());
            }
        }

        return result;
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(LocalDate.parse(resultSet.getString("releaseDate")))
                .duration(resultSet.getInt("duration"))
                .build();
    }

    private List<Long> mapRowToLikes(ResultSet resultSet, int rowNum) throws SQLException {
        List<Long> filmIds = new ArrayList<>();

        while (resultSet.next()) {
            Long filmId = resultSet.getLong("film_id");

            filmIds.add(filmId);
        }

        return filmIds;
    }

}
