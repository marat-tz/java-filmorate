package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {

    Collection<User> getFriends(long userId);

    Collection<User> getCommonFriends(long firstUserId, long secondUserId);

    User create(User user);

    User update(User user);

    Collection<User> findAll();

    User addFriend(long mainUserId, long friendUserId);

    User removeFriend(long mainUserId, long friendUserId);

}
