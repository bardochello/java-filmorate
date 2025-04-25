package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        validate(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        validateUpdate(film);
        return filmStorage.update(film);
    }

    public Film findById(int id) {
        Film film = filmStorage.findById(id);
        if (film == null) {
            throw new NotFoundException("Фильм с ID " + id + " не найден");
        }
        return film;
    }

    public void addLike(int filmId, int userId) {
        Film film = findById(filmId);
        if (userStorage.findById(userId) == null) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }
        film.getLikes().add(userId);
        filmStorage.update(film);
    }

    public void removeLike(int filmId, int userId) {
        Film film = findById(filmId);
        if (userStorage.findById(userId) == null) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }
        film.getLikes().remove(userId);
        filmStorage.update(film);
    }

    public Collection<Film> getPopularFilms(int count) {
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt(f -> -f.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Описание не может превышать 200 символов");
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }

    private void validateUpdate(Film film) {
        if (film.getId() == null) {
            throw new ValidationException("ID обязателен для обновления");
        }
        if (filmStorage.findById(film.getId()) == null) {
            throw new NotFoundException("Фильм с ID " + film.getId() + " не найден");
        }
        validate(film); // Повторная проверка общих условий
    }
}