package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;

public interface GenreStorage {

    Collection<Genre> findAll();

    Optional<Genre> findById(Long id);

    Genre getNameById(Long id);

}
