package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface FriendshipStorage {

    User addFriend(Long user1Id, Long user2Id);

    User removeFriend(Long mainUserId, Long friendUserId);

    Collection<User> getCommonFriends(Long firstUserId, Long secondUserId);

    Collection<User> getFriends(Long userId);

}
