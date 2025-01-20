package ru.yandex.practicum.filmorate.storage.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.mappers.mapstruct.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorageImpl implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserMapper mapper;
    private final UserRowMapper userRowMapper;

    @Override
    public Collection<User> findAll() {
        String sqlQuery = "SELECT id, email, login, name, birthday from users";
        return jdbcTemplate.query(sqlQuery, userRowMapper::mapRowToUser);
    }

    @Override
    public User findById(Long id) {
        Optional<User> resultUser;

        String sqlQuery = "SELECT id, email, login, name, birthday " +
                "from users where id = ?";

        try {
            resultUser = Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery,
                    userRowMapper::mapRowToUser, id));
        } catch (EmptyResultDataAccessException e) {
            resultUser = Optional.empty();
        }

        if (resultUser.isPresent()) {
            return resultUser.get();

        } else {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
    }

    @Override
    public User create(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final User finalUser;
        final Long userId;

        log.info("Создание нового пользователя: {}", user.getLogin());

        String sqlQuery = "INSERT INTO users(email, login, name, birthday) " +
                "values (?, ?, ?, ?)";

        if (Objects.isNull(user.getName())) {
            finalUser = mapper.toUserIfNoName(user);
        } else {
            finalUser = mapper.toUser(user);
        }

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, finalUser.getEmail());
            stmt.setString(2, finalUser.getLogin());
            stmt.setString(3, finalUser.getName());
            stmt.setString(4, finalUser.getBirthday().toString());
            return stmt;
        }, keyHolder);

        if (Objects.nonNull(keyHolder.getKey())) {
            userId = keyHolder.getKey().longValue();
        } else {
            throw new NotFoundException("Ошибка добавления пользователя в таблицу");
        }

        log.info("Пользователь c login = {} успешно добавлен", user.getLogin());
        return User.builder()
                .id(userId)
                .email(finalUser.getEmail())
                .login(finalUser.getLogin())
                .name(finalUser.getName())
                .birthday(finalUser.getBirthday())
                .build();
    }

    @Override
    public User update(User newUser) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final long userId;

        log.info("Обновление данных пользователя с id = {}", newUser.getId());

        String sqlQuery = "UPDATE users SET " +
                "email = ?, login = ?, name = ?, birthday = ? " +
                "where id = ?";

        int rows = jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, newUser.getEmail());
            stmt.setString(2, newUser.getLogin());
            stmt.setString(3, newUser.getName());
            stmt.setString(4, newUser.getBirthday().toString());
            stmt.setLong(5, newUser.getId());
            return stmt;
        }, keyHolder);

        if (Objects.nonNull(keyHolder.getKey())) {
            userId = keyHolder.getKey().longValue();
        } else {
            throw new NotFoundException("Ошибка обновления пользователя");
        }

        User resultUser = User.builder()
                .id(userId)
                .email(newUser.getEmail())
                .login(newUser.getLogin())
                .name(newUser.getName())
                .birthday(newUser.getBirthday())
                .build();

        if (rows > 0) {
            log.info("Пользователь с id = {} успешно обновлён", userId);
            return resultUser;

        } else {
            log.error("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        try {
            deleteFriendships(id);
            deleteLikes(id);
            deleteReviews(id);
            deleteUseful(id);
            deleteUser(id);
            log.info("Пользователь с id = {} был успешно удален.", id);
        } catch (Exception e) {
            log.error("Ошибка при удалении пользователя с id = {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public void deleteFriendships(Long id) {
        String sql = "DELETE FROM friendship WHERE user1_id = ? OR user2_id = ?";
        jdbcTemplate.update(sql, id, id);
    }

    @Override
    public void deleteLikes(Long id) {
        String sql = "DELETE FROM film_like WHERE user_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void deleteReviews(Long id) {
        String sql = "DELETE FROM reviews WHERE user_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void deleteUseful(Long id) {
        String sql = "DELETE FROM useful WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, id, id);
    }

    @Override
    public void deleteUser(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
