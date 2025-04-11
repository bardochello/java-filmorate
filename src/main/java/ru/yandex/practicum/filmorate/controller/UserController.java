package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utils.ValidationUtils;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>(); // Хранилище пользователей в памяти
    private final Logger logger = LoggerFactory.getLogger(UserController.class); // Логгер для класса

    // Получение всех пользователей
    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    // Создание нового пользователя
    @PostMapping
    public User create(@RequestBody User user) {
        ValidationUtils.validateUser(user); // Валидация пользователя
        // Устанавливаем имя равным логину только если name не указано или пустое
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId()); // Установка id
        users.put(user.getId(), user); // Сохранение пользователя в хранилище
        logger.info("Создан пользователь с id: {}", user.getId());
        return user;
    }

    // Обновление существующего пользователя
    @PutMapping
    public User updateUser(@RequestBody User user) {
        ValidationUtils.validateUserUpdate(user, users);
        User existingUser = users.get(user.getId());

        existingUser.setEmail(user.getEmail());
        existingUser.setLogin(user.getLogin());
        existingUser.setBirthday(user.getBirthday());
        existingUser.setName(user.getName().isBlank() ? user.getLogin() : user.getName());

        logger.info("Обновлен пользователь: {}", existingUser);
        return existingUser;
    }

    // Генерируем уникальный id для film
    private int getNextId() {
        int currentMaxId = users.keySet() //текущий максимальный ID из films или 0, если коллекция пустая
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
