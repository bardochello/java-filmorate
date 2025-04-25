package ru.yandex.practicum.filmorate.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Map;

public class ValidationUtils {
    private static final int MAX_DESCRIPTION_LENGTH = 200;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private static final Logger logger = LoggerFactory.getLogger(FilmController.class); // Логгер для класса

    //Валидация film
    public static void validateFilm(Film film) {
        if (film == null) {
            logger.error("Не передан film");
            throw new ValidationException("Фильм не может быть равен null");
        } else {
            if (film.getName() == null || film.getName().isBlank()) {
                logger.error("Название фильма пустое");
                throw new ValidationException("Название фильма не может быть пустым!");
            }

            if (film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
                logger.error("Длинное описание фильма");
                throw new ValidationException("Максимальная длина описания фильма " + MAX_DESCRIPTION_LENGTH + " символов!");
            }

            if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
                logger.error("Дата выпуска раньше 28 декабря 1895 года");
                throw new ValidationException("Дата выпуска должна быть не раньше 28 декабря 1895 года");
            }

            if (film.getDuration() < 0) {
                logger.error("Продолжительность фильма меньше 0");
                throw new ValidationException("Продолжительность фильма не должна быть меньше 0");
            }
        }
    }

    public static void validateFilmUpdate(Film film, FilmStorage filmStorage) {
        if (film == null) {
            logger.error("Фильм равен null");
            throw new ValidationException("Фильм не может быть равен null");
        }
        if (film.getId() == null) {
            logger.error("ID фильма равен null");
            throw new ValidationException("ID обязателен для обновления");
        }
        try {
            filmStorage.findById(film.getId());
        } catch (NotFoundException e) {
            logger.error("Фильм с ID {} не найден", film.getId());
            throw new NotFoundException("Фильм не существует");
        }
        if (film.getName() != null && film.getName().isBlank()) {
            logger.error("Название фильма пустое");
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            logger.error("Описание длиннее {} символов", MAX_DESCRIPTION_LENGTH);
            throw new ValidationException("Описание превышает 200 символов");
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            logger.error("Дата релиза раньше {}", MIN_RELEASE_DATE);
            throw new ValidationException("Дата релиза раньше 28 декабря 1895");
        }
        if (film.getDuration() != 0 && film.getDuration() < 0) {
            logger.error("Продолжительность отрицательная: {}", film.getDuration());
            throw new ValidationException("Продолжительность не может быть отрицательной");
        }
    }

    //Валидация user
    public static void validateUser(User user) {
        if (user == null) {
            logger.error("Пользователь равен null");
            throw new ValidationException("Тело запроса не может быть пустым");
        }
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            logger.error("Некорректный email: {}", user.getEmail());
            throw new ValidationException("Email обязателен и должен содержать @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            logger.error("Некорректный логин: {}", user.getLogin());
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            logger.error("Дата рождения в будущем: {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    // Валидация пользователя при обновлении
    public static void validateUserUpdate(User user, UserStorage userStorage) {
        if (user == null) {
            logger.error("Пользователь равен null");
            throw new ValidationException("Тело запроса не может быть пустым");
        }
        if (user.getId() == null) {
            logger.error("ID пользователя равен null");
            throw new ValidationException("ID обязателен для обновления");
        }
        try {
            userStorage.findById(user.getId());
        } catch (NotFoundException e) {
            logger.error("Пользователь с ID {} не найден", user.getId());
            throw new NotFoundException("Пользователь не существует");
        }
        validateUser(user);
    }
}
