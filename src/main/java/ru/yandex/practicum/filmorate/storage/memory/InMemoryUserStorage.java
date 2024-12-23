package ru.yandex.practicum.filmorate.storage.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mappers.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component("memoryUserDbStorage")
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

    private Optional<User> findUser(Long userId) {
        return Optional.of(users.get(userId));
    }

    @Override
    public Optional<User> findById(Long id) {
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
    public User addFriend(Long mainUserId, Long friendUserId) {

        if (mainUserId == friendUserId) {
            log.error("Нельзя добавить в друзья самого себя");
            throw new ValidationException("Нельзя добавить в друзья самого себя");
        }

        Optional<User> mainUser = findUser(mainUserId);
        Optional<User> friendUser = findUser(friendUserId);

        if (mainUser.isPresent() && friendUser.isPresent()) {
            mainUser.get().getFriends().add(friendUser.get());
            friendUser.get().getFriends().add(mainUser.get());

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
    public User removeFriend(Long mainUserId, Long friendUserId) {

        if (mainUserId == friendUserId) {
            log.error("Нельзя удалить из друзей самого себя");
            throw new ValidationException("Нельзя удалить из друзей самого себя");
        }

        Optional<User> mainUser = findUser(mainUserId);
        Optional<User> friendUser = findUser(friendUserId);

        if (mainUser.isPresent() && friendUser.isPresent()) {
            mainUser.get().getFriends().remove(friendUserId);
            friendUser.get().getFriends().remove(mainUserId);

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
    public Collection<User> getCommonFriends(Long firstUserId, Long secondUserId) {

        Optional<User> firstUser = findUser(firstUserId);
        Optional<User> secondUser = findUser(secondUserId);

        if (firstUser.isPresent() && secondUser.isPresent()) {
            Set<User> firstFriends = firstUser.get().getFriends();
            Set<User> secondFriends = secondUser.get().getFriends();

            List<User> commonUsers = firstFriends
                    .stream()
                    .filter(secondFriends::contains)
                    .toList();

            return findAll()
                    .stream()
                    .filter(commonUsers::contains)
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
    public Collection<User> getFriends(Long userId) {

        Optional<User> currentUser = findUser(userId);

        if (currentUser.isPresent()) {
            Set<User> friends = currentUser.get().getFriends();

            return findAll()
                    .stream()
                    .filter(friends::contains)
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
