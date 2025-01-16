package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.ReviewRowMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ReviewDbStorageImpl implements ReviewStorage {
    private final JdbcTemplate jdbc;
    private final ReviewRowMapper reviewMapper;

    @Override
    public Review addReview(Review reviews) {
        final String ADD_REVIEW_QUERY = "INSERT INTO reviews (content, is_positive, user_id, film_id) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder key = new GeneratedKeyHolder();
        jdbc.update(m -> {
            PreparedStatement ps = m.prepareStatement(ADD_REVIEW_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, reviews.getContent());
            ps.setBoolean(2, reviews.getIsPositive());
            ps.setLong(3, reviews.getUserId());
            ps.setLong(4, reviews.getFilmId());
            return ps;
        }, key);
        return getReviewById(Objects.requireNonNull(key.getKey()).longValue());
    }

    @Override
    public Review updateReview(Review reviews) {
        final String UPDATE_REVIEW_QUERY = "UPDATE reviews SET content = ?, is_positive = ?, film_id = ? " +
                "WHERE id = ?";
        int update = jdbc.update(UPDATE_REVIEW_QUERY,
                reviews.getContent(),
                reviews.getIsPositive(),
                reviews.getFilmId(),
                reviews.getReviewId()
        );
        if (update == 0) {
            log.info("Не удалось обновить отзыв с id {}.", reviews.getReviewId());
            throw new NotFoundException("Отзыв с таким id не найден.");
        }
        return getReviewById(reviews.getReviewId());
    }

    @Override
    public void deleteReview(Long id) {
        final String DELETE_USEFUL_QUERY = "DELETE FROM useful WHERE useful_id = ?";
        jdbc.update(DELETE_USEFUL_QUERY, id);

        final String DELETE_REVIEW_QUERY = "DELETE FROM reviews WHERE id = ?";
        jdbc.update(DELETE_REVIEW_QUERY, id);
    }

    @Override
    public Review getReviewById(Long id) {
        final String QUERY = "SELECT r.id, r.content, r.is_positive, u.name AS user_name, f.name AS film_name, " +
                "r.user_id, r.film_id, " +
                "COALESCE(SUM(CASE WHEN uf.like_id IS NOT NULL THEN 1 ELSE 0 END), 0) AS likes, " +
                "COALESCE(SUM(CASE WHEN uf.dislike_id IS NOT NULL THEN 1 ELSE 0 END), 0) AS dislikes " +
                "FROM reviews r " +
                "JOIN users u ON r.user_id = u.id " +
                "JOIN films f ON r.film_id = f.id " +
                "LEFT JOIN useful uf ON r.id = uf.useful_id " +
                "WHERE r.id = ? " +
                "GROUP BY r.id, r.content, r.is_positive, u.name, f.name, r.user_id, r.film_id";

        Review review = jdbc.queryForObject(QUERY, reviewMapper, id);
        log.info("Получен отзыв {}", review);
        return review;
    }

    @Override
    public List<Review> getReviewByFilm(Long id, int count) {
        final String reviewByFilm = "SELECT r.id, r.content, r.is_positive, u.name AS user_name, f.name AS film_name, " +
                "r.user_id, r.film_id, " +
                "COALESCE(SUM(CASE WHEN uf.like_id IS NOT NULL THEN 1 ELSE 0 END), 0) AS likes, " +
                "COALESCE(SUM(CASE WHEN uf.dislike_id IS NOT NULL THEN 1 ELSE 0 END), 0) AS dislikes " +
                "FROM reviews r " +
                "JOIN users u ON r.user_id = u.id " +
                "JOIN films f ON r.film_id = f.id " +
                "LEFT JOIN useful uf ON r.id = uf.useful_id " +
                "WHERE r.film_id = ? " +
                "GROUP BY r.id, r.content, r.is_positive, u.name, f.name, r.user_id, r.film_id " +
                "LIMIT ?";
        List<Review> review = jdbc.query(reviewByFilm, reviewMapper, id, count);
        log.info("Получены отзывы о фильме {}.", review);
        return review;
    }

    @Override
    public List<Review> getAllReviews(int count) {
        final String review = "SELECT * FROM reviews LIMIT ?";
        List<Review> reviews = jdbc.query(review, reviewMapper, count);
        log.info("Получены отзывы о фильме {}.", reviews);
        return reviews;
    }

    @Override
    public void likeToReview(Long reviewId, Long userId) {
        String checkSql = "SELECT * FROM useful WHERE useful_id = ? AND like_id = ? OR useful_id = ? AND dislike_id = ?";
        List<Map<String, Object>> likeDislike = jdbc.queryForList(checkSql, reviewId, userId, reviewId, userId);
        if (!likeDislike.isEmpty()) {
            String updateSql;
            if (likeDislike.get(0).get("like_id") != null) {
                updateSql = "UPDATE useful SET like_id = NULL, dislike_id = ? WHERE useful_id = ? AND like_id = ?";
                jdbc.update(updateSql, userId, reviewId, userId);
            } else {
                updateSql = "UPDATE useful SET dislike_id = NULL, like_id = ? WHERE useful_id = ? AND dislike_id = ?";
                jdbc.update(updateSql, userId, reviewId, userId);
            }
        } else {
            String insertSql = "INSERT INTO useful (useful_id, like_id, dislike_id) VALUES (?, ?, NULL)";
            jdbc.update(insertSql, reviewId, userId);
        }
        log.info("Добавлен лайк у отзыва {} или обновлен для пользователя {}.", reviewId, userId);
    }

    @Override
    public void dislikeToReview(Long reviewId, Long userId) {
        String checkSql = "SELECT * FROM useful WHERE useful_id = ? AND like_id = ? OR useful_id = ? AND dislike_id = ?";
        List<Map<String, Object>> likeDislike = jdbc.queryForList(checkSql, reviewId, userId, reviewId, userId);
        if (!likeDislike.isEmpty()) {
            String updateSql;
            if (likeDislike.get(0).get("dislike_id") != null) {
                updateSql = "UPDATE useful SET dislike_id = NULL, like_id = ? WHERE useful_id = ? AND dislike_id = ?";
                jdbc.update(updateSql, userId, reviewId, userId);
            } else {
                updateSql = "UPDATE useful SET like_id = NULL, dislike_id = ? WHERE useful_id = ? AND like_id = ?";
                jdbc.update(updateSql, userId, reviewId, userId);
            }
        } else {
            String insertSql = "INSERT INTO useful (useful_id, like_id, dislike_id) VALUES (?, NULL, ?)";
            jdbc.update(insertSql, reviewId, userId);
        }
        log.info("Добавлен дизлайк у отзыва {} или обновлен для пользователя {}.", reviewId, userId);
    }

    @Override
    public void deleteLike(Long reviewId, Long userId) {
        final String sql = "DELETE FROM useful WHERE useful_id = ? AND like_id = ?";
        jdbc.update(sql, reviewId, userId);
        log.info("Лайк удален.");
    }

    @Override
    public void deleteDislike(Long reviewId, Long userId) {
        final String sql = "DELETE FROM useful WHERE useful_id = ? AND dislike_id = ?";
        jdbc.update(sql, reviewId, userId);
        log.info("Дислайк удален.");
    }
}