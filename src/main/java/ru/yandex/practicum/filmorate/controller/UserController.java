package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserStorage storage;

    @GetMapping
    public Collection<User> findAll() {
        return storage.findAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return storage.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        return storage.update(newUser);
    }
}
