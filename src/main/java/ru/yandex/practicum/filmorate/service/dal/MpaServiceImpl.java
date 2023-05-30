package ru.yandex.practicum.filmorate.service.dal;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.entity.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Service
public class MpaServiceImpl implements MpaService {
    private final MpaStorage mpaStorage;

    public MpaServiceImpl(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    @Override
    public List<Mpa> getMpas() {
        return mpaStorage.getMpas();
    }

    @Override
    public Mpa getMpa(Integer id) {
        Mpa mpaOnDb = mpaStorage.getMpa(id);

        if (mpaOnDb == null) {
            throw new EntityNotFoundException(String.format("Рейтинг с ID = %s не найден", id));
        }

        return mpaOnDb;
    }
}
