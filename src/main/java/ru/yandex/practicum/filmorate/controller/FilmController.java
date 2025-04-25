package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.utils.ValidationUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger logger = LoggerFactory.getLogger(FilmController.class); // Логгер для класса
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    // Получение всех фильмов
    @GetMapping
    public Collection<Film> findAll() {
        logger.info("Получен запрос на получение всех фильмов");
        return filmStorage.findAll();
    }

    // Создание фильма
    @PostMapping
    public Film create(@RequestBody Film film) {
        ValidationUtils.validateFilm(film);
        Film createdFilm = filmStorage.create(film);
        logger.info("Создан фильм с ID: {}", createdFilm.getId());
        return createdFilm;
    }

    // Обновление существующего фильма
    @PutMapping
    public Film update(@RequestBody Film film) {
        ValidationUtils.validateFilmUpdate(film, filmStorage);
        Film updatedFilm = filmStorage.update(film);
        logger.info("Обновлен фильм с ID: {}", updatedFilm.getId());
        return updatedFilm;
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable int id) {
        Film film = filmStorage.findById(id);
        logger.info("Получен фильм с ID: {}", id);
        return film;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id, userId);
        logger.info("Пользователь с ID {} поставил лайк фильму с ID {}", userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        filmService.removeLike(id, userId);
        logger.info("Пользователь с ID {} удалил лайк с фильма с ID {}", userId, id);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        Collection<Film> popularFilms = filmService.getPopularFilms(count);
        logger.info("Получен список {} популярных фильмов", count);
        return popularFilms;
    }
}
