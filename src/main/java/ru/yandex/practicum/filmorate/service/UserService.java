package ru.yandex.practicum.filmorate.service;

import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserService {

    Collection<User> getFriends(Long userId);

    Collection<User> getCommonFriends(Long firstUserId, Long secondUserId);

    User create(User user);

    User update(User user);

    Collection<User> findAll();

    User findById(Long id);

    User addFriend(Long mainUserId, Long friendUserId);

    User removeFriend(Long mainUserId, Long friendUserId);

    Collection<Feed> getUserFeed(Long id);

}
