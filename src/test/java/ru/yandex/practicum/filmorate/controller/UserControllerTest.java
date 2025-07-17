package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
class UserControllerTest {
    @Autowired
    private UserController userController;

    @Test
    public void testUpdateUserSuccessfully() {
        User initialUser = new User();
        initialUser.setEmail("initial@example.com");
        initialUser.setLogin("initialLogin");
        initialUser.setName("Initial User");
        initialUser.setBirthday(LocalDate.of(2000, 1, 1));
        User createdUser = userController.create(initialUser);

        User updatedUser = new User();
        updatedUser.setId(createdUser.getId());
        updatedUser.setEmail("updated@example.com");
        updatedUser.setLogin("updatedLogin");
        updatedUser.setName("Updated User");
        updatedUser.setBirthday(LocalDate.of(2000, 1, 1));

        User result = userController.update(updatedUser);
        assertEquals("updated@example.com", result.getEmail(), "Email должен обновиться");
        assertEquals("updatedLogin", result.getLogin(), "Логин должен обновиться");
        assertEquals("Updated User", result.getName(), "Имя должно обновиться");
        assertEquals(LocalDate.of(2000, 1, 1), result.getBirthday(), "Дата рождения не должна измениться");
    }
}