package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.FeedRowMapper;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class FeedDbStorageImpl implements FeedStorage {

    private final JdbcTemplate jdbcTemplate;

    private final FeedRowMapper feedRowMapper;

    @Override
    public Collection<Feed> findAll() {
        log.info("Выгрузка всех событий");
        final String sqlQuery = "SELECT id, entity_id, user_id, time_stamp, event_type, operation FROM feed";
        return jdbcTemplate.query(sqlQuery, feedRowMapper::mapRowToFeed);
    }

    @Override
    public Feed findById(Long id) {
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
            throw new NotFoundException("Событие с id = " + id + " не найдено");
        }
    }

    @Override
    public Feed create(Long userId, EventType event, Operation operation, Long entityId) {
        log.info("Создание нового события");
        final long feedId;
        KeyHolder keyHolder = new GeneratedKeyHolder();

        Feed feed = Feed
                .builder()
                .timestamp(Timestamp.from(Instant.now()))
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
            stmt.setTimestamp(3, feed.getTimestamp());
            stmt.setString(4, feed.getEventType().toString());
            stmt.setString(5, feed.getOperation().toString());
            return stmt;
        }, keyHolder);

        if (Objects.nonNull(keyHolder.getKey())) {
            feedId = keyHolder.getKey().longValue();
        } else {
            throw new NotFoundException("Ошибка добавления пользователя в таблицу");
        }

        log.info("Создано новое событие с id {}", feedId);
        return findById(feedId);
    }

    @Override
    public Collection<Feed> getUserFeed(Long id) {

        String sqlQuery = "SELECT id, entity_id, user_id, time_stamp, event_type, operation " +
                "FROM feed where user_id = ?";

        return jdbcTemplate.query(sqlQuery, feedRowMapper::mapRowToFeed, id).stream().toList();
    }
}
