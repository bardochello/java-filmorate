package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.utils.ValidationUtils;
import java.util.Collection;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    // Получение всех пользователей
    @GetMapping
    public Collection<User> findAll() {
        logger.info("Получен запрос на получение всех пользователей");
        return userStorage.findAll();
    }

    // Создание нового пользователя
    @PostMapping
    public User create(@RequestBody User user) {
        ValidationUtils.validateUser(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        User createdUser = userStorage.create(user);
        logger.info("Создан пользователь с ID: {}", createdUser.getId());
        return createdUser;
    }

    // Обновление существующего пользователя
    @PutMapping
    public User updateUser(@RequestBody User user) {
        ValidationUtils.validateUserUpdate(user, userStorage);
        User existingUser = userStorage.findById(user.getId());
        existingUser.setEmail(user.getEmail());
        existingUser.setLogin(user.getLogin());
        existingUser.setBirthday(user.getBirthday());
        String newName = user.getName();
        if (newName == null || newName.isBlank()) {
            existingUser.setName(user.getLogin());
        } else {
            existingUser.setName(newName);
        }
        User updatedUser = userStorage.update(existingUser);
        logger.info("Обновлен пользователь с ID: {}", updatedUser.getId());
        return updatedUser;
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable int id) {
        User user = userStorage.findById(id);
        logger.info("Получен пользователь с ID: {}", id);
        return user;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.addFriend(id, friendId);
        logger.info("Пользователь с ID {} добавил в друзья пользователя с ID {}", id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.removeFriend(id, friendId);
        logger.info("Пользователь с ID {} удалил из друзей пользователя с ID {}", id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable int id) {
        Collection<User> friends = userService.getFriends(id);
        logger.info("Получен список друзей пользователя с ID {}", id);
        return friends;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        Collection<User> commonFriends = userService.getCommonFriends(id, otherId);
        logger.info("Получен список общих друзей пользователей с ID {} и {}", id, otherId);
        return commonFriends;
    }
}
