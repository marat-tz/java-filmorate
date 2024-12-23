package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

public interface GenreStorage {

    Collection<Genre> findAll();

    Collection<Long> findIds();

    Optional<Genre> findById(Long id);

    Genre getNameById(Long id);

}
