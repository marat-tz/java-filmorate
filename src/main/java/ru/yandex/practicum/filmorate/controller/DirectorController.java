package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.impl.DbDirectorServiceImpl;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/directors")
public class DirectorController {
    private final DbDirectorServiceImpl service;

    public DirectorController(DbDirectorServiceImpl service) {
        this.service = service;
    }

    //GET /directors - Список всех режиссёров
    @GetMapping
    public Collection<Director> findAll() {
        log.info("Получен GET запрос /directors");
        Collection<Director> result = service.findAll();
        log.info("Отправлен GET ответ {}", result);
        return result;
    }

    //GET /directors/{id}- Получение режиссёра по id
    @GetMapping("/{id}")
    public Director getById(@PathVariable Long id) {
        log.info("Получен GET запрос /directors/{id}");
        Director result = service.getById(id);
        log.info("Отправлен GET ответ {}", result);
        return result;
    }

    //POST /directors - Создание режиссёра
    @PostMapping
    public Director create(@Valid @RequestBody Director director) {
        log.info("Получен POST запрос /directors");
        Director result = service.create(director);
        log.info("Отправлен POST ответ {}", result);
        return result;
    }

    //PUT /directors - Изменение режиссёра
    @PutMapping
    public Director update(@Valid @RequestBody Director director) {
        log.info("Получен PUT запрос /directors");
        Director result = service.update(director);
        log.info("Отправлен PUT ответ {}", result);
        return result;
    }

    //DELETE /directors/{id} - Удаление режиссёра
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Получен DELETE запрос /directors/{id}");
        service.delete(id);
    }
}
