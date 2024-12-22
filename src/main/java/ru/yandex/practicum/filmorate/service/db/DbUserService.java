package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.interfaces.UserService;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service("dbUserService")
public class DbUserService implements UserService {

    @Qualifier("userDbStorage")
    private final UserStorage storage;

    public DbUserService(@Qualifier("userDbStorage") UserStorage storage) {
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
