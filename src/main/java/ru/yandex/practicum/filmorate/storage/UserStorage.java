package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    Collection<User> findAll();

    User findById(Long id);

    User create(User user);

    User update(User newUser);

    void delete(Long id);

    void deleteFriendships(Long id);

    void deleteLikes(Long id);

    void deleteReviews(Long id);

    void deleteUseful(Long id);

    void deleteUser(Long id);
}
