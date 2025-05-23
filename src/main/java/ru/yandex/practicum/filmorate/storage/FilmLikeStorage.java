package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmLikeStorage {

    void addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);

    List<Film> getPopularFilms(Long count, Long genreId, Long year);

    Long getLikesById(Long id);

    List<Film> getCommonFilms(Long userId, Long friendUserId);

    List<Film> getRecommendations(Long userId);
}
