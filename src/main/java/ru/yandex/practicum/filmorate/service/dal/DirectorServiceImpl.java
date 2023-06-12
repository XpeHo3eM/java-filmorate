package ru.yandex.practicum.filmorate.service.dal;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.entity.EntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.entity.EntityNotDeletedException;
import ru.yandex.practicum.filmorate.exception.entity.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Service
public class DirectorServiceImpl implements DirectorService {
    private final DirectorStorage storage;

    public DirectorServiceImpl(DirectorStorage storage) {
        this.storage = storage;
    }

    @Override
    public Director getDirectorById(long id) {
        Director directorOnDb = storage.getDirectorById(id);

        if (directorOnDb == null) {
            throw new EntityNotFoundException(String.format("Режиссер с ID = %s не найден", id));
        }

        return directorOnDb;
    }

    @Override
    public List<Director> getAllDirectors() {
        return storage.getAllDirectors();
    }

    @Override
    public Director addDirector(Director director) {
        Director directorOnDb = storage.addDirector(director);

        if (directorOnDb == null) {
            throw new EntityAlreadyExistsException(String.format("Режиссер с ID = %s уже добавлен", director.getId()));
        }

        return directorOnDb;
    }

    @Override
    public Director updateDirector(Director director) {
        Director directorOnDb = storage.updateDirector(director);

        if (directorOnDb == null) {
            throw new EntityNotFoundException(String.format("Режиссер с ID = %s не найден", director.getId()));
        }

        return directorOnDb;
    }

    @Override
    public Director removeDirector(long id) {
        Director directorOnDb = storage.removeDirector(id);

        if (directorOnDb != null) {
            throw new EntityNotDeletedException(String.format("Режиссер с ID = %s найден после удаления", id));
        }

        return null;
    }
}
