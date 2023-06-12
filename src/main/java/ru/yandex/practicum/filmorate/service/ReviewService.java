package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewService {
    Review addReview(Review review);

    Review updateReview(Review review);

    Review findReviewById(long reviewId);

    void removeReview(long reviewId);

    void addLikeReview(long reviewId, long userId);

    void removeLikeReview(long reviewId, long userId);

    void addDislikeReview(long reviewId, long userId);

    void removeDislikeReview(long reviewId, long userId);

    List<Review> getReviews(int count);

    List<Review> getReviewsByFilm(long filmId, int count);
}
