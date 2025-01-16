package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review addReview(Review reviews);

    Review updateReview(Review reviews);

    void deleteReview(Long id);

    Review getReviewById(Long id);

    List<Review> getReviewByFilm(Long id, int count);

    List<Review> getAllReviews(int count);

    void likeToReview(Long reviewId, Long userId);

    void dislikeToReview(Long reviewId, Long userId);

    void deleteLike(Long reviewId, Long userId);

    void deleteDislike(Long reviewId, Long userId);
}