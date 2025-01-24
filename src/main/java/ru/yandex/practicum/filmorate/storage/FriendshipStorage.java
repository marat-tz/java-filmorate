package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendshipStorage {

    User addFriend(Long user1Id, Long user2Id);

    User removeFriend(Long mainUserId, Long friendUserId);

    List<User> getCommonFriends(Long firstUserId, Long secondUserId);

    List<User> getFriends(Long userId);

}
