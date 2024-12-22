package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class InMemoryUserService implements UserService {

    @Qualifier("userDbStorage")
    private final UserStorage storage;

    public InMemoryUserService(@Qualifier("userDbStorage") UserStorage storage) {
        this.storage = storage;
    }

    private Optional<User> findUser(long userId) {
        return storage.findById(userId);
    }

    @Override
    public Collection<User> getFriends(long userId) {
        return storage.getFriends(userId);
    }

    @Override
    public Collection<User> getCommonFriends(long firstUserId, long secondUserId) {
        return storage.getCommonFriends(firstUserId, secondUserId);
    }

    @Override
    public User create(User user) {
        return storage.create(user);
    }

    @Override
    public User update(User user) {
        return storage.update(user);
    }

    @Override
    public Collection<User> findAll() {
        return storage.findAll();
    }

    @Override
    public Optional<User> findById(long id) {
        return storage.findById(id);
    }

    @Override
    public User addFriend(long mainUserId, long friendUserId) {
        return storage.addFriend(mainUserId, friendUserId);
    }

    @Override
    public User removeFriend(long mainUserId, long friendUserId) {
        return storage.removeFriend(mainUserId, friendUserId);
    }
}
