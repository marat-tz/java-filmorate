package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
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
    public Optional<Mpa> findById(Long id) {
        return storage.findById(id);
    }

    @Override
    public Mpa getNameById(Long id) {
        return storage.getNameById(id);
    }
}
