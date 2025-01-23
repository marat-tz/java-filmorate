package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> findAll();

    User findById(Long id);

    User create(User user);

    User update(User newUser);

    void delete(Long id);
}
