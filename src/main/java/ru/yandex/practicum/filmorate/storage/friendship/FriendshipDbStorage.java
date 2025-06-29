package ru.yandex.practicum.filmorate.storage.friendship;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.Collection;

@Component
public class FriendshipDbStorage implements FriendshipStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendshipDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriendRequest(int userId, int friendId) {
        String sql = "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, 'pending')";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void confirmFriend(int userId, int friendId) {
        // Проверяем, есть ли запрос от friendId к userId
        String checkSql = "SELECT COUNT(*) FROM friends WHERE user_id = ? AND friend_id = ? AND status = 'pending'";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, friendId, userId);
        if (count == null || count == 0) {
            throw new NotFoundException("Запрос в друзья от пользователя " + friendId + " к " + userId + " не найден");
        }
        // Обновляем статус запроса на confirmed
        String updateSql = "UPDATE friends SET status = 'confirmed' WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(updateSql, friendId, userId);
        // Добавляем обратную дружбу
        String insertSql = "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, 'confirmed')";
        jdbcTemplate.update(insertSql, userId, friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public Collection<Integer> getFriends(int userId) {
        String sql = "SELECT friend_id FROM friends WHERE user_id = ? AND status = 'confirmed'";
        return jdbcTemplate.queryForList(sql, Integer.class, userId);
    }
}