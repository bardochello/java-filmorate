package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAll() {
        logger.info("Получен запрос на получение всех пользователей");
        return userService.findAll();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        logger.info("Создание пользователя: {}", user.getLogin());
        return userService.create(user);
    }

    @PutMapping
    public User update(@RequestBody User user) {
        logger.info("Обновление пользователя с ID: {}", user.getId());
        return userService.update(user);
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable int id) {
        logger.info("Получен запрос на пользователя с ID: {}", id);
        return userService.findById(id).orElseThrow(() -> new NotFoundException("Пользователь с ID " + id + " не найден"));
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable int id, @PathVariable int friendId) {
        logger.info("Пользователь {} добавляет в друзья {}", id, friendId);
        userService.addFriend(id, friendId);
        return userService.findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    @PutMapping("/{id}/friends/{friendId}/confirm")
    public void confirmFriend(@PathVariable int id, @PathVariable int friendId) {
        logger.info("Пользователь {} подтверждает дружбу с {}", id, friendId);
        userService.confirmFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable int id, @PathVariable int friendId) {
        logger.info("Пользователь {} удаляет из друзей {}", id, friendId);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable int id) {
        logger.info("Получен запрос на список друзей пользователя {}", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        logger.info("Получен запрос на общих друзей пользователей {} и {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }
}