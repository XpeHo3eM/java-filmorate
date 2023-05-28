package ru.yandex.practicum.filmorate.service.dal;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.genre.GenreEntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class GenreServiceImpl implements GenreService {
    private final GenreStorage genreStorage;

    public GenreServiceImpl(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    @Override
    public List<Genre> getGenres() {
        return genreStorage.getGenres();
    }

    @Override
    public Genre getGenre(Integer id) {
        Genre genreOnDb = genreStorage.getGenre(id);

        if (genreOnDb == null) {
            throw new GenreEntityNotFoundException(String.format("Жанр с ID = %s не найден", id));
        }

        return genreOnDb;
    }
}
