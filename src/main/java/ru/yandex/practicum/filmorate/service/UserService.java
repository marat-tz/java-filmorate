package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserService {

    Collection<User> getFriends(long userId);

    Collection<User> getCommonFriends(long firstUserId, long secondUserId);

    User create(User user);

    User update(User user);

    Collection<User> findAll();

    Optional<User> findById(long id);

    User addFriend(long mainUserId, long friendUserId);

    User removeFriend(long mainUserId, long friendUserId);

}
