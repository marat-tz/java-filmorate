package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class FilmService {

    @Autowired
    FilmStorage filmStorage;

    @Autowired
    UserStorage userStorage;

    private Optional<Film> findFilm(long id) {
        return filmStorage.findAll()
                .stream()
                .filter(film -> film.getId() == id)
                .findFirst();
    }

    private Optional<User> findUser(long id) {
        return userStorage.findAll()
                .stream()
                .filter(user -> user.getId() == id)
                .findFirst();
    }

    public Film addLike(long filmId, long userId) {
        Optional<Film> film = findFilm(filmId);
        Optional<User> user = findUser(userId);

        if (film.isPresent() && user.isPresent()) {
            film.get().getLikes().add(userId);

            log.info("Пользователь с id = {} поставил лайк фильму с id = {}", userId, filmId);
            return film.get();

        } else if (user.isEmpty()) {
            log.error("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");

        } else {
            log.error("Фильм с id = {} не найден", filmId);
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
    }

    public Film removeLike(long filmId, long userId) {
        Optional<Film> film = findFilm(filmId);
        Optional<User> user = findUser(userId);

        if (film.isPresent() && user.isPresent()) {
            film.get().getLikes().remove(userId);

            log.info("Пользователь с id = {} удалил лайк фильму с id = {}", userId, filmId);
            return film.get();

        } else if (user.isEmpty()) {
            log.error("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");

        } else {
            log.error("Фильм с id = {} не найден", filmId);
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
    }

    public List<Film> getPopularFilms(Long count) {
        Comparator<Film> filmComparator = Comparator.comparingInt(film -> film.getLikes().size());

        if (count <= 0) {
            log.error("Число отображаемых фильмов count не может быть меньше, либо равно 0");
            throw new RuntimeException("Число отображаемых фильмов count не может быть меньше, либо равно 0");
        }

        return filmStorage.findAll()
                .stream()
                .sorted(filmComparator.reversed())
                .limit(count)
                .toList();
    }
}
