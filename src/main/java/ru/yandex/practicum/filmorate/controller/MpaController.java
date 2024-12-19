package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.InMemoryMpaService;
import ru.yandex.practicum.filmorate.service.InMemoryUserService;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/mpa")
public class MpaController {

    private final InMemoryMpaService service;

    public MpaController(InMemoryMpaService service) {
        this.service = service;
    }

    @GetMapping
    public Collection<Mpa> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Mpa> findById(long id) {
        return service.findById(id);
    }

    @GetMapping
    public String getNameById(@RequestParam Long id) {
        return service.getNameById(id);
    }

}
