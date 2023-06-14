package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.entity.EntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.model.ReviewStatusLike;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.util.Mapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewDao implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public Optional<Review> addReview(Review review) {
        long reviewId = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film_reviews")
                .usingGeneratedKeyColumns("review_id")
                .executeAndReturnKey(Mapper.reviewToMap(review)).longValue();

        return findReviewById(reviewId);
    }

    @Override
    @Transactional
    public Optional<Review> updateReview(Review review) {
        String sqlQuery = "UPDATE film_reviews\n" +
                "SET content = ?,\n" +
                "\tis_positive = ?;";

        try {
            jdbcTemplate.update(sqlQuery,
                    review.getContent(),
                    review.getIsPositive());
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }

        return findReviewById(review.getReviewId());
    }

    @Override
    @Transactional
    public Optional<Review> findReviewById(long reviewId) {
        String sqlQuery = "SELECT \n" +
                "\tf.review_id,\n" +
                "\tf.content,\n" +
                "\tf.is_positive,\n" +
                "\tf.user_id,\n" +
                "\tf.film_id,\n" +
                "\tCOALESCE(SUM(l.is_positive),0) -\n" +
                "\tCOALESCE((COUNT(l.review_id) - SUM(l.is_positive)),0) AS useful\n" +
                "FROM film_reviews AS f\n" +
                "LEFT JOIN film_reviews_likes AS l ON f.review_id = l.review_id\n" +
                "WHERE f.review_id = ?\n" +
                "GROUP BY f.review_id, f.content, f.is_positive, f.user_id, f.film_id;";

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, Mapper::mapRowToReview, reviewId));
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public boolean removeReview(long reviewId) {
        String sqlQuery = "DELETE FROM film_reviews\n" +
                "WHERE review_id = ?;";

        try {
            jdbcTemplate.update(sqlQuery, reviewId);
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            return false;
        }

        return findReviewById(reviewId).isEmpty();
    }

    @Override
    @Transactional
    public boolean addLikeReview(long reviewId, long userId) {
        try {
            ReviewStatusLike currentStatusLike = getLikeOrDislike(reviewId, userId);

            switch (currentStatusLike) {
                case LIKE:
                    throw new EntityAlreadyExistsException(String.format("Лайк отзыву " +
                            "с ID = %d от пользователя ID = %d уже добавлен", reviewId, userId));

                case DISLIKE:
                    updateLikeOrDislike(reviewId, userId, true);
                    return true;

                case EMPTY:
                    addLikeOrDislike(reviewId, userId, true);
                    return true;

                default:
                    log.error("Непредвиденная ошибка при добавлении лайка");
                    return false;
            }
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public boolean removeLikeReview(long reviewId, long userId) {
        try {
            removeLikeOrDislike(reviewId, userId);

            ReviewStatusLike currentStatusLike = getLikeOrDislike(reviewId, userId);

            if (currentStatusLike.equals(ReviewStatusLike.LIKE)) {
                return true;
            }

        } catch (DataAccessException e) {
            log.error(e.getMessage());
        }

        return false;
    }

    @Override
    @Transactional
    public boolean addDislikeReview(long reviewId, long userId) {
        try {
            ReviewStatusLike currentStatusLike = getLikeOrDislike(reviewId, userId);

            switch (currentStatusLike) {
                case LIKE:
                    updateLikeOrDislike(reviewId, userId, false);
                    return true;

                case DISLIKE:
                    throw new EntityAlreadyExistsException(String.format("Дизлайк отзыву " +
                            "с ID = %d от пользователя ID = %d уже добавлен", reviewId, userId));

                case EMPTY:
                    addLikeOrDislike(reviewId, userId, false);
                    return true;

                default:
                    log.error("Непредвиденная ошибка при добавлении дизлайка");
                    return false;
            }
        } catch (DataAccessException e) {
            log.error(e.getMessage());

            return false;
        }
    }

    @Override
    @Transactional
    public boolean removeDislikeReview(long reviewId, long userId) {
        try {
            removeLikeOrDislike(reviewId, userId);

            ReviewStatusLike currentStatusLike = getLikeOrDislike(reviewId, userId);

            if (currentStatusLike.equals(ReviewStatusLike.DISLIKE)) {
                return true;
            }

        } catch (DataAccessException e) {
            log.error(e.getMessage());
            return false;
        }

        return false;
    }


    @Override
    @Transactional
    public List<Review> getReviews(int count) {
        String sqlQuery = "SELECT \n" +
                "\tf.review_id,\n" +
                "\tf.content,\n" +
                "\tf.is_positive,\n" +
                "\tf.user_id,\n" +
                "\tf.film_id,\n" +
                "\tCOALESCE(SUM(l.is_positive),0) -\n" +
                "\tCOALESCE((COUNT(l.review_id) - SUM(l.is_positive)),0) AS useful\n" +
                "FROM film_reviews AS f\n" +
                "LEFT JOIN film_reviews_likes AS l ON f.review_id = l.review_id\n" +
                "GROUP BY f.review_id, f.content, f.is_positive, f.user_id, f.film_id\n" +
                "ORDER BY useful DESC\n" +
                "LIMIT ?;";

        try {
            return jdbcTemplate.query(sqlQuery, Mapper::mapRowToReview, count);

        } catch (DataAccessException e) {
            log.error(e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional
    public List<Review> getReviewsByFilm(long filmId, int count) {
        String sqlQuery = "SELECT \n" +
                "\tf.review_id,\n" +
                "\tf.content,\n" +
                "\tf.is_positive,\n" +
                "\tf.user_id,\n" +
                "\tf.film_id,\n" +
                "\tCOALESCE(SUM(l.is_positive),0) -\n" +
                "\tCOALESCE((COUNT(l.review_id) - SUM(l.is_positive)),0) AS useful\n" +
                "FROM film_reviews AS f\n" +
                "LEFT JOIN film_reviews_likes AS l ON f.review_id = l.review_id\n" +
                "WHERE film_id = ?\n" +
                "GROUP BY f.review_id, f.content, f.is_positive, f.user_id, f.film_id\n" +
                "ORDER BY useful DESC\n" +
                "LIMIT ?;";

        try {
            return jdbcTemplate.query(sqlQuery, Mapper::mapRowToReview, filmId, count);

        } catch (DataAccessException e) {
            log.error(e.getMessage());
            return Collections.emptyList();
        }
    }

    private void addLikeOrDislike(long reviewId, long userId, boolean status) {
        String sqlQuery = "INSERT INTO film_reviews_likes (review_id, user_id, is_positive)\n" +
                "VALUES (?, ?, ?);";

        jdbcTemplate.update(sqlQuery, reviewId, userId, status);
    }

    private void updateLikeOrDislike(long reviewId, long userId, boolean status) {
        String sqlQuery = "UPDATE film_reviews_likes\n" +
                "SET is_positive = ?\n" +
                "WHERE review_id = ?\n" +
                "AND user_id = ?;";

        jdbcTemplate.update(sqlQuery, reviewId, userId, status);
    }

    private void removeLikeOrDislike(long reviewId, long userId) {
        String sqlQuery = "DELETE FROM film_reviews_likes\n" +
                "WHERE review_id = ?\n" +
                "AND user_id = ?;";

        jdbcTemplate.update(sqlQuery, reviewId, userId);
    }

    private ReviewStatusLike getLikeOrDislike(long reviewId, long userId) {
        String sqlQuery = "SELECT is_positive\n" +
                "FROM film_reviews_likes\n" +
                "WHERE review_id = ?\n" +
                "AND user_id = ?;";

        List<Boolean> getQueryString = jdbcTemplate.queryForList(sqlQuery, Boolean.class, reviewId, userId);

        if (getQueryString.isEmpty()) {
            return ReviewStatusLike.EMPTY;
        }

        if (getQueryString.get(0)) {
            return ReviewStatusLike.LIKE;
        } else {
            return ReviewStatusLike.DISLIKE;
        }
    }
}
