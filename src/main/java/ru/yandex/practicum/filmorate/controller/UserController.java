package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(@Qualifier("dbUserService") UserService service) {
        this.service = service;
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("Получен GET запрос /users");
        Collection<User> result = service.findAll();
        log.info("Отправлен GET ответ {}", result);
        return service.findAll();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable Long id) {
        log.info("Получен GET запрос /users/{}", id);
        User result = service.findById(id);
        log.info("Отправлен GET ответ {}", result);
        return result;
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Получен POST запрос /users");
        User result = service.create(user);
        log.info("Отправлен POST ответ {}", result);
        return result;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        log.info("Получен PUT запрос /users");
        User result = service.update(newUser);
        log.info("Отправлен PUT ответ {}", result);
        return result;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        log.info("Получен DELETE запрос /id  с телом {}", id);
        service.delete(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Получен PUT запрос /users/{}/friends/{}", id, friendId);
        User result = service.addFriend(id, friendId);
        log.info("Отправлен PUT ответ {}", result);
        return result;
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Получен DELETE запрос /users/{}/friends/{}", id, friendId);
        User result = service.removeFriend(id, friendId);
        log.info("Отправлен DELETE ответ {}", result);
        return result;
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable Long id) {
        log.info("Получен GET запрос /users/{}/friends/", id);
        Collection<User> result = service.getFriends(id);
        log.info("Отправлен GET ответ {}", result);
        return result;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Получен GET запрос /users/{}/friends/common/{}", id, otherId);
        Collection<User> result = service.getCommonFriends(id, otherId);
        log.info("Отправлен GET ответ {}", result);
        return result;
    }

    @GetMapping("/{id}/feed")
    public Collection<Feed> getFeed(@PathVariable Long id) {
        log.info("Получен GET запрос /users/{}/feed", id);
        Collection<Feed> result = service.getUserFeed(id);
        log.info("Отправлен GET ответ {}", result);
        return result;
    }

    @GetMapping("/{id}/recommendations")
    public Collection<Film> getRecommendations(@PathVariable Long id) {
        log.info("Получен GET запрос /users/{}/recommendations", id);
        Collection<Film> result = service.getRecommendations(id);
        log.info("Отправлен GET ответ {}", result);
        return result;
    }
}
