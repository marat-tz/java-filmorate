package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
