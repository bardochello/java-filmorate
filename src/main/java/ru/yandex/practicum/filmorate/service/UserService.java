package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, FriendshipStorage friendshipStorage) {
        this.userStorage = userStorage;
        this.friendshipStorage = friendshipStorage;
    }

    public User create(User user) {
        validate(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        validate(user);
        if (userStorage.findById(user.getId()).isEmpty()) {
            throw new NotFoundException("Пользователь с ID " + user.getId() + " не найден");
        }
        return userStorage.update(user);
    }

    public Optional<User> findById(int id) {
        return userStorage.findById(id);
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public void addFriend(int userId, int friendId) {
        if (userStorage.findById(userId).isEmpty() || userStorage.findById(friendId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        friendshipStorage.addFriendRequest(userId, friendId);
        friendshipStorage.addConfirmedFriend(friendId, userId);
    }

    public void confirmFriend(int userId, int friendId) {
        if (userStorage.findById(userId).isEmpty() || userStorage.findById(friendId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        friendshipStorage.confirmFriend(userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        if (userStorage.findById(userId).isEmpty() || userStorage.findById(friendId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        friendshipStorage.removeFriend(userId, friendId);
        friendshipStorage.removeFriend(userId, friendId);
    }

    public Collection<User> getFriends(int userId) {
        if (userStorage.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }
        return friendshipStorage.getFriends(userId).stream()
                .map(id -> userStorage.findById(id).orElse(null))
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(int userId, int otherId) {
        if (userStorage.findById(userId).isEmpty() || userStorage.findById(otherId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        Collection<Integer> userFriends = friendshipStorage.getFriends(userId);
        Collection<Integer> otherFriends = friendshipStorage.getFriends(otherId);
        return userFriends.stream()
                .filter(otherFriends::contains)
                .map(id -> userStorage.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void validate(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Некорректный email");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}