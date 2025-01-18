package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.sql.Timestamp;

@Value
@Builder(toBuilder = true)
public class Feed {

    Long timestamp;

    Long userId;

    EventType eventType;

    Operation operation;

    Long eventId;

    Long entityId;

}
