package ru.yandex.practicum.filmorate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.adapters.LocalDateAdapter;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerTests {

    @Autowired
    MockMvc mvc;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    private String createFilm(Long id, String name, String description, LocalDate releaseDate, Integer duration) {
        Film film = Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .releaseDate(releaseDate)
                .duration(duration)
                .build();
        return gson.toJson(film);
    }

    private String createUser(Long id, String email, String login, String name, LocalDate birth) {
        User user = User.builder()
                .id(id)
                .email(email)
                .login(login)
                .name(name)
                .birthday(birth)
                .build();
        return gson.toJson(user);
    }

    @Test
    void findAll_shouldFindAllFilms() throws Exception {
        mvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createFilm(null, "Name", "Description",
                                LocalDate.of(2001, 9, 11), 100)))
                .andExpect(status().isOk());

        mvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void create_shouldCreateFilm() throws Exception {
        mvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createFilm(null, "TestName", "Description",
                                LocalDate.of(2001, 9, 11), 100)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("TestName"));
    }

    @Test
    void create_shouldNotCreateEmptyName() throws Exception {
        mvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createFilm(null, "", "Description",
                                LocalDate.of(2001, 9, 11), 100)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_shouldNotCreateDescriptionMore200() throws Exception {
        mvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createFilm(null, "Name", "g".repeat(201),
                                LocalDate.of(2001, 9, 11), 100)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_shouldNotCreateDurationEmpty() throws Exception {
        mvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createFilm(null, "Name", "Description",
                                LocalDate.of(2001, 9, 11), null)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_shouldNotCreateDurationNegative() throws Exception {
        mvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createFilm(null, "Name", "Description",
                                LocalDate.of(2001, 9, 11), -1)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_shouldUpdateFilm() throws Exception {
        mvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createFilm(null, "Name", "Description",
                                LocalDate.of(2001, 9, 11), 100)))
                .andExpect(status().isOk());

        mvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createFilm(1L, "Name1", "Description1",
                                LocalDate.of(2002, 9, 11), 101)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Name1"))
                .andExpect(jsonPath("$.description").value("Description1"));
    }

    @Test
    void addLike_shouldAddLike() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUser(null, "test@test.ru", "Test", "Name",
                                LocalDate.of(2001, 9, 11))))
                .andExpect(status().isOk());

        mvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createFilm(null, "Name", "Description",
                                LocalDate.of(2001, 9, 11), 100)))
                .andExpect(status().isOk());

        mvc.perform(put("/films/1/like/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes").isNotEmpty());
    }

    @Test
    void removeLike_shouldRemoveLike() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUser(null, "test@test.ru", "Test", "Name",
                                LocalDate.of(2001, 9, 11))))
                .andExpect(status().isOk());

        mvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createFilm(null, "Name", "Description",
                                LocalDate.of(2001, 9, 11), 100)))
                .andExpect(status().isOk());

        mvc.perform(put("/films/1/like/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes").isNotEmpty());

        mvc.perform(delete("/films/1/like/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes").isEmpty());
    }

    @Test
    void getPopularFilms_shouldGetPopularFilms() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUser(null, "test@test.ru", "Test", "Name",
                                LocalDate.of(2001, 9, 11))))
                .andExpect(status().isOk());

        mvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createFilm(null, "Name", "Description",
                                LocalDate.of(2001, 9, 11), 100)))
                .andExpect(status().isOk());

        mvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createFilm(null, "Name", "Description",
                                LocalDate.of(2001, 9, 11), 100)))
                .andExpect(status().isOk());

        mvc.perform(put("/films/1/like/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes").isNotEmpty());

        mvc.perform(put("/films/1/like/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes").isNotEmpty());

        mvc.perform(get("/films/popular")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }
}
