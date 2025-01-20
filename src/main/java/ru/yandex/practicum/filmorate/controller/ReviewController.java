package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@Slf4j
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public Review addReview(@Valid @RequestBody Review review) {
        log.info("Пришел POST запрос с телом {}", review);
        Review addReview = reviewService.addReview(review);
        log.info("Отправлен POST ответ с телом {}", addReview);
        return addReview;
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        log.info("Пришел PUT запрос с телом {}", review);
        Review updateReview = reviewService.updateReview(review);
        log.info("Отправлен PUT ответ с телом {}", updateReview);
        return updateReview;
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        log.info("Пришел DELETE запрос с телом {}", id);
        reviewService.deleteReview(id);
    }


    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable Long id) {
        log.info("Пришел GET запрос с телом {}", id);
        Review review = reviewService.getReviewById(id);
        log.info("Отправлен GET ответ с телом {}", review);
        return review;
    }

    @GetMapping
    public List<Review> getReviewByFilm(@RequestParam(required = false) Long filmId,
                                        @RequestParam(defaultValue = "10") int count) {
        log.info("Пришел GET запрос с телом {} {}", filmId, count);
        if (filmId != null) {
            List<Review> review = reviewService.getReviewByFilm(filmId, count);
            log.info("Отправлен GET ответ с телом {}", review);
            return review;
        } else {
            List<Review> reviews = reviewService.getAllReviews(count);
            log.info("Отправлен GET ответ с телом {}", reviews);
            return reviews;
        }
    }

    @PutMapping("/{id}/like/{userId}")
    public void likeToReview(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Пришел PUT запрос с телом {}, {}", id, userId);
        reviewService.likeToReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void dislikeToReview(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Пришел PUT запрос с телом {}, {}", id, userId);
        reviewService.dislikeToReview(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Пришел DELETE запрос с телом {}, {}", id, userId);
        reviewService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Пришел DELETE запрос с телом {}, {}", id, userId);
        reviewService.deleteDislike(id, userId);
    }
}