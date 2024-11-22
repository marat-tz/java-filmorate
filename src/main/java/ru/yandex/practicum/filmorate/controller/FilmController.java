package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmStorage storage;
    private final FilmService service;

    @Autowired
    public FilmController(FilmStorage storage, FilmService service) {
        this.storage = storage;
        this.service = service;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return storage.findAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return storage.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        return storage.update(newFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable Long id, @PathVariable Long userId) {
        return service.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film removeLike(@PathVariable Long id, @PathVariable Long userId) {
        return service.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") Long count) {
        return service.getPopularFilms(count);
    }
}
