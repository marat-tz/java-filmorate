package ru.yandex.practicum.filmorate.storage.interfaces;

import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Collection<User> findAll();

    Optional<User> findById(long id);

    User create(User user);

    User update(User newUser);

    User addFriend(long mainUserId, long friendUserId);

    User removeFriend(long mainUserId, long friendUserId);

    Collection<User> getCommonFriends(long firstUserId, long secondUserId);

    Collection<User> getFriends(long userId);

}
