package ru.yandex.practicum.filmorate.storage.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.memory.InMemoryUserService;
import ru.yandex.practicum.filmorate.service.interfaces.UserService;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


@Slf4j
@Component("memoryFilmDbStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private final FilmMapper mapper;
    private final FilmStorage filmStorage;
    private final UserService userService;

    public InMemoryFilmStorage(FilmMapper mapper, FilmStorage filmStorage, InMemoryUserService userService) {
        this.mapper = mapper;
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film findById(Long id) {
        if (films.containsKey(id)) {
            return films.get(id);
        }
        throw new NotFoundException("Фильм не найден");
    }

    @Override
    public Film create(Film film) {
        log.info("Добавление нового фильма: {}", film.getName());
        film = film.toBuilder().id(getNextId()).build();
        films.put(film.getId(), film);
        log.info("Фильм c id = {} успешно добавлен", film.getId());
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        Long id = newFilm.getId();
        Film film;
        if (films.containsKey(id)) {
            log.info("Обновление фильма с id = {}", id);
            film = mapper.toFilm(newFilm);
            films.put(id, film);
            log.info("Фильм с id = {} успешно обновлён", id);
            return film;
        } else {
            log.error("Фильм с id = {} не найден", id);
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
    }

    @Override
    public void addLike(long filmId, long userId) {
        Film film = findFilm(filmId);
        Optional<User> user = findUser(userId);

        if (Objects.nonNull(film) && user.isPresent()) {
            film.getLikes().add(userId);

            log.info("Пользователь с id = {} поставил лайк фильму с id = {}", userId, filmId);
            //return film.get();

        } else if (user.isEmpty()) {
            log.error("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");

        } else {
            log.error("Фильм с id = {} не найден", filmId);
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
    }

    @Override
    public void removeLike(long filmId, long userId) {
        Film film = findFilm(filmId);
        Optional<User> user = findUser(userId);

        if (Objects.nonNull(film) && user.isPresent()) {
            film.getLikes().remove(userId);

            log.info("Пользователь с id = {} удалил лайк фильму с id = {}", userId, filmId);

        } else if (user.isEmpty()) {
            log.error("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");

        } else {
            log.error("Фильм с id = {} не найден", filmId);
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
    }

    @Override
    public List<Film> getPopularFilms(Long count) {
        Comparator<Film> filmComparator = Comparator.comparingInt(film -> film.getLikes().size());

        if (count <= 0) {
            log.error("Число отображаемых фильмов count не может быть меньше, либо равно 0");
            throw new RuntimeException("Число отображаемых фильмов count не может быть меньше, либо равно 0");
        }

        return findAll()
                .stream()
                .sorted(filmComparator.reversed())
                .limit(count)
                .toList();
    }

    private Film findFilm(long id) {
        return filmStorage.findById(id);
    }

    private Optional<User> findUser(long id) {
        return userService.findById(id);
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
