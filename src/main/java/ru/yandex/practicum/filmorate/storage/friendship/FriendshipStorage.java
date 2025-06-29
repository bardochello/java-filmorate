package ru.yandex.practicum.filmorate.storage.friendship;

import java.util.Collection;

public interface FriendshipStorage {
    void addFriendRequest(int userId, int friendId);

    void confirmFriend(int userId, int friendId);

    void removeFriend(int userId, int friendId);

    Collection<Integer> getFriends(int userId);

    void addConfirmedFriend(int userId, int friendId);
}