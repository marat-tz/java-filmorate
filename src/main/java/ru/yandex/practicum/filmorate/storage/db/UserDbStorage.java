package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mappers.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    public Optional<User> findById(Long id) {
        Optional<User> resultUser;

        String sqlQuery = "SELECT id, email, login, name, birthday " +
                "from users where id = ?";

        try {
            resultUser = Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery,
                    this::mapRowToUser, id));
        } catch (EmptyResultDataAccessException e) {
            resultUser = Optional.empty();
        }

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
    public User addFriend(Long user1Id, Long user2Id) {
        if (Objects.equals(user1Id, user2Id)) {
            log.error("Нельзя добавить в друзья самого себя");
            throw new ValidationException("Нельзя добавить в друзья самого себя");
        }

        Optional<User> mainUser = findById(user1Id);
        Optional<User> friendUser = findById(user2Id);

        if (mainUser.isPresent() && friendUser.isPresent()) {

            String sqlQueryUser2 = "SELECT user2_id " +
                    "FROM friendship WHERE user1_id = ?";

            String sqlQueryAddFriend = "INSERT INTO friendship(user1_id, user2_id) values (?, ?)";

            List<Long> user2Ids = jdbcTemplate.queryForList(sqlQueryUser2, Long.class, user1Id);

            if (!user2Ids.contains(user2Id)) {
                jdbcTemplate.update(connection -> {
                    PreparedStatement stmt = connection.prepareStatement(sqlQueryAddFriend);
                    stmt.setLong(1, user1Id);
                    stmt.setLong(2, user2Id);
                    return stmt;
                });
            }

            mainUser.get().getFriends().add(friendUser.get());

            log.info("Пользователь с id = {} добавил в друзья пользователя с id = {}", user1Id, user2Id);
            return mainUser.get();

        } else if (mainUser.isEmpty()) {
            log.error("Пользователь с id = {} не найден", user1Id);
            throw new NotFoundException("Пользователь с id = " + user1Id + " не найден");

        } else {
            log.error("Пользователь с id = {} не найден", user2Id);
            throw new NotFoundException("Пользователь с id = " + user2Id + " не найден");
        }
    }

    @Override
    public User removeFriend(Long mainUserId, Long friendUserId) {
        log.info("Удаление из друзей");
        if (mainUserId == friendUserId) {
            log.error("Нельзя удалить из друзей самого себя");
            throw new ValidationException("Нельзя удалить из друзей самого себя");
        }

        Optional<User> mainUser = findById(mainUserId);
        Optional<User> friendUser = findById(friendUserId);

        if (mainUser.isPresent() && friendUser.isPresent()) {

            String sqlDeleteFriend = "DELETE FROM friendship WHERE user1_id = ? AND user2_id = ?";

            int deletedRows = jdbcTemplate.update(sqlDeleteFriend, mainUserId, friendUserId);
            log.info("Удалено {} строк", deletedRows);

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
        String sqlCommonFriends = "SELECT id, email, login, name, birthday FROM users " +
        "JOIN friendship AS fri ON users.id = fri.user2_id " +
        "JOIN friendship AS fri2 ON users.id = fri2.user2_id " +
        "WHERE fri.user1_id = ? AND fri2.user1_id = ? ";

        if (findById(firstUserId).isPresent() && findById(secondUserId).isPresent()) {

            return jdbcTemplate.query(sqlCommonFriends, this::mapRowToUser, firstUserId, secondUserId);

        } else if (findById(firstUserId).isEmpty()) {
            log.error("Пользователь с id = {} не найден", firstUserId);
            throw new NotFoundException("Пользователь с id = " + firstUserId + " не найден");

        } else {
            log.error("Пользователь с id = {} не найден", secondUserId);
            throw new NotFoundException("Пользователь с id = " + secondUserId + " не найден");
        }
    }

    @Override
    public Collection<User> getFriends(Long userId) {

        String sqlQueryUser2 = "SELECT user2_id " +
                "FROM friendship WHERE user1_id = ?";

        List<Long> friendsId = jdbcTemplate.queryForList(sqlQueryUser2, Long.class, userId);
        List<User> allUsers = findAll().stream().toList();
        List<User> result = new ArrayList<>();

        for (User user : allUsers) {
            if (friendsId.contains(user.getId())) {
                result.add(user);
            }
        }

        return result;
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
