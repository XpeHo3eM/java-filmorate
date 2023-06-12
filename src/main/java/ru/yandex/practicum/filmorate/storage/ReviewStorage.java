package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {
    Optional<Review> addReview(Review review);

    Optional<Review> updateReview(Review review);

    Optional<Review> findReviewById(long reviewId);

    boolean removeReview(long reviewId);

    boolean addLikeReview(long reviewId, long userId);

    boolean removeLikeReview(long reviewId, long userId);

    boolean addDislikeReview(long reviewId, long userId);

    boolean removeDislikeReview(long reviewId, long userId);

    List<Review> getReviews(int count);

    List<Review> getReviewsByFilm(long filmId, int count);
}
