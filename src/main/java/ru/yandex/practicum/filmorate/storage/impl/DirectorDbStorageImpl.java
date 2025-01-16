package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.DirectorRowMappers;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class DirectorDbStorageImpl implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;
    private final DirectorRowMappers directorRowMappers;

    @Override
    public Collection<Director> findAll() {
        String sqlQuery = "SELECT id, name from directors";
        return jdbcTemplate.query(sqlQuery, directorRowMappers::mapRowToDirector);
    }

    @Override
    public Director getById(Long id) {
        String sqlQuery = "SELECT id, name from directors" +
                " where id = ?";
        Director director = jdbcTemplate.query(sqlQuery, directorRowMappers::mapRowToDirector, id)
                .stream().findAny().orElse(null);

        if (director == null) {
            throw new NotFoundException("Режиссера с id = " + id + " не найден");
        }

        return director;
    }

    @Override
    public Director create(Director director) {
        String sqlQuery = "INSERT INTO directors(name) values (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection
                    .prepareStatement(sqlQuery,
                            Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, director.getName());
            return preparedStatement;
        }, keyHolder);


        return getById(Objects.requireNonNull(keyHolder.getKey()).longValue());
    }

    @Override
    public Director update(Director director) {
        String sqlQuery = "UPDATE directors" +
                " SET name = ?" +
                " where id = ?";
        int rows = jdbcTemplate.update(sqlQuery, director.getName(), director.getId());

        if (rows == 0) {
            throw new NotFoundException("Ошибка обновления режиссера id = " + director.getId());
        }

        return getById(director.getId());
    }

    @Override
    public void delete(Long id) {
        String sqlQuery = "DELETE FROM directors" +
                " where id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public List<Director> getDirectorsByFilm(Long filmId) {
        String sqlQuery = "SELECT d.id, d.name " +
                "FROM film_director f_d " +
                "LEFT JOIN directors d " +
                "    ON f_d.director_id = d.id " +
                "WHERE film_id = ? " +
                "ORDER BY d.id ";
        return jdbcTemplate.query(sqlQuery, directorRowMappers::mapRowToDirector, filmId).stream().toList();
    }

    @Override
    public void updateDirectorsByFilm(Film film) {
        deleteDirectorsByFilm(film);
        addDirectorsByFilm(film);
    }

    @Override
    public void deleteDirectorsByFilm(Film film) {
        String sqlQuery = "DELETE FROM film_director" +
                " where film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
    }

    @Override
    public void addDirectorsByFilm(Film film) {
        addDirectorsByFilm(film, film.getId());
    }

    @Override
    public void addDirectorsByFilm(Film film, long filmId) {
        if (film.getDirectors() != null) {

            String sqlQuery = "INSERT INTO film_director(film_id, director_id) values (?, ?)";

            jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                    preparedStatement.setLong(1, filmId);
                    preparedStatement.setLong(2, film.getDirectors().get(i).getId());
                }

                @Override
                public int getBatchSize() {
                    return film.getDirectors().size();
                }
            });
        }
    }
}
