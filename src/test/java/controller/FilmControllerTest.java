package controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmControllerTest {

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

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

        Film film = Film
                .builder()
                .id(1L)
                .name("test")
                .description("test desc")
                .releaseDate(LocalDate.of(1995, 10, 10))
                .duration(100)
                .build();

        filmStorage.create(film);
    }

//    @AfterEach
//    public void afterEach() {
//        jdbcTemplate.update(FileReader.readString("src/test/resources/drop.sql"));
//    }

    @Test
    void testFindUserById() {
        Optional<User> userOptional = userStorage.findById(1L);

        assertTrue(userOptional.isPresent());
    }

}
