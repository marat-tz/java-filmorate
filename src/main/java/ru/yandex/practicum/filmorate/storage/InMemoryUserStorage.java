package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mappers.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final UserMapper mapper;

    public InMemoryUserStorage(UserMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    private Optional<User> findUser(long userId) {
        return Optional.of(users.get(userId));
    }

    @Override
    public Optional<User> findById(long id) {
        if (users.containsKey(id)) {
            return Optional.of(users.get(id));
        }
        return Optional.empty();
    }

    @Override
    public User create(User user) {
        log.info("Создание нового пользователя: {}", user.getLogin());
        if (Objects.isNull(user.getName())) {
            user = mapper.toUserIfNoName(user);
        } else {
            user = user.toBuilder()
                    .id(getNextId())
                    .build();
        }
        users.put(user.getId(), user);
        log.info("Пользователь c id = {} успешно добавлен", user.getId());
        return user;
    }

    @Override
    public User update(User newUser) {
        Long id = newUser.getId();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        if (users.containsKey(id)) {
            log.info("Обновление данных пользователя с id = {}", id);
            User user = mapper.toUser(newUser);
            users.put(id, user);

            log.info("Пользователь с id = {} успешно обновлён", id);
            return user;

        } else {
            log.error("Пользователь с id = {} не найден", id);
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
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

            return findAll()
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
    public Collection<User> getFriends(long userId) {

        Optional<User> currentUser = findUser(userId);

        if (currentUser.isPresent()) {
            Set<Long> friends = currentUser.get().getFriendsId();

            return findAll()
                    .stream()
                    .filter(user -> friends.contains(user.getId()))
                    .toList();

        } else {
            log.error("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
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
