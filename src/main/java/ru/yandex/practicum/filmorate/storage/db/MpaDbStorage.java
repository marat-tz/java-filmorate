package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Component("mpaDbStorage")
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Mpa> findAll() {
        String sqlQuery = "SELECT * from mpa";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
    }

    @Override
    public Mpa findById(Integer id) {
        Optional<Mpa> resultMpa;
        String sqlQuery = "SELECT id, name " +
                "FROM mpa where id = ?";

        try {
            resultMpa = Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery,
                    this::mapRowToMpa, id));
        } catch (EmptyResultDataAccessException e) {
            resultMpa = Optional.empty();
        }

        return resultMpa.orElse(null);
    }

    @Override
    public Mpa getNameById(Long id) {
        log.info("Поиск MPA по id: {}", id);
        String sqlQuery = "SELECT * " +
                "FROM mpa where id = ?";

        Optional<Mpa> resultMpa;

        try {
            resultMpa = Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery,
                    this::mapRowToMpa, id));
        } catch (EmptyResultDataAccessException e) {
            resultMpa = Optional.empty();
        }

        if (resultMpa.isPresent()) {
            return resultMpa.get();

        } else {
            log.error("Mpa с id = {} не найден", id);
            throw new NotFoundException("Mpa с id = " + id + " не найден");
        }
    }

    public Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();
    }

}
