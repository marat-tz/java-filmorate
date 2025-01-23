package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {

    List<User> getFriends(Long userId);

    List<User> getCommonFriends(Long firstUserId, Long secondUserId);

    User create(User user);

    User update(User user);

    void delete(Long id);

    List<User> findAll();

    User findById(Long id);

    User addFriend(Long mainUserId, Long friendUserId);

    User removeFriend(Long mainUserId, Long friendUserId);

    List<Feed> getUserFeed(Long id);

    List<Film> getRecommendations(Long id);
}
