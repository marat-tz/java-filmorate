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
        Long reviewId = rs.getLong("id");
        String content = rs.getString("content");
        Boolean isPositive = rs.getBoolean("is_positive");
        Long userId = rs.getLong("user_id");
        Long filmId = rs.getLong("film_id");
        Integer useful = rs.getInt("lik") - rs.getInt("dis");

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