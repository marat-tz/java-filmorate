package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.util.Collection;
import java.util.Optional;

public interface FeedStorage {

    Collection<Feed> findAll();

    Feed findById(Long id);

    Feed create(Long userId, EventType event, Operation operation, Long entityId);

    Collection<Feed> getUserFeed(Long id);

}
