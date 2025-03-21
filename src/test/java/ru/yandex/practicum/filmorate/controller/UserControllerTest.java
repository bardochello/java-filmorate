package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserControllerTest {
    @Autowired
    private UserContoller userController;

    @Test
    public void testCreateUserWithEmptyEmail() {
        User user = new User();
        user.setEmail(""); // Пустой email
        user.setLogin("login");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(ValidationException.class, () -> userController.create(user),
                "Ожидается исключение для пустого email");
    }

    @Test
    public void testCreateUserWithInvalidEmail() {
        User user = new User();
        user.setEmail("invalid-email"); // Email без @
        user.setLogin("login");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(ValidationException.class, () -> userController.create(user),
                "Ожидается исключение для email без @");
    }

    @Test
    public void testCreateUserWithEmptyLogin() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin(""); // Пустой логин
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(ValidationException.class, () -> userController.create(user),
                "Ожидается исключение для пустого логина");
    }

    @Test
    public void testCreateUserWithLoginWithSpaces() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("log in"); // Логин с пробелом
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(ValidationException.class, () -> userController.create(user),
                "Ожидается исключение для логина с пробелами");
    }

    @Test
    public void testCreateUserWithFutureBirthday() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.now().plusDays(1)); // Дата в будущем

        assertThrows(ValidationException.class, () -> userController.create(user),
                "Ожидается исключение для даты рождения в будущем");
    }

    @Test
    public void testCreateUserWithEmptyName() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("login");
        user.setName(""); // Пустое имя
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User createdUser = userController.create(user);
        assertEquals("login", createdUser.getName(), "Имя должно быть равно логину при пустом name");
    }

    @Test
    public void testCreateUserSuccessfully() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("login");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User createdUser = userController.create(user);
        assertNotNull(createdUser.getId(), "ID созданного пользователя не должен быть null");
        assertEquals("Test User", createdUser.getName(), "Имя пользователя должно совпадать");
    }
}
