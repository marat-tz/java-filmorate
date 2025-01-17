package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewService {
    Review addReview(Review reviews);

    Review updateReview(Review review);

    void deleteReview(Long id);

    Review getReviewById(Long id);

    List<Review> getReviewByFilm(Long filmId, int count);

    List<Review> getAllReviews(int count);

    void likeToReview(Long id, Long userId);

    void dislikeToReview(Long id, Long userId);

    void deleteLike(Long id, Long userId);

    void deleteDislike(Long id, Long userId);
}
