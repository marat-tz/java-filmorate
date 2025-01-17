package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;

@Service
@Slf4j
public class DbReviewServiceImpl implements ReviewService {
    private final ReviewStorage reviewStorage;

    public DbReviewServiceImpl(ReviewStorage reviewStorage) {
        this.reviewStorage = reviewStorage;
    }

    public Review addReview(Review reviews) {
        if (reviews.getUserId() == null || reviews.getUserId() <= 0) {
            throw new NotFoundException("Пользователь не найден.");
        }
        if (reviews.getFilmId() == null || reviews.getFilmId() <= 0) {
            throw new NotFoundException("Фильм не найден.");
        }
        try {
            return reviewStorage.addReview(reviews);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при добавлении.", e);
        }
    }

    public Review updateReview(Review reviews) {
        try {
            return reviewStorage.updateReview(reviews);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при обновлении.");
        }
    }

    public void deleteReview(Long id) {
        try {
            reviewStorage.deleteReview(id);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении.");
        }
    }

    public Review getReviewById(Long id) {
        try {
            return reviewStorage.getReviewById(id);
        } catch (Exception e) {
            throw new NotFoundException("Ошибка при получении.");
        }
    }

    public List<Review> getReviewByFilm(Long id, int count) {
        try {
            return reviewStorage.getReviewByFilm(id, count);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении отзывов о фильме.");
        }
    }

    public List<Review> getAllReviews(int count) {
        try {
            return reviewStorage.getAllReviews(count);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении всех отзывов.");
        }
    }

    public void likeToReview(Long reviewId, Long userId) {
        try {
            reviewStorage.likeOrDislikeToReview(reviewId, userId, true);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при добавление лайка у отзыва.");
        }
    }

    public void dislikeToReview(Long reviewId, Long userId) {
        try {
            reviewStorage.likeOrDislikeToReview(reviewId, userId, false);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при добавлении дизлайка к отзыву.", e);
        }
    }

    public void deleteLike(Long reviewId, Long userId) {
        reviewStorage.deleteLikeOrDislike(reviewId, userId, true);
    }

    public void deleteDislike(Long reviewId, Long userId) {
        reviewStorage.deleteLikeOrDislike(reviewId, userId, false);
    }

}

