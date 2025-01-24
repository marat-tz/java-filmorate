package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.FeedRowMapper;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component("feedDbStorage")
@RequiredArgsConstructor
public class FeedDbStorageImpl implements FeedStorage {

    private final JdbcTemplate jdbcTemplate;

    private final FeedRowMapper feedRowMapper;

    @Override
    public List<Feed> findAll() {
        log.info("Выгрузка всех событий");
        final String sqlQuery = "SELECT id, entity_id, user_id, time_stamp, event_type, operation FROM feed";
        return jdbcTemplate.query(sqlQuery, feedRowMapper::mapRowToFeed);
    }

    @Override
    public Feed findById(Long id) {
        log.info("Поиск события по id {}", id);
        Optional<Feed> resultFeed;

        String sqlQuery = "SELECT id, entity_id, user_id, time_stamp, event_type, operation " +
                "FROM feed where id = ?";

        try {
            resultFeed = Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery,
                    feedRowMapper::mapRowToFeed, id));
        } catch (EmptyResultDataAccessException e) {
            resultFeed = Optional.empty();
        }

        if (resultFeed.isPresent()) {
            return resultFeed.get();

        } else {
            log.error("Событие с id {} не найдено", id);
            throw new NotFoundException("Событие с id = " + id + " не найдено");
        }
    }

    @Override
    public Feed create(Long userId, EventType event, Operation operation, Long entityId) {
        log.info("Создание события. Пользователь user_id = {} выполнил действие {} {} сущности entity_id = {}",
                userId, event, operation, entityId);
        final long feedId;
        KeyHolder keyHolder = new GeneratedKeyHolder();

        Feed feed = Feed
                .builder()
                .timestamp(System.currentTimeMillis())
                .userId(userId)
                .eventType(event)
                .operation(operation)
                .entityId(entityId)
                .build();

        String sqlQuery = "INSERT INTO feed(entity_id, user_id, time_stamp, event_type, operation) " +
                "values (?, ?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setLong(1, feed.getEntityId());
            stmt.setLong(2, feed.getUserId());
            stmt.setLong(3, feed.getTimestamp());
            stmt.setString(4, feed.getEventType().toString());
            stmt.setString(5, feed.getOperation().toString());
            return stmt;
        }, keyHolder);

        if (Objects.nonNull(keyHolder.getKey())) {
            feedId = keyHolder.getKey().longValue();
        } else {
            log.error("Ошибка добавления пользователя {} в таблицу", userId);
            throw new NotFoundException("Ошибка добавления пользователя в таблицу");
        }

        log.info("Создано новое событие с id {}", feedId);
        return findById(feedId);
    }

    @Override
    public List<Feed> getUserFeed(Long id) {
        log.info("Получаем события для пользователя с id {}", id);

        // запрос на события друзей
        String sqlQueryFriends = "SELECT Distinct fe.id, fe.entity_id, fe.user_id, fe.time_stamp, " +
                "fe.event_type, fe.operation " +
                "FROM feed fe " +
                "WHERE fe.user_id = ? " +
                "ORDER BY fe.time_stamp";

        List<Feed> result = jdbcTemplate.query(sqlQueryFriends, feedRowMapper::mapRowToFeed, id).stream().toList();

        log.info("Возвращение списка Feed в методе getUserFeed {}", result);
        return result;
    }
}
