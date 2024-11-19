package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.Collection;

@RestController
@RequestMapping("/films")
public class FilmController {

    @Autowired
    private FilmStorage storage;

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
}
