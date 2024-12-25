package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;

@Slf4j
@Service
public class DbMpaServiceImpl implements MpaService {

    private final MpaStorage storage;

    public DbMpaServiceImpl(@Qualifier("mpaDbStorage") MpaStorage storage) {
        this.storage = storage;
    }

    @Override
    public Collection<Mpa> findAll() {
        return storage.findAll();
    }

    @Override
    public Mpa findById(Integer id) {
        return storage.findById(id);
    }

    @Override
    public Mpa getNameById(Long id) {
        return storage.getNameById(id);
    }
}
