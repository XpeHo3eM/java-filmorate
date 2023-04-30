package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    public User getUserById(long id);
    public List<User> getAllUsers();
    public User addUser(User user);
    public User updateUser(User user);
}
