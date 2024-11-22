package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private final FilmMapper mapper;

    @Autowired
    public InMemoryFilmStorage(FilmMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film create(Film film) {
        log.info("Добавление нового фильма: {}", film.getName());
        film = film.toBuilder().id(getNextId()).build();
        films.put(film.getId(), film);
        log.info("Фильм c id = {} успешно добавлен", film.getId());
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        Long id = newFilm.getId();
        Film film;
        if (films.containsKey(id)) {
            log.info("Обновление фильма с id = {}", id);
            film = mapper.toFilm(newFilm);
            films.put(id, film);
            log.info("Фильм с id = {} успешно обновлён", id);
            return film;
        } else {
            log.error("Фильм с id = {} не найден", id);
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
