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
import ru.yandex.practicum.filmorate.model.User;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerSpringTests {

    @Autowired
    MockMvc mvc;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

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
    void findAll_shouldFindAllUsers() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUser(null, "email@test.ru", "Login", "Name",
                                LocalDate.of(2001, 9, 11))))
                .andExpect(status().isOk());

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void create_shouldNotCreateEmptyEmail() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUser(null, "", "Login", "Name",
                                LocalDate.of(2001, 9, 11))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_shouldNotCreateWrongEmail() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUser(null, "ololo", "Login", "Name",
                                LocalDate.of(2001, 9, 11))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_shouldNotCreateEmptyLogin() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUser(null, "ololo", "", "Name",
                                LocalDate.of(2001, 9, 11))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_shouldCreateEmptyName() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUser(null, "test@test.ru", "Test", "",
                                LocalDate.of(2001, 9, 11))))
                .andExpect(status().isOk());
    }

    @Test
    void create_shouldNotCreateBirthAfterNow() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUser(null, "test@test.ru", "Test", "Name",
                                LocalDate.now().plusDays(1))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_shouldReturnValidUser() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUser(null, "test@test.ru", "Test", "Name",
                                LocalDate.of(2001, 9, 11))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.email").value("test@test.ru"))
                .andExpect(jsonPath("$.login").value("Test"))
                .andExpect(jsonPath("$.name").value("Name"))
                .andExpect(jsonPath("$.birthday").value("2001-09-11"));
    }

    @Test
    void update_shouldUpdateUser() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUser(null, "test@test.ru", "Test", "Name",
                                LocalDate.of(2001, 9, 11))))
                .andExpect(status().isOk());

        mvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUser(1L,"test@test1.ru", "Test1", "Name1",
                                LocalDate.of(2002, 9, 11))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@test1.ru"))
                .andExpect(jsonPath("$.login").value("Test1"))
                .andExpect(jsonPath("$.name").value("Name1"))
                .andExpect(jsonPath("$.birthday").value("2002-09-11"));
    }

    @Test
    void addFriend_shouldAddFriend() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUser(null, "test@test.ru", "Test", "Name",
                                LocalDate.of(2001, 9, 11))))
                .andExpect(status().isOk());

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUser(null, "test@test.ru", "Test", "Name",
                                LocalDate.of(2001, 9, 11))))
                .andExpect(status().isOk());

        mvc.perform(put("/users/1/friends/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.friendsId").isNotEmpty());
    }

    @Test
    void addFriend_shouldNotAddMyself() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUser(null, "test@test.ru", "Test", "Name",
                                LocalDate.of(2001, 9, 11))))
                .andExpect(status().isOk());

        mvc.perform(put("/users/1/friends/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void removeFriend_shouldRemoveFriend() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUser(null, "test@test.ru", "Test", "Name",
                                LocalDate.of(2001, 9, 11))))
                .andExpect(status().isOk());

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUser(null, "test@test.ru", "Test", "Name",
                                LocalDate.of(2001, 9, 11))))
                .andExpect(status().isOk());

        mvc.perform(put("/users/1/friends/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.friendsId").isNotEmpty());

        mvc.perform(delete("/users/1/friends/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.friendsId").isEmpty());
    }

    @Test
    void getFriends_shouldGetFriends() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUser(null, "test@test.ru", "Test", "Name",
                                LocalDate.of(2001, 9, 11))))
                .andExpect(status().isOk());

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUser(null, "test@test.ru", "Test", "Name",
                                LocalDate.of(2001, 9, 11))))
                .andExpect(status().isOk());

        mvc.perform(put("/users/1/friends/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.friendsId").isNotEmpty());

        mvc.perform(get("/users/1/friends")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void getCommonFriends_shouldGetCommonFriends() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUser(null, "test@test.ru", "Test", "Name",
                                LocalDate.of(2001, 9, 11))))
                .andExpect(status().isOk());

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUser(null, "test@test.ru", "Test", "Name",
                                LocalDate.of(2001, 9, 11))))
                .andExpect(status().isOk());

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUser(null, "test@test.ru", "Test", "Name",
                                LocalDate.of(2001, 9, 11))))
                .andExpect(status().isOk());

        mvc.perform(put("/users/1/friends/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.friendsId").isNotEmpty());

        mvc.perform(put("/users/1/friends/3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.friendsId").isNotEmpty());

        mvc.perform(get("/users/2/friends/common/3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }


}
