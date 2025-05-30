package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;

@Slf4j
@Service
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
        if (reviews.getIsPositive() == null) {
            throw new ValidationException("IsPositive is null");
        }
        try {
            return reviewStorage.addReview(reviews);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при добавлении. " + e.getMessage());
        }
    }

    public Review updateReview(Review reviews) {
        if (reviews.getUserId() == null || reviews.getUserId() <= 0) {
            throw new NotFoundException("Пользователь не найден.");
        }
        if (reviews.getFilmId() == null || reviews.getFilmId() <= 0) {
            throw new NotFoundException("Фильм не найден.");
        }
        if (reviews.getIsPositive() == null) {
            throw new ValidationException("IsPositive is null");
        }
        try {
            return reviewStorage.updateReview(reviews);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при обновлении. " + e.getMessage());
        }
    }

    public void deleteReview(Long id) {
        try {
            reviewStorage.deleteReview(id);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении. " + e.getMessage());
        }
    }

    public Review getReviewById(Long id) {
        try {
            return reviewStorage.getReviewById(id);
        } catch (Exception e) {
            throw new NotFoundException("Отзыв с id = " + id + " не найден " + e.getMessage());
        }
    }

    public List<Review> getReviewByFilm(Long id, int count) {
        try {
            return reviewStorage.getReviewByFilm(id, count);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении отзывов о фильме. " + e.getMessage());
        }
    }

    public List<Review> getAllReviews(int count) {
        try {
            return reviewStorage.getAllReviews(count);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении всех отзывов. " + e.getMessage());
        }
    }

    public void likeToReview(Long reviewId, Long userId) {
        try {
            reviewStorage.likeOrDislikeToReview(reviewId, userId, true);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при добавлении лайка у отзыва. " + e.getMessage());
        }
    }

    public void dislikeToReview(Long reviewId, Long userId) {
        try {
            reviewStorage.likeOrDislikeToReview(reviewId, userId, false);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при добавлении дизлайка к отзыву. " + e.getMessage());
        }
    }

    public void deleteLike(Long reviewId, Long userId) {
        reviewStorage.deleteLikeOrDislike(reviewId, userId, true);
    }

    public void deleteDislike(Long reviewId, Long userId) {
        reviewStorage.deleteLikeOrDislike(reviewId, userId, false);
    }

}

