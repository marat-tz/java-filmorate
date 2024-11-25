package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
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

    private final UserStorage storage;

    public InMemoryUserService(UserStorage storage) {
        this.storage = storage;
    }

    private Optional<User> findUser(long userId) {
        return storage.findById(userId);
    }

    @Override
    public Collection<User> getFriends(long userId) {

        Optional<User> currentUser = findUser(userId);

        if (currentUser.isPresent()) {
            Set<Long> friends = currentUser.get().getFriendsId();

            return storage.findAll()
                    .stream()
                    .filter(user -> friends.contains(user.getId()))
                    .toList();

        } else {
            log.error("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
    }

    @Override
    public Collection<User> getCommonFriends(long firstUserId, long secondUserId) {

        Optional<User> firstUser = findUser(firstUserId);
        Optional<User> secondUser = findUser(secondUserId);

        if (firstUser.isPresent() && secondUser.isPresent()) {
            Set<Long> firstFriends = firstUser.get().getFriendsId();
            Set<Long> secondFriends = secondUser.get().getFriendsId();

            List<Long> commonIds = firstFriends
                    .stream()
                    .filter(secondFriends::contains)
                    .toList();

            return storage.findAll()
                    .stream()
                    .filter(user -> commonIds.contains(user.getId()))
                    .toList();

        } else if (firstUser.isEmpty()) {
            log.error("Пользователь с id = {} не найден", firstUserId);
            throw new NotFoundException("Пользователь с id = " + firstUserId + " не найден");

        } else {
            log.error("Пользователь с id = {} не найден", secondUserId);
            throw new NotFoundException("Пользователь с id = " + secondUserId + " не найден");
        }
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

        if (mainUserId == friendUserId) {
            log.error("Нельзя добавить в друзья самого себя");
            throw new ValidationException("Нельзя добавить в друзья самого себя");
        }

        Optional<User> mainUser = findUser(mainUserId);
        Optional<User> friendUser = findUser(friendUserId);

        if (mainUser.isPresent() && friendUser.isPresent()) {
            mainUser.get().getFriendsId().add(friendUserId);
            friendUser.get().getFriendsId().add(mainUserId);

            log.info("Пользователь с id = {} добавил в друзья пользователя с id = {}", mainUserId, friendUserId);
            return mainUser.get();

        } else if (mainUser.isEmpty()) {
            log.error("Пользователь с id = {} не найден", mainUserId);
            throw new NotFoundException("Пользователь с id = " + mainUserId + " не найден");

        } else {
            log.error("Пользователь с id = {} не найден", friendUserId);
            throw new NotFoundException("Пользователь с id = " + friendUserId + " не найден");
        }
    }

    @Override
    public User removeFriend(long mainUserId, long friendUserId) {

        if (mainUserId == friendUserId) {
            log.error("Нельзя добавить в друзья самого себя");
            throw new ValidationException("Нельзя добавить в друзья самого себя");
        }

        Optional<User> mainUser = findUser(mainUserId);
        Optional<User> friendUser = findUser(friendUserId);

        if (mainUser.isPresent() && friendUser.isPresent()) {
            mainUser.get().getFriendsId().remove(friendUserId);
            friendUser.get().getFriendsId().remove(mainUserId);

            log.info("Пользователь с id = {} удалил из друзей пользователя с id = {}", mainUserId, friendUserId);
            return mainUser.get();

        } else if (mainUser.isEmpty()) {
            log.error("Пользователь с id = {} не найден", mainUserId);
            throw new NotFoundException("Пользователь с id = " + mainUserId + " не найден");

        } else {
            log.error("Пользователь с id = {} не найден", friendUserId);
            throw new NotFoundException("Пользователь с id = " + friendUserId + " не найден");
        }
    }
}
