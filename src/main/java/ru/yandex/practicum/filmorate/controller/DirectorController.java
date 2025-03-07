package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.impl.DbDirectorServiceImpl;

import java.util.List;


@RestController
@RequestMapping("/directors")
public class DirectorController {
    private final DbDirectorServiceImpl service;

    public DirectorController(DbDirectorServiceImpl service) {
        this.service = service;
    }

    //GET /directors - Список всех режиссёров
    @GetMapping
    public List<Director> findAll() {
        return service.findAll();
    }

    //GET /directors/{id}- Получение режиссёра по id
    @GetMapping("/{id}")
    public Director getById(@PathVariable Long id) {
        return service.getById(id);
    }

    //POST /directors - Создание режиссёра
    @PostMapping
    public Director create(@Valid @RequestBody Director director) {
        return service.create(director);
    }

    //PUT /directors - Изменение режиссёра
    @PutMapping
    public Director update(@Valid @RequestBody Director director) {
        return service.update(director);
    }

    //DELETE /directors/{id} - Удаление режиссёра
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
