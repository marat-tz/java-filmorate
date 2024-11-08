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
import ru.yandex.practicum.filmorate.model.Film;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class FilmControllerTests {

    ConfigurableApplicationContext ctx;
    private HttpClient client;
    private Film film = Film.builder()
            .name("Name")
            .description("Description")
            .releaseDate(LocalDate.of(2001, 9, 11))
            .duration(90)
            .build();

    private Gson gson = new GsonBuilder()
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
    void create_shouldNotCreateEmptyName() throws IOException, InterruptedException {
        Film newFilm = film.toBuilder().name("").build();
        String filmJson = gson.toJson(newFilm);

        URI url = URI.create("http://localhost:8080/films");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(filmJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void create_shouldCreateFilm() throws IOException, InterruptedException {
        Film actualFilm = film.toBuilder().id(1L).build();
        String filmJson = gson.toJson(film);

        URI url = URI.create("http://localhost:8080/films");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(filmJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(actualFilm, gson.fromJson(response.body(), Film.class));
    }

    @Test
    void create_shouldNotCreateDescriptionLonger200() throws IOException, InterruptedException {
        Film newFilm = film.toBuilder().description(".".repeat(201)).build();
        String filmJson = gson.toJson(newFilm);

        URI url = URI.create("http://localhost:8080/films");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(filmJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void create_shouldCreateDescriptionEquals200() throws IOException, InterruptedException {
        Film newFilm = film.toBuilder().description(".".repeat(200)).build();
        String filmJson = gson.toJson(newFilm);

        URI url = URI.create("http://localhost:8080/films");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(filmJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void create_shouldNotCreateNullDate() throws IOException, InterruptedException {
        Film newFilm = film.toBuilder().releaseDate(null).build();
        String filmJson = gson.toJson(newFilm);

        URI url = URI.create("http://localhost:8080/films");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(filmJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void create_shouldNotCreateDateBefore1895() throws IOException, InterruptedException {
        Film newFilm = film.toBuilder().releaseDate(LocalDate.of(1895, 12, 27)).build();
        String filmJson = gson.toJson(newFilm);

        URI url = URI.create("http://localhost:8080/films");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(filmJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void create_shouldNotCreateNegativeDuration() throws IOException, InterruptedException {
        Film newFilm = film.toBuilder().duration(-1).build();
        String filmJson = gson.toJson(newFilm);

        URI url = URI.create("http://localhost:8080/films");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(filmJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void create_shouldNotCreateNullDuration() throws IOException, InterruptedException {
        Film newFilm = film.toBuilder().duration(null).build();
        String filmJson = gson.toJson(newFilm);

        URI url = URI.create("http://localhost:8080/films");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(filmJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

}