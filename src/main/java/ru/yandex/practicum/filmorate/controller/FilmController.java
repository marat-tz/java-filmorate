package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService service;

    public FilmController(@Qualifier("dbFilmService") FilmService service) {
        this.service = service;
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Получен GET запрос /films");
        Collection<Film> result = service.findAll();
        log.info("Отправлен GET ответ, size={}, {}", result.size(), result);
        return result;
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable Long id) {
        log.info("Получен GET запрос /films/{id}");
        Film result = service.findById(id);
        log.info("Отправлен GET ответ {}", result);
        return result;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Получен POST запрос /films");
        Film result = service.create(film);
        log.info("Отправлен POST ответ {}", result);
        return result;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        log.info("Получен PUT запрос /films");
        Film result = service.update(newFilm);
        log.info("Отправлен PUT ответ {}", result);
        return result;
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable Long id) {
        log.info("Получен DELETE запрос /id с телом {}", id);
        service.delete(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен PUT запрос /films/{}/like/{}", id, userId);
        service.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен DELETE запрос /films/{}/like/{}", id, userId);
        service.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") Long count, Long genreId, Long year) {
        log.info("Получен GET запрос /films/popular?count={}&genreId={}&year={}", count, genreId, year);
        List<Film> result = service.getPopularFilms(count, genreId, year);
        log.info("Отправлен GET ответ, size={}, {}", result.size(), result);
        return result;
    }

    @GetMapping("director/{id}")
    public List<Film> getFilmsByDirector(@PathVariable Long id, @RequestParam String sortBy) {
        log.info("Получен GET запрос /films/director/{}", id);
        List<Film> result = service.getFilmsByDirector(id, sortBy);
        log.info("Отправлен GET ответ, size={}, {}", result.size(), result);
        return result;
    }

    @GetMapping("/search")
    public List<Film> getFilmsByDirectorAndOrByTitle(@RequestParam String query, @RequestParam String by) {
        return service.getFilmsByDirectorAndOrByTitle(query, by);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam Long userId, @RequestParam Long friendId) {
        return service.getCommonFilms(userId, friendId);
    }
}
