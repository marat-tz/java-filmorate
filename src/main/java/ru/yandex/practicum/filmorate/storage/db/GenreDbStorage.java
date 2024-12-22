package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Component("genreDbStorage")
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Genre> findAll() {
        String sqlQuery = "SELECT * from genres";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    public Collection<Long> findIds() {
        String sqlQuery = "SELECT id from genres";
        return jdbcTemplate.queryForList(sqlQuery, Long.class);
    }

    @Override
    public Optional<Genre> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Genre getNameById(Long id) {
        log.info("Поиск жанра по id: {}", id);
        String sqlQuery = "SELECT * " +
                "FROM genres where id = ?";

        Optional<Genre> resultGenre;

        try {
            resultGenre = Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery,
                    this::mapRowToGenre, id));
        } catch (EmptyResultDataAccessException e) {
            resultGenre = Optional.empty();
        }

        if (resultGenre.isPresent()) {
            return resultGenre.get();

        } else {
            log.error("Жанр с id = {} не найден", id);
            throw new NotFoundException("Жанр с id = " + id + " не найден");
        }
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
