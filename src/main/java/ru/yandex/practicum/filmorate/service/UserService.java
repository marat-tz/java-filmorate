package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserService {

    Collection<User> getFriends(Long userId);

    Collection<User> getCommonFriends(Long firstUserId, Long secondUserId);

    User create(User user);

    User update(User user);

    Collection<User> findAll();

    Optional<User> findById(Long id);

    User addFriend(Long mainUserId, Long friendUserId);

    User removeFriend(Long mainUserId, Long friendUserId);

}
