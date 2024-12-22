package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.db.DbGenreService;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
public class GenreController {

    private final DbGenreService service;

    public GenreController(DbGenreService service) {
        this.service = service;
    }

    @GetMapping
    public Collection<Genre> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Genre getNameById(@PathVariable Long id) {
        return service.getNameById(id);
    }

}
