package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utils.ValidationUtils;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserContoller {
    private final Map<Long, User> users = new HashMap<>(); // Хранилище пользователей в памяти
    private final Logger logger = LoggerFactory.getLogger(UserContoller.class); // Логгер для класса

    // Получение всех пользователей
    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    // Создание нового пользователя
    @PostMapping
    public User create(@RequestBody User user) {
        ValidationUtils.validateUser(user); // Валидация пользователя
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin()); // Установка имени, если оно пустое
        }
        user.setId(getNextId()); // Установка id
        users.put(user.getId(), user); // Сохранение пользователя в хранилище
        logger.info("Создан пользователь с id: {}", user.getId());
        return user;
    }

    // Обновление существующего пользователя
    @PutMapping
    public User updateUser(@RequestBody User user) {
        if (user.getId() == null || !users.containsKey(user.getId())) {
            logger.error("Пользователь с ID {} не найден", user.getId());
            throw new NotFoundException("Пользователя не существует");
        }

        ValidationUtils.validateUser(user);
        User existingUser = users.get(user.getId());

        // Обновляем только пришедшие поля
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }

        if (user.getLogin() != null) {
            existingUser.setLogin(user.getLogin());
        }

        if (user.getBirthday() != null) {
            existingUser.setBirthday(user.getBirthday());
        }

        // Обработка имени. Если пришло пустое - ставим логин
        if (user.getName() != null) {
            existingUser.setName(
                    user.getName().isBlank() ? user.getLogin() : user.getName()
            );
        }

        logger.info("Обновлен пользователь: {}", existingUser);
        return existingUser;
    }

    // Генерируем уникальный id для film
    private long getNextId() {
        long currentMaxId = users.keySet() //текущий максимальный ID из films или 0, если коллекция пустая
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
