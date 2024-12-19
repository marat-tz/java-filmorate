package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;

public class InMemoryMpaService implements MpaService {

    private final MpaStorage storage;

    public InMemoryMpaService(@Qualifier("mpaDbStorage") MpaStorage storage) {
        this.storage = storage;
    }

    @Override
    public Collection<Mpa> findAll() {
        return storage.findAll();
    }

    @Override
    public Optional<Mpa> findById(long id) {
        return storage.findById(id);
    }

    @Override
    public String getNameById(long id) {
        return storage.getNameById(id);
    }
}
