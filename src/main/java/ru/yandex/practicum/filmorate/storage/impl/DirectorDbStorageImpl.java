package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class DirectorDbStorageImpl implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    public DirectorDbStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Director> findAll() {
        String sqlQuery = "SELECT id, name from directors";
        return jdbcTemplate.query(sqlQuery, this::mapRowToDirector);
    }

    @Override
    public Director getById(Long id) {
        String sqlQuery = "SELECT id, name from directors" +
                " where id = ?";
        Director director = jdbcTemplate.query(sqlQuery, this::mapRowToDirector, id)
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
        return jdbcTemplate.query(sqlQuery, this::mapRowToDirector, filmId).stream().toList();
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
        if (film.getDirectors() != null) {
            String sqlQuery = "INSERT INTO film_director(film_id, director_id) values (?, ?)";
            for (Director director : film.getDirectors()) {
                jdbcTemplate.update(sqlQuery, film.getId(), director.getId());
            }
        }
    }

    public Director mapRowToDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return Director.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
