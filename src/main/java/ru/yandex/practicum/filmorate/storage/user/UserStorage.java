package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> findAll(); // Возвращает список всех пользователей в памяти

    User create(User user); // Создание фильма

    User update(User user); // Обновление фильма

    User findById(int id); // Возвращает пользователя по id

    void delete(int id); // Удаляет пользователя

}
