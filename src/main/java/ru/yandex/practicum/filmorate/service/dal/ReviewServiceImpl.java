package ru.yandex.practicum.filmorate.service.dal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.entity.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.review.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final ReviewStorage reviewStorage;

    @Override
    public Review addReview(Review review) {
        checkFoundUserAndFilm(review.getUserId(), review.getFilmId());

        Optional<Review> newReview = reviewStorage.addReview(review);

        if (newReview.isPresent()) {
            log.debug("Создан новый отзыв: [{}]", newReview.get());
        } else {
            throw new ReviewNotCreatedException(String.format("Новый отзыв не добавлен: [%s]", review));
        }

        return newReview.get();
    }

    @Override
    public Review updateReview(Review review) {
        checkFoundUserAndFilm(review.getUserId(), review.getFilmId());

        Review checkFoundReview = findReviewById(review.getReviewId());

        if (review.equals(checkFoundReview)) {
            log.debug("Новых данных для обновления отзыва не найдено: \n" +
                    "Данные из контроллера: \n[Review={}], \nДанные из БД: \n[Review={}]", review, checkFoundReview);
            return review;
        }
        Optional<Review> updateReview = reviewStorage.updateReview(review);

        if (updateReview.isPresent()) {
            log.debug("Обновлен отзыв. Данные из контроллера: \n[Review={}], \nДанные после обновления: \n[Review={}]",
                    review, updateReview.get());
        } else {
            throw new ReviewNotUpdatedException(String.format("Отзыв не обновлен: [%s]", review));
        }

        return updateReview.get();
    }

    @Override
    public Review findReviewById(long reviewId) {
        Optional<Review> findReview = reviewStorage.findReviewById(reviewId);

        if (findReview.isPresent()) {
            log.debug("Найден Отзыв: [{}]", findReview.get());
        } else {
            throw new EntityNotFoundException(String.format("Отзыв с [id = %d] не найден", reviewId));
        }

        return findReview.get();
    }

    @Override
    public void removeReview(long reviewId) {
        Review checkFoundReview = findReviewById(reviewId);

        if (reviewStorage.removeReview(reviewId)) {
            log.debug("Удален отзыв: [{}]", checkFoundReview);
        } else {
            throw new ReviewNotRemovedException(String.format("Отзыв не удален: [%s]", checkFoundReview));
        }
    }

    @Override
    public void addLikeReview(long reviewId, long userId) {
        checkFoundUserAndReview(reviewId, userId);

        if (reviewStorage.addLikeReview(reviewId, userId)) {
            log.debug("Пользователь [userId = {}] поставил лайк отзыву [reviewId = {}]", userId, reviewId);
        } else {
            throw new ReviewNotLikedException(String.format("Пользователь [id = %d] не поставил лайк отзыву [id = %d]",
                    userId, reviewId));
        }
    }

    @Override
    public void removeLikeReview(long reviewId, long userId) {
        checkFoundUserAndReview(reviewId, userId);

        if (reviewStorage.removeLikeReview(reviewId, userId)) {
            log.debug("Пользователь [userId = {}] удалил лайк отзыву [reviewId = {}]", userId, reviewId);
        } else {
            throw new ReviewNotRemovedLikeException(String.format("Пользователь [id = %d] " +
                    "не удалил лайк отзыву [id = %d]", userId, reviewId));
        }
    }

    @Override
    public void addDislikeReview(long reviewId, long userId) {
        checkFoundUserAndReview(reviewId, userId);

        if (reviewStorage.addDislikeReview(reviewId, userId)) {
            log.debug("Пользователь [userId = {}] поставил дизлайк отзыву [reviewId = {}]", userId, reviewId);
        } else {
            throw new ReviewNotDislikedException(String.format("Пользователь [id = %d] " +
                    "не поставил дизлайк отзыву [id = %d]", userId, reviewId));
        }
    }

    @Override
    public void removeDislikeReview(long reviewId, long userId) {
        checkFoundUserAndReview(reviewId, userId);

        if (reviewStorage.removeDislikeReview(reviewId, userId)) {
            log.debug("Пользователь [userId = {}] удалил дизлайк отзыву [reviewId = {}]", userId, reviewId);
        } else {
            throw new ReviewNotRemovedDislikeException(String.format("Пользователь [id = %d] " +
                    "не удалил дизлайк отзыву [id = %d]", userId, reviewId));
        }
    }

    @Override
    public List<Review> getReviews(int count) {
        List<Review> listAllReviews = reviewStorage.getReviews(count);

        if (listAllReviews.isEmpty()) {
            log.debug("Вернулся пустой список отзывов, ограничение было [count={}]", count);
        } else {
            log.info("Вернулся список из [count={}] отзывов. Изначальный запрос был [count={}]",
                    listAllReviews.size(), count);
        }

        return getSortedListReviews(listAllReviews);
    }

    @Override
    public List<Review> getReviewsByFilm(long filmId, int count) {
        List<Review> listReviewsByFilm = reviewStorage.getReviewsByFilm(filmId, count);

        if (listReviewsByFilm.isEmpty()) {
            log.debug("Вернулся пустой список отзывов к фильму [filmId = {}], ограничение было [count={}]",
                    filmId, count);
        } else {
            log.info("Вернулся список из [count={}] отзывов к фильму [filmId = {}]. Изначальный запрос был [count={}]",
                    listReviewsByFilm.size(), filmId, count);
        }

        return getSortedListReviews(listReviewsByFilm);
    }

    private void checkFoundUser(long userId) {
        if (userStorage.getUserById(userId) == null) {
            throw new EntityNotFoundException(String.format("Пользователь с ID = %d не найден", userId));
        }
    }

    private void checkFoundFilm(long filmId) {
        if (filmStorage.getFilmById(filmId) == null) {
            throw new EntityNotFoundException(String.format("Фильм с ID = %d не найден", filmId));
        }
    }

    private void checkFoundUserAndFilm(long userId, long filmId) {
        checkFoundUser(userId);
        checkFoundFilm(filmId);
    }

    private void checkFoundUserAndReview(long userId, long reviewId) {
        checkFoundUser(userId);
        findReviewById(reviewId);
    }

    private List<Review> getSortedListReviews(List<Review> reviews) {
        return reviews.stream().sorted(Comparator.comparingLong(Review::getUseful).reversed())
                .collect(Collectors.toList());
    }
}
