package ru.yandex.practicum.filmorate.mappers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class FeedRowMapper {

    public Feed mapRowToFeed(ResultSet resultSet, int rowNum) throws SQLException {
        String event = resultSet.getString("event_type").toUpperCase();
        String operation = resultSet.getString("operation").toUpperCase();

        return Feed.builder()
                .eventId(resultSet.getLong("id"))
                .timestamp(resultSet.getLong("time_stamp"))
                .userId(resultSet.getLong("user_id"))
                .eventType(checkEvent(event))
                .operation(checkOperation(operation))
                .entityId(resultSet.getLong("entity_id"))
                .build();
    }

    private EventType checkEvent(String event) {
        return switch (event) {
            case ("LIKE") -> EventType.LIKE;
            case ("REVIEW") -> EventType.REVIEW;
            case ("FRIEND") -> EventType.FRIEND;
            default -> throw new NotFoundException("Неизвестный тип события");
        };
    }

    private Operation checkOperation(String operation) {
        return switch (operation) {
            case ("REMOVE") -> Operation.REMOVE;
            case ("ADD") -> Operation.ADD;
            case ("UPDATE") -> Operation.UPDATE;
            default -> throw new NotFoundException("Неизвестный тип операции");
        };
    }

}