package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mappers.ReviewRowMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
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

    private final FeedStorage feedStorage;

    @Override
    public Review addReview(Review reviews) {
        Long reviewId;
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

        if (Objects.nonNull(key.getKey())) {
            reviewId = key.getKey().longValue();
        } else {
            throw new ValidationException("Ошибка присвоения id отзыву");
        }

        feedStorage.create(reviews.getUserId(), EventType.REVIEW, Operation.ADD, reviewId);
        return getReviewById(Objects.requireNonNull(reviewId));
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
        feedStorage.create(reviews.getUserId(), EventType.REVIEW, Operation.UPDATE, reviews.getReviewId());
        return getReviewById(reviews.getReviewId());
    }

    @Override
    public void deleteReview(Long id) {
        Review review = getReviewById(id);

        final String DELETE_USEFUL_QUERY = "DELETE FROM useful WHERE review_id = ?";
        jdbc.update(DELETE_USEFUL_QUERY, id);

        final String DELETE_REVIEW_QUERY = "DELETE FROM reviews WHERE id = ?";
        jdbc.update(DELETE_REVIEW_QUERY, id);

        feedStorage.create(review.getUserId(), EventType.REVIEW, Operation.REMOVE, id);
    }

    @Override
    public Review getReviewById(Long id) {
        final String QUERY = "SELECT r.id, r.content, r.is_positive, u.name AS user_name, f.name AS film_name, " +
                "r.user_id, r.film_id, " +
                "COALESCE(SUM(CASE WHEN uf.is_like IS TRUE THEN 1 ELSE 0 END), 0) AS likes, " +
                "COALESCE(SUM(CASE WHEN uf.is_like IS FALSE THEN 1 ELSE 0 END), 0) AS dislikes " +
                "FROM reviews r " +
                "JOIN users u ON r.user_id = u.id " +
                "JOIN films f ON r.film_id = f.id " +
                "LEFT JOIN useful uf ON r.id = uf.review_id " +
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
                "COALESCE(SUM(CASE WHEN uf.is_like IS TRUE THEN 1 ELSE 0 END), 0) AS likes, " +
                "COALESCE(SUM(CASE WHEN uf.is_like IS FALSE THEN 1 ELSE 0 END), 0) AS dislikes " +
                "FROM reviews r " +
                "JOIN users u ON r.user_id = u.id " +
                "JOIN films f ON r.film_id = f.id " +
                "LEFT JOIN useful uf ON r.id = uf.review_id " +
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
    public void likeOrDislikeToReview(Long reviewId, Long userId, boolean isLike) {
        log.info("Начало метода likeOrDislikeToReview");

        String checkSql = "SELECT * FROM useful WHERE review_id = ? AND user_id = ?";
        List<Map<String, Object>> likeDislike = jdbc.queryForList(checkSql, reviewId, userId);

        if (!likeDislike.isEmpty()) {
            String updateSql = "UPDATE useful SET is_like = ? WHERE review_id = ? AND user_id = ?";
            jdbc.update(updateSql, isLike, reviewId, userId);

        } else {
            String insertSql = "INSERT INTO useful (review_id, is_like, user_id) VALUES (?, ?, ?)";
            jdbc.update(insertSql, reviewId, isLike, userId);
        }
        log.info("Добавлен {} у отзыва {} или обновлен для пользователя {}.", isLike ? "лайк" : "дизлайк", reviewId, userId);
    }

    @Override
    public void deleteLikeOrDislike(Long reviewId, Long userId, boolean isLike) {
        String action = isLike ? "Лайк" : "Дислайк";
        final String sql = "DELETE FROM useful WHERE review_id = ? AND user_id = ?";
        jdbc.update(sql, reviewId, userId);
        log.info(action + " удален.");
    }

}