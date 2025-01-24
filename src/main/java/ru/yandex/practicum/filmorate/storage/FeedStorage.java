package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.util.List;

public interface FeedStorage {

    List<Feed> findAll();

    Feed findById(Long id);

    Feed create(Long userId, EventType event, Operation operation, Long entityId);

    List<Feed> getUserFeed(Long id);

}
