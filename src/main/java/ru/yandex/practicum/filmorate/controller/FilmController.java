package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Добавление нового фильма: {}", film.getName());
        film = film.toBuilder().id(getNextId()).build();
        films.put(film.getId(), film);
        log.info("Фильм c id {} успешно добавлен", film.getId());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        Long id = newFilm.getId();
        if (films.containsKey(id)) {
            log.info("Обновление фильма с id: {}", id);

            Film oldFilm = newFilm.toBuilder()
                    .name(newFilm.getName())
                    .description(newFilm.getDescription())
                    .releaseDate(newFilm.getReleaseDate())
                    .duration(newFilm.getDuration())
                    .build();

            films.put(id, oldFilm);
            log.info("Фильм с id {} успешно обновлён", id);
            return oldFilm;
        } else {
            log.error("Фильм с id = " + newFilm.getId() + " не найден");
            throw new ValidationException("Фильм с id = " + newFilm.getId() + " не найден");
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
