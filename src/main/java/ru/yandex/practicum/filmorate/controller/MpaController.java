package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.impl.DbMpaServiceImpl;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
public class MpaController {

    private final DbMpaServiceImpl service;

    public MpaController(DbMpaServiceImpl service) {
        this.service = service;
    }

    @GetMapping
    public Collection<Mpa> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Mpa getNameById(@PathVariable Long id) {
        return service.getNameById(id);
    }

}
