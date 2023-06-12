package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.entity.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.RecommendationsService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService service;
    private final RecommendationsService recommendationsService;
    private final FeedService feedService;

    public UserController(UserService service, RecommendationsService recommendationsService, FeedService feedService) {
        this.service = service;
        this.recommendationsService = recommendationsService;
        this.feedService = feedService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return service.getAllUsers();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        return service.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return service.updateUser(user);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable long id) {
        return service.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void addToFriend(@PathVariable long id, @PathVariable long friendId) {
        service.addFriend(id, friendId);
        feedService.createFeed(id, friendId, "FRIEND", "ADD");
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFromFriends(@PathVariable long id, @PathVariable long friendId) {
        service.removeFriend(id, friendId);
        feedService.createFeed(id, friendId, "FRIEND", "REMOVE");
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable long id) {
        return service.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable long id, @PathVariable long otherId) {
        return service.getMutualFriends(id, otherId);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommended(@PathVariable long id) {
        User user = service.getUserById(id);

        if (user == null) {
            throw new EntityNotFoundException("Пользователь не найден.");
        }

        return recommendationsService.getRecommended(id);
    }

    @DeleteMapping("/{userId}")
    public void deleteFromFriends(@PathVariable long userId) {
        service.removeUserById(userId);
    }

    @GetMapping("/{id}/feed")
    public List<FeedEvent> getAllFeed(@PathVariable long id) {
        return feedService.getFeedByUserId(id);
    }
}
