package ru.yandex.practicum.filmorate.mappers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmRowMappers {

    private final MpaStorage mpaStorage;
    private final DirectorStorage directorStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final FilmLikeStorage filmLikeStorage;

    public Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {

        Integer mpaId = resultSet.getInt("mpa_id");
        Mpa mpa = mpaStorage.findById(mpaId);
        List<Genre> genres = filmGenreStorage.getListGenreFromDbGenres(resultSet.getLong("id"));
        Long likes = filmLikeStorage.getLikesById(resultSet.getLong("id"));
        List<Director> directors = directorStorage.getDirectorsByFilm(resultSet.getLong("id"));
        LocalDate date = LocalDate.of(1895, 12,28);
        if (resultSet.getString("releaseDate") != null) {
            date = LocalDate.parse(resultSet.getString("releaseDate"));
        }

        return Film.builder()
                .id(resultSet.getLong("id"))
                .likes(likes)
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(date)
                .duration(resultSet.getInt("duration"))
                .mpa(mpa)
                .genres(genres)
                .directors(directors)
                .build();
    }
}
