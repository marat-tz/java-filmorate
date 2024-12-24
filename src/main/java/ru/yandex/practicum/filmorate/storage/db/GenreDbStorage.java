package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mappers.GenreRowMappers;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component("genreDbStorage")
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreRowMappers genreRowMappers;

    @Override
    public Collection<Genre> findAll() {
        String sqlQuery = "SELECT * from genres";
        return jdbcTemplate.query(sqlQuery, genreRowMappers::mapRowToGenre);
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
                    genreRowMappers::mapRowToGenre, id));
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

    @Override
    public Collection<Genre> getExistGenres(Film film) {
        List<Long> genres = findIds().stream().toList();
        List<Genre> filmGenres = film.getGenres();
        List<Genre> resultGenres = new ArrayList<>();

        if (Objects.nonNull(filmGenres)) {
            filmGenres.forEach(genre -> {
                        if (genres.contains(genre.getId())) {
                            resultGenres.add(genre);
                        } else {
                            throw new ValidationException("Указанный жанр не существует");
                        }
                    }
            );
        }
        return resultGenres;
    }
}
