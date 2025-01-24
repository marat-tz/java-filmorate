package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorService {

    List<Director> findAll();

    Director getById(Long id);

    Director create(Director director);

    Director update(Director director);

    void delete(Long id);
}
