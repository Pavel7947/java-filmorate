package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.mpa.MpaRepository;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class MpaService {
    private final MpaRepository mpaRepository;

    public Mpa getById(int id) {
        return mpaRepository.getById(id).orElseThrow(() -> new NotFoundException("Рейтинг с id: " + id + "не найден"));
    }

    public List<Mpa> getAll() {
        return mpaRepository.getAll();
    }
}
