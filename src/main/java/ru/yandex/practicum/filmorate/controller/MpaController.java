package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.impl.DbMpaServiceImpl;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class MpaController {

    private final DbMpaServiceImpl service;

    public MpaController(DbMpaServiceImpl service) {
        this.service = service;
    }

    @GetMapping
    public Collection<Mpa> findAll() {
        log.info("Получен GET запрос /mpa");
        Collection<Mpa> result = service.findAll();
        log.info("Отправлен GET ответ {}", result);
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Mpa getNameById(@PathVariable Long id) {
        log.info("Получен GET запрос /mpa/{}", id);
        Mpa result = service.getNameById(id);
        log.info("Отправлен GET ответ {}", result);
        return result;
    }

}
