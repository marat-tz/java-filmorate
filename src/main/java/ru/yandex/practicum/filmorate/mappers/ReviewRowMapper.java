package ru.yandex.practicum.filmorate.mappers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Component
public class ReviewRowMapper implements RowMapper<Review> {
    @Override
    public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
        log.info("Начало метода ReviewRowMapper mapRow");

        Long reviewId = rs.getLong("id");
        log.info("Получено значение reviewId={}", reviewId);

        String content = rs.getString("content");
        log.info("Получено значение content={}", content);

        Boolean isPositive = rs.getBoolean("is_positive");
        log.info("Получено значение isPositive={}", isPositive);

        Long userId = rs.getLong("user_id");
        log.info("Получено значение userId={}", userId);

        Long filmId = rs.getLong("film_id");
        log.info("Получено значение filmId={}", filmId);

        Integer useful = rs.getInt("lik") - rs.getInt("dis");
        log.info("Получено значение useful={}", useful);

        return Review.builder()
                .reviewId(reviewId)
                .content(content)
                .isPositive(isPositive)
                .userId(userId)
                .filmId(filmId)
                .useful(useful)
                .build();
    }
}