package storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    private final MpaStorage mpaStorage;

    @BeforeEach
    public void beforeEach() {
        User user = User
                .builder()
                .id(1L)
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

        Film film = Film
                .builder()
                .id(1L)
                .name("test")
                .description("test desc")
                .releaseDate(LocalDate.of(1995, 10, 10))
                .duration(100)
                .mpa(mpa)
                .build();
        filmStorage.create(film);
    }

    @Test
    void findById_shouldFindFilmById() {
        Film film = filmStorage.findById(1L);
        assertEquals(1L, film.getId());
    }

}
