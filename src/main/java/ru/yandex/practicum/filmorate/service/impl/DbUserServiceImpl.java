package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.FilmLikeStorage;

import java.util.Collection;

@Service("dbUserService")
public class DbUserServiceImpl implements UserService {

    private final UserStorage storage;
    private final FeedStorage feedStorage;
    private final FriendshipStorage friendshipStorage;
    private final FilmLikeStorage filmLikeStorage;

    public DbUserServiceImpl(@Qualifier("userDbStorage") UserStorage storage, FriendshipStorage friendshipStorage,
                             FeedStorage feedStorage, FilmLikeStorage filmLikeStorage) {
        this.storage = storage;
        this.friendshipStorage = friendshipStorage;
        this.feedStorage = feedStorage;
        this.filmLikeStorage = filmLikeStorage;
    }

    @Override
    public Collection<User> getFriends(Long userId) {
        if (findById(userId) == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }

        return friendshipStorage.getFriends(userId);
    }

    @Override
    public Collection<User> getCommonFriends(Long firstUserId, Long secondUserId) {
        return friendshipStorage.getCommonFriends(firstUserId, secondUserId);
    }

    @Override
    public User create(User user) {
        return storage.create(user);
    }

    @Override
    public User update(User user) {
        return storage.update(user);
    }

    public void delete(Long id) {
        storage.delete(id);
    }

    @Override
    public Collection<User> findAll() {
        return storage.findAll();
    }

    @Override
    public User findById(Long id) {
        return storage.findById(id);
    }

    @Override
    public User addFriend(Long mainUserId, Long friendUserId) {
        return friendshipStorage.addFriend(mainUserId, friendUserId);
    }

    @Override
    public User removeFriend(Long mainUserId, Long friendUserId) {
        return friendshipStorage.removeFriend(mainUserId, friendUserId);
    }

    @Override
    public Collection<Feed> getUserFeed(Long id) {
        return feedStorage.getUserFeed(id);
    }

    @Override
    public Collection<Film> getRecommendations(Long id) {
        return filmLikeStorage.getRecommendations(id);
    }

}
