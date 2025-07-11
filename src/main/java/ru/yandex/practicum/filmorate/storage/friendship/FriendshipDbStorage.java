package ru.yandex.practicum.filmorate.storage.friendship;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Component
public class FriendshipDbStorage implements FriendshipStorage {
    private static final Logger logger = LoggerFactory.getLogger(FriendshipDbStorage.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendshipDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    @Override
    public void addFriend(int userId, int friendId) {
        String checkSql = "SELECT COUNT(*) FROM friends WHERE user_id = ? AND friend_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, userId, friendId);
        if (count != null && count > 0) {
            logger.warn("Дружба между пользователями уже существует: userId={}, friendId={}", userId, friendId);
            return;
        }


        String insertSql = "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, 'confirmed')";
        jdbcTemplate.update(insertSql, userId, friendId);
        logger.info("Добавлен друг: userId={} -> friendId={}", userId, friendId);
    }

    @Override
    public void addFriendRequest(int userId, int friendId) {
        String sql = "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, 'pending')";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void confirmFriend(int userId, int friendId) {
        String updateSql = "UPDATE friends SET status = 'confirmed' WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(updateSql, userId, friendId);
    }

    @Override
    public void addConfirmedFriend(int userId, int friendId) {
        String sql = "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, 'confirmed')";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
        logger.info("Друзья удалены: userId={} -> friendId={}", userId, friendId);
    }

    @Override
    public Collection<Integer> getFriends(int userId) {
        String sql = "SELECT friend_id FROM friends WHERE user_id = ?";
        logger.info("Поиск друзей по userId={}", userId);
        Collection<Integer> friends = jdbcTemplate.queryForList(sql, Integer.class, userId);
        logger.info("Найдены друзья {} для userId={}", friends.size(), userId);
        return friends;
    }
}
