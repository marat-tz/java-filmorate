package ru.yandex.practicum.filmorate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.filmorate.adapters.LocalDateAdapter;
import ru.yandex.practicum.filmorate.model.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserControllerTests {

    ConfigurableApplicationContext ctx;
    private HttpClient client;
    private final URI url = URI.create("http://localhost:8080/users");
    private final User user = User.builder()
            .email("email@test.ru")
            .login("Login")
            .name("Name")
            .birthday(LocalDate.of(2001, 9, 11))
            .build();

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    @BeforeEach
    public void start() {
        client = HttpClient.newHttpClient();
        ctx = SpringApplication.run(FilmorateApplication.class);
    }

    @AfterEach
    public void stop() {
        ctx.close();
    }

    @Test
    void create_shouldNotCreateEmptyEmail() throws IOException, InterruptedException {
        User newUser = user.toBuilder().email("").build();
        String userJson = gson.toJson(newUser);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(userJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void create_shouldCreateUser() throws IOException, InterruptedException {
        User actualUser = user.toBuilder().id(1L).build();
        String userJson = gson.toJson(user);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(userJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(actualUser, gson.fromJson(response.body(), User.class));
    }

    @Test
    void create_shouldNotCreateEmptyLogin() throws IOException, InterruptedException {
        User actualUser = user.toBuilder().login("").build();
        String userJson = gson.toJson(actualUser);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(userJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void create_shouldCreateEmptyName() throws IOException, InterruptedException {
        User actualUser = user.toBuilder().name("").build();
        String userJson = gson.toJson(actualUser);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(userJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void create_shouldNotCreateBirthAfterNow() throws IOException, InterruptedException {
        User actualUser = user.toBuilder().birthday(LocalDate.now().plusDays(1)).build();
        String userJson = gson.toJson(actualUser);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(userJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }
}
