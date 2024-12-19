package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;

public interface MpaService {

    Collection<Mpa> findAll();

    Optional<Mpa> findById(long id);

    String getNameById(long id);

}
