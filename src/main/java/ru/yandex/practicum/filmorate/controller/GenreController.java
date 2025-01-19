package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.impl.DbGenreServiceImpl;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/genres")
public class GenreController {

    private final DbGenreServiceImpl service;

    public GenreController(DbGenreServiceImpl service) {
        this.service = service;
    }

    @GetMapping
    public Collection<Genre> findAll() {
        log.info("Получен GET запрос /genres");
        Collection<Genre> result = service.findAll();
        log.info("Отправлен GET ответ {}", result);
        return result;
    }

    @GetMapping("/{id}")
    public Genre getNameById(@PathVariable Long id) {
        log.info("Получен GET запрос /genres/{}", id);
        Genre result = service.getNameById(id);
        log.info("Отправлен GET ответ {}", result);
        return result;
    }

}
