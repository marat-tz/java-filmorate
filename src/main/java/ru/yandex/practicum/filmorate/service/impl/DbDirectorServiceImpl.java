package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Service
public class DbDirectorServiceImpl implements DirectorService {
    private final DirectorStorage storage;

    public DbDirectorServiceImpl(DirectorStorage storage) {
        this.storage = storage;
    }

    @Override
    public List<Director> findAll() {
        return storage.findAll();
    }

    @Override
    public Director getById(Long id) {
        return storage.getById(id);
    }

    @Override
    public Director create(Director director) {
        return storage.create(director);
    }

    @Override
    public Director update(Director director) {
        return storage.update(director);
    }

    @Override
    public void delete(Long id) {
        storage.delete(id);
    }

}
