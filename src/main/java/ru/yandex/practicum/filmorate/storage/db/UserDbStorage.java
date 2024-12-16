package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


// DAO - объект доступа к данным

// Напишите в DAO соответствующие
// мапперы и методы, позволяющие сохранять пользователей в базу данных и получать их из неё.

@Slf4j
@Component("userDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserMapper mapper;

    public UserDbStorage(JdbcTemplate jdbcTemplate, UserMapper mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
    }

    @Override
    public Collection<User> findAll() {
        String sqlQuery = "SELECT id, email, login, name, birthday from users";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public Optional<User> findById(long id) {
        String sqlQuery = "SELECT id, email, login, name, birthday " +
                "from users where id = ?";

        Optional<User> resultUser = Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery,
                this::mapRowToUser, id));

        if (resultUser.isPresent()) {
            return resultUser;

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

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

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

        log.info("__Число записей jdbcTemplate равно {} __", rows);

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

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(LocalDate.parse(resultSet.getString("birthday")))
                .build();
    }
}
