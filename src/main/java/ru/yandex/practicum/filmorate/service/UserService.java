package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    public void addFriend(long user1Id, long user2Id);
    public void removeFriend(long user1Id, long user2Id);
    public Set<User> getMutualFriends(long user1Id, long user2Id);
    public User getUserById(long id);
    public List<User> getAllUsers();
    public User addUser(User user);
    public User updateUser(User user);
    public List<User> getFriends(long id);
}
