package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class InMemoryGenreService implements GenreService {

    private final GenreStorage genreStorage;

    public InMemoryGenreService(@Qualifier("genreDbStorage") GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    @Override
    public Collection<Genre> findAll() {
        return genreStorage.findAll();
    }

    @Override
    public Optional<Genre> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Genre getNameById(Long id) {
        return genreStorage.getNameById(id);
    }

}
