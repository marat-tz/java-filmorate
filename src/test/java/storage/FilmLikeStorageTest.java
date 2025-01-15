package storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmLikeStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final FilmLikeStorage filmLikeStorage;
    private final MpaStorage mpaStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final GenreStorage genreStorage;

    @BeforeEach
    public void init() {
        User user = User
                .builder()
                .email("test@test.com")
                .login("test")
                .name("test")
                .birthday(LocalDate.of(1995, 10, 10))
                .build();
        userStorage.create(user);

        Mpa mpa = Mpa
                .builder()
                .id(1L)
                .name("test")
                .build();

        Genre genre1 = Genre
                .builder()
                .id(1L)
                .name("test")
                .build();

        Genre genre2 = Genre
                .builder()
                .id(2L)
                .name("test")
                .build();

        List<Genre> genres1 = new ArrayList<>();
        List<Genre> genres2 = new ArrayList<>();
        genres1.add(genre1);
        genres2.add(genre2);

        Film film1 = Film
                .builder()
                .name("test")
                .description("test desc")
                .releaseDate(LocalDate.of(1995, 10, 10))
                .duration(100)
                .mpa(mpa)
                .genres(genres1)
                .build();

        Film film2 = Film
                .builder()
                .name("test")
                .description("test desc")
                .releaseDate(LocalDate.of(1995, 10, 10))
                .duration(100)
                .mpa(mpa)
                .genres(genres1)
                .build();

        Film film3 = Film
                .builder()
                .name("test")
                .description("test desc")
                .releaseDate(LocalDate.of(1996, 10, 10))
                .duration(100)
                .mpa(mpa)
                .genres(genres2)
                .build();

        filmStorage.create(film1);
        filmStorage.create(film2);
        filmStorage.create(film3);
    }

    @AfterEach
    public void cleanTables() {
        jdbcTemplate.update("DROP TABLE IF EXISTS films CASCADE;");
    }

    @Test
    void getPopularFilms_shouldReturnDescList() {
        filmLikeStorage.addLike(1L, 1L);
        filmLikeStorage.addLike(2L, 1L);
        filmLikeStorage.addLike(3L, 1L);

        List<Film> films = filmLikeStorage.getPopularFilms(10L, null, null);

        assertEquals(1, films.get(0).getId());
        assertEquals(2, films.get(1).getId());
        assertEquals(3, films.get(2).getId());
    }

    @Test
    void getPopularFilms_shouldReturnDescListFilterGenres() {
        filmLikeStorage.addLike(1L, 1L);
        filmLikeStorage.addLike(2L, 1L);
        filmLikeStorage.addLike(3L, 1L);

        List<Film> films = filmLikeStorage.getPopularFilms(10L, 1L, null);

        assertEquals(1, films.get(0).getId());
        assertEquals(2, films.get(1).getId());
        assertEquals(2, films.size());
    }

    @Test
    void getPopularFilms_shouldReturnDescListFilterYear() {
        filmLikeStorage.addLike(1L, 1L);
        filmLikeStorage.addLike(2L, 1L);
        filmLikeStorage.addLike(3L, 1L);

        List<Film> films = filmLikeStorage.getPopularFilms(10L, null, 1995L);

        assertEquals(1, films.get(0).getId());
        assertEquals(2, films.get(1).getId());
        assertEquals(2, films.size());
    }

    @Test
    void getPopularFilms_shouldReturnDescListFilterAll() {
        filmLikeStorage.addLike(1L, 1L);
        filmLikeStorage.addLike(2L, 1L);
        filmLikeStorage.addLike(3L, 1L);

        List<Film> films = filmLikeStorage.getPopularFilms(10L, 2L, 1996L);

        assertEquals(3, films.get(0).getId());
        assertEquals(1, films.size());
    }

}
