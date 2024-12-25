package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

public interface MpaService {

    Collection<Mpa> findAll();

    Mpa findById(Integer id);

    Mpa getNameById(Long id);

}
