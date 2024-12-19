package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Qualifier("mpaDbStorage")
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
    public Optional<Mpa> findById(long id) {
        String sqlQuery = "SELECT id, name " +
                "FROM mpa where id = ?";

        Optional<Mpa> resultMpa = Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery,
                this::mapRowToMpa, id));

        if (resultMpa.isPresent()) {
            return resultMpa;

        } else {
            throw new NotFoundException("Mpa с id = " + id + " не найден");
        }
    }

    @Override
    public String getNameById(long id) {
        String sqlQuery = "SELECT name " +
                "FROM mpa where id = ?";

        Optional<Mpa> resultMpa = Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery,
                this::mapRowToMpa, id));

        if (resultMpa.isPresent()) {
            return resultMpa.get().getName();

        } else {
            throw new NotFoundException("Mpa с id = " + id + " не найден");
        }
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();
    }

}
