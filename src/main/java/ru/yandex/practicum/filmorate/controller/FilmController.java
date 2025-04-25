package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger logger = LoggerFactory.getLogger(FilmController.class); // Логгер для класса
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    // Получение всех фильмов
    @GetMapping
    public Collection<Film> findAll() {
        logger.info("Получен запрос на получение всех фильмов");
        return filmService.findAll();
    }

    // Создание фильма
    @PostMapping
    public Film create(@RequestBody Film film) {
        Film createdFilm = filmService.create(film);
        logger.info("Создан фильм с ID: {}", createdFilm.getId());
        return createdFilm;
    }

    // Обновление существующего фильма
    @PutMapping
    public Film update(@RequestBody Film film) {
        Film updatedFilm = filmService.update(film);
        logger.info("Обновлен фильм с ID: {}", updatedFilm.getId());
        return updatedFilm;
    }

    //Поиск фильма по id
    @GetMapping("/{id}")
    public Film findById(@PathVariable int id) {
        Film film = filmService.findById(id);
        logger.info("Получен фильм с ID: {}", id);
        return film;
    }

    // Поставить лайк на фильм по id от пользователя по userId
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id, userId);
        logger.info("Пользователь с ID {} поставил лайк фильму с ID {}", userId, id);
    }

    // Удалить лайк на фильм по id от пользователя по userId
    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        filmService.removeLike(id, userId);
        logger.info("Пользователь с ID {} удалил лайк с фильма с ID {}", userId, id);
    }

    // Сортировка фильмов по кол-ву лайков (популярности)
    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        Collection<Film> popularFilms = filmService.getPopularFilms(count);
        logger.info("Получен список {} популярных фильмов", count);
        return popularFilms;
    }
}
