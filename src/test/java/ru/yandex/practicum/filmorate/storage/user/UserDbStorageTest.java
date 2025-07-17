package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class})
class UserDbStorageTest {
    private final UserDbStorage userStorage;

    @Test
    public void testCreateUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("test");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        User createdUser = userStorage.create(user);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isNotNull();
        assertThat(createdUser.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    public void testFindUserById() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("test");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        User createdUser = userStorage.create(user);

        Optional<User> userOptional = userStorage.findById(createdUser.getId());

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u -> assertThat(u).hasFieldOrPropertyWithValue("id", createdUser.getId()));
    }

    @Test
    public void testUpdateUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("test");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        User createdUser = userStorage.create(user);

        createdUser.setName("Updated User");
        userStorage.update(createdUser);
        Optional<User> updatedUser = userStorage.findById(createdUser.getId());

        assertThat(updatedUser)
                .isPresent()
                .hasValueSatisfying(u -> assertThat(u).hasFieldOrPropertyWithValue("name", "Updated User"));
    }

    @Test
    public void testFindAllUsers() {
        User user1 = new User();
        user1.setEmail("test1@example.com");
        user1.setLogin("test1");
        user1.setName("Test User 1");
        user1.setBirthday(LocalDate.of(2000, 1, 1));
        userStorage.create(user1);

        User user2 = new User();
        user2.setEmail("test2@example.com");
        user2.setLogin("test2");
        user2.setName("Test User 2");
        user2.setBirthday(LocalDate.of(2000, 1, 2));
        userStorage.create(user2);

        Collection<User> users = userStorage.findAll();
        assertThat(users).hasSize(2);
    }

    @Test
    public void testDeleteUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("test");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        User createdUser = userStorage.create(user);

        userStorage.delete(createdUser.getId());
        Optional<User> userOptional = userStorage.findById(createdUser.getId());

        assertThat(userOptional).isEmpty();
    }
}