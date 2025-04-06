package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utils.ValidationUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger logger = LoggerFactory.getLogger(FilmController.class); // Логгер для класса

    private final Map<Integer, Film> films = new HashMap<>();
    // Хранилище фильмов в памяти

    // Получение всех фильмов
    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    // Создание фильма
    @PostMapping
    public Film create(@RequestBody Film film) {
        ValidationUtils.validateFilm(film); // Валидация фильма
        film.setId(nextId()); // Установка id
        films.put(film.getId(), film); // Сохранение фильма в хранилище
        logger.info("Создан фильм с id: {}", film.getId());
        return film;
    }

    // Обновление существующего фильма
    @PutMapping
    public Film update(@RequestBody Film film) {
        ValidationUtils.validateFilmUpdate(film, films);
        Film existingFilm = films.get(film.getId());

        existingFilm.setName(film.getName());
        existingFilm.setDescription(film.getDescription());
        existingFilm.setReleaseDate(film.getReleaseDate());
        existingFilm.setDuration(film.getDuration());

        films.put(existingFilm.getId(), existingFilm);
        logger.info("Обновлен фильм: {}", existingFilm);
        return existingFilm;
    }


    // Генерируем уникальный id для film
    private Integer nextId() {
        int currentMaxId = films.keySet() //текущий максимальный ID из films или 0, если коллекция пустая
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);

        return ++currentMaxId;
    }
}
