package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>(); // Хранилище фильмов в памяти
    private final Logger logger = LoggerFactory.getLogger(FilmController.class); // Логгер для класса

    // Получение всех фильмов
    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    // Создание фильма
    @PostMapping
    public Film create(@RequestBody Film film) {
        validateFilm(film); // Валидация фильма
        film.setId(nextId()); // Установка id
        films.put(film.getId(), film); // Сохранение фильма в хранилище
        logger.info("Создан фильм с id: {}", film.getId());
        return film;
    }

    // Обновление существующего фильма
    @PutMapping
    public Film update(@RequestBody Film film) {
        Film existingFilm = films.get(film.getId());
        boolean isUpdated = false;

        if (film == null) {
            logger.error("Не передан film");
            throw new ValidationException("Фильм не может быть равен null");
        }

        if (film.getId() == null) {
            logger.error("Id фильма равен null");
            throw new ValidationException("ID обязателен для обновления");
        }

        //Обновление полей
        if (existingFilm == null) {
            logger.error("Фильм с ID {} не найден", film.getId());
            throw new NotFoundException("Фильм не существует");
        }

        if (film.getName() != null) {
            if (film.getName().isBlank()) {
                logger.error("Пустое название фильма");
                throw new ValidationException("Название не может быть пустым");
            }

            existingFilm.setName(film.getName());
            isUpdated = true;
        }

        if (film.getDescription() != null) {
            if (film.getDescription().length() > 200) {
                logger.error("Слишком длинное описание");
                throw new ValidationException("Описание превышает 200 символов");
            }
            existingFilm.setDescription(film.getDescription());
            isUpdated = true;
        }

        if (film.getReleaseDate() != null) {
            LocalDate minDate = LocalDate.of(1895, 12, 28);
            if (film.getReleaseDate().isBefore(minDate)) {
                logger.error("Некорректная дата релиза");
                throw new ValidationException("Дата релиза раньше 28 декабря 1895");
            }
            existingFilm.setReleaseDate(film.getReleaseDate());
            isUpdated = true;
        }

        if (film.getDuration() != 0) {
            if (film.getDuration() < 0) {
                logger.error("Отрицательная продолжительность");
                throw new ValidationException("Продолжительность не может быть отрицательной");
            }
            existingFilm.setDuration(film.getDuration());
            isUpdated = true;
        }

        if (!isUpdated) {
            logger.warn("Нет данных для обновления");
            throw new ValidationException("Не передано ни одного поля для обновления");
        }

        films.put(existingFilm.getId(), existingFilm);
        logger.info("Обновлен фильм: {}", existingFilm);
        return existingFilm;
    }

    // Генерируем уникальный id для film
    private long nextId() {
        long currentMaxId = films.keySet() //текущий максимальный ID из films или 0, если коллекция пустая
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);

        return ++currentMaxId;
    }

    //Валидация film
    private void validateFilm(Film film) {
        final int SYMBOLS = 200;
        final LocalDate RELEASE_DATE = LocalDate.of(1895, 12, 28);

        if (film == null) {
            logger.error("Не передан film");
            throw new ValidationException("Фильм не может быть равен null");
        } else {
            if (film.getName() == null || film.getName().isBlank()) {
                logger.error("Название фильма пустое");
                throw new ValidationException("Название фильма не может быть пустым!");
            }

            if (film.getDescription().length() > SYMBOLS) {
                logger.error("Длинное описание фильма");
                throw new ValidationException("Максимальная длина описания фильма " + SYMBOLS + " символов!");
            }

            if (film.getReleaseDate().isBefore(RELEASE_DATE)) {
                logger.error("Дата выпуска раньше 28 декабря 1895 года");
                throw new ValidationException("Дата выпуска должна быть не раньше 28 декабря 1895 года");
            }

            if (film.getDuration() < 0) {
                logger.error("Продолжительность фильма меньше 0");
                throw new ValidationException("Продолжительность фильма не должна быть меньше 0");
            }
        }
    }
}
