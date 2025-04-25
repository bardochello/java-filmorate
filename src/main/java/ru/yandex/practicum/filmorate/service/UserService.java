package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(int userId, int friendId) {
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void removeFriend(int userId, int friendId) {
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public Collection<User> getFriends(int userId) {
        User user = userStorage.findById(userId);
        return user.getFriends().stream()
                .map(userStorage::findById)
                .collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(int userId, int otherId) {
        User user = userStorage.findById(userId);
        User other = userStorage.findById(otherId);
        Set<Integer> commonFriends = new HashSet<>(user.getFriends());
        commonFriends.retainAll(other.getFriends());
        return commonFriends.stream()
                .map(userStorage::findById)
                .collect(Collectors.toList());
    }
}