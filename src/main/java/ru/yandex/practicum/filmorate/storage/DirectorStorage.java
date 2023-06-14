package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {
    Director getDirectorById(long id);

    List<Director> getAllDirectors();

    Director addDirector(Director director);

    Director updateDirector(Director director);

    Director removeDirector(long id);
}
