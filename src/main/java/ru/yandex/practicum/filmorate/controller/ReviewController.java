package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService service;
    private final FeedService feedService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Review addReview(@Valid @RequestBody Review review,
                            HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());
        Review reviewAdd = service.addReview(review);
        feedService.createFeed(reviewAdd.getUserId(), reviewAdd.getReviewId(), "REVIEW", "ADD");

        return reviewAdd;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Review updateReview(@Valid @RequestBody Review review,
                               HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());
        Review reviewUpdate = service.updateReview(review);
        feedService.createFeed(reviewUpdate.getUserId(), reviewUpdate.getReviewId(), "REVIEW", "UPDATE");

        return reviewUpdate;
    }

    @GetMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.OK)
    public Review getReview(@PathVariable("reviewId") long reviewId,
                            HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());

        return service.findReviewById(reviewId);
    }

    @DeleteMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeReview(@PathVariable("reviewId") long reviewId,
                             HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());
        Review review = service.findReviewById(reviewId);

        service.removeReview(reviewId);

        feedService.createFeed(review.getUserId(), review.getReviewId(), "REVIEW", "REMOVE");
    }

    @PutMapping("/{reviewId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addLikeReview(@PathVariable("reviewId") long reviewId,
                              @PathVariable("userId") long userId,
                              HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());

        service.addLikeReview(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeLikeReview(@PathVariable("reviewId") long reviewId,
                                 @PathVariable("userId") long userId,
                                 HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());

        service.removeLikeReview(reviewId, userId);
    }

    @PutMapping("/{reviewId}/dislike/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addDislikeReview(@PathVariable("reviewId") long reviewId,
                                 @PathVariable("userId") long userId,
                                 HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());

        service.addDislikeReview(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/dislike/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeDislikeReview(@PathVariable("reviewId") long reviewId,
                                    @PathVariable("userId") long userId,
                                    HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());

        service.removeDislikeReview(reviewId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Review> getReviews(@RequestParam(defaultValue = "0", required = false) long filmId,
                                   @RequestParam(defaultValue = "10", required = false) int count,
                                   HttpServletRequest request) {
        log.debug("On URL [{}] used method [{}]", request.getRequestURL(), request.getMethod());
        if (filmId > 0) {
            return service.getReviewsByFilm(filmId, count);
        } else {
            return service.getReviews(count);
        }
    }
}
