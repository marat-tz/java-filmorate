package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Создание нового пользователя: {}", user.getLogin());
        if (Objects.isNull(user.getName())) {
            user = user.toBuilder()
                    .id(getNextId())
                    .name(user.getLogin())
                    .build();
        } else {
            user = user.toBuilder()
                    .id(getNextId())
                    .build();
        }
        users.put(user.getId(), user);
        log.info("Пользователь c id {} успешно добавлен", user.getId());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        Long id = newUser.getId();
        if (users.containsKey(id)) {
            log.info("Обновление данных пользователя с id: {}", id);
            // логин не трогаем
            User oldUser = newUser.toBuilder()
                    .name(newUser.getName())
                    .email(newUser.getEmail())
                    .name(newUser.getName())
                    .birthday(newUser.getBirthday())
                    .build();

            users.put(id, oldUser);
            log.info("Пользователь с id {} успешно обновлён", id);
            return oldUser;
        } else {
            log.error("Пользователь с id = " + id + " не найден");
            throw new ValidationException("Пользователь с id = " + id + " не найден");
        }
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
