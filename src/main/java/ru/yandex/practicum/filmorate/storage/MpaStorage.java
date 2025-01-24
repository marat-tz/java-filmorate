package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaStorage {

    List<Mpa> findAll();

    Mpa findById(Integer id);

    Mpa getNameById(Long id);

    Integer getCountById(Film film);

}
