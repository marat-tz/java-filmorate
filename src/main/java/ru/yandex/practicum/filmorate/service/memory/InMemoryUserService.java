package ru.yandex.practicum.filmorate.service.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.interfaces.UserService;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class InMemoryUserService implements UserService {

    @Qualifier("userDbStorage")
    private final UserStorage storage;

    public InMemoryUserService(@Qualifier("memoryUserDbStorage") UserStorage storage) {
        this.storage = storage;
    }

    private Optional<User> findUser(Long userId) {
        return storage.findById(userId);
    }

    @Override
    public Collection<User> getFriends(Long userId) {
        return storage.getFriends(userId);
    }

    @Override
    public Collection<User> getCommonFriends(Long firstUserId, Long secondUserId) {
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
    public Optional<User> findById(Long id) {
        return storage.findById(id);
    }

    @Override
    public User addFriend(Long mainUserId, Long friendUserId) {
        return storage.addFriend(mainUserId, friendUserId);
    }

    @Override
    public User removeFriend(Long mainUserId, Long friendUserId) {
        return storage.removeFriend(mainUserId, friendUserId);
    }
}
