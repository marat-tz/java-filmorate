package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserService {

    Collection<User> getFriends(Long userId);

    Collection<User> getCommonFriends(Long firstUserId, Long secondUserId);

    User create(User user);

    User update(User user);

    void delete(Long id);

    Collection<User> findAll();

    User findById(Long id);

    User addFriend(Long mainUserId, Long friendUserId);

    User removeFriend(Long mainUserId, Long friendUserId);

    Collection<Feed> getUserFeed(Long id);

    Collection<Film> getRecommendations(Long id);
}
