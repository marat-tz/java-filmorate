package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MpaStorage {

    Collection<Mpa> findAll();

    Mpa findById(Integer id);

    Mpa getNameById(Long id);

}
