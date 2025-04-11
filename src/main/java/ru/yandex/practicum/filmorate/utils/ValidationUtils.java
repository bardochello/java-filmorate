package ru.yandex.practicum.filmorate.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Map;

public class ValidationUtils {
    private static final int MAX_DESCRIPTION_LENGTH = 200;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private static final Logger logger = LoggerFactory.getLogger(FilmController.class); // Логгер для класса

    //Валидация film
    public static void validateFilm(Film film) {
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

    public static void validateFilmUpdate(Film film, Map<Integer, Film> films) {
        if (film == null) {
            logger.error("Не передан film");
            throw new ValidationException("Фильм не может быть равен null");
        }
        if (film.getId() == null) {
            logger.error("Id фильма равен null");
            throw new ValidationException("ID обязателен для обновления");
        }
        if (!films.containsKey(film.getId())) {
            logger.error("Фильм с ID {} не найден", film.getId());
            throw new NotFoundException("Фильм не существует");
        }
        // Дополнительные проверки полей, если они переданы
        if (film.getName() != null && film.getName().isBlank()) {
            logger.error("Пустое название фильма");
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            logger.error("Слишком длинное описание");
            throw new ValidationException("Описание превышает 200 символов");
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            logger.error("Некорректная дата релиза");
            throw new ValidationException("Дата релиза раньше 28 декабря 1895");
        }
        if (film.getDuration() != 0 && film.getDuration() < 0) {
            logger.error("Отрицательная продолжительность");
            throw new ValidationException("Продолжительность не может быть отрицательной");
        }
    }

    //Валидация user
    public static void validateUser(User user) {
        if (user == null) {
            logger.error("Запрос с пустым телом");
            throw new ValidationException("Тело запроса не может быть пустым");
        }

        // Валидация email
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            logger.error("Некорректный email: {}", user.getEmail());
            throw new ValidationException("Email обязателен и должен содержать @");
        }

        // Валидация логина
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            logger.error("Некорректный логин: {}", user.getLogin());
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        }

        // Валидация даты рождения
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            logger.error("Некорректная дата рождения: {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    // Валидация пользователя при обновлении
    public static void validateUserUpdate(User user, Map<Integer, User> users) {
        if (user.getId() == null || !users.containsKey(user.getId())) {
            logger.error("Пользователь с ID {} не найден", user.getId());
            throw new NotFoundException("Пользователь не существует");
        }
        validateUser(user); // Повторная проверка основных полей
    }
}
