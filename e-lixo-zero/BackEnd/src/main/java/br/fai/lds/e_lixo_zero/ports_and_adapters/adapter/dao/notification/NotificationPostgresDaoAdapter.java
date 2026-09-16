package br.fai.lds.e_lixo_zero.ports_and_adapters.adapter.dao.notification;

import br.fai.lds.e_lixo_zero.domain.NotificationModel;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.notification.NotificationDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NotificationPostgresDaoAdapter implements NotificationDao {

    private final Connection connection;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public NotificationPostgresDaoAdapter(final Connection connection) {
        this.connection = connection;
    }

    @Override
    public int add(final NotificationModel entity) {
        final String sql = "INSERT INTO notifications (user_id, title, message, notification_type, is_read, sent_at) VALUES (?, ?, ?, ?, ?, current_timestamp)";
        try {
            connection.setAutoCommit(false);
            final PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"notification_id"});
            preparedStatement.setInt(1, entity.getUserId());
            preparedStatement.setString(2, entity.getTitle());
            preparedStatement.setString(3, entity.getMessage());
            preparedStatement.setString(4, entity.getNotificationType());
            preparedStatement.setBoolean(5, entity.isRead());
            preparedStatement.executeUpdate();

            final ResultSet resultSet = preparedStatement.getGeneratedKeys();
            int id = 0;
            if (resultSet.next()) {
                id = resultSet.getInt(1);
            }
            resultSet.close();
            preparedStatement.close();
            connection.commit();
            return id;
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(final int id) {
        final String sql = "DELETE FROM notifications WHERE notification_id = ?";
        try {
            connection.setAutoCommit(false);
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.commit();
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public NotificationModel readById(final int id) {
        final String sql = "SELECT * FROM notifications WHERE notification_id = ?";
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            final ResultSet resultSet = preparedStatement.executeQuery();
            NotificationModel notification = null;
            if (resultSet.next()) {
                notification = mapNotification(resultSet);
            }
            resultSet.close();
            preparedStatement.close();
            return notification;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<NotificationModel> readAll() {
        final String sql = "SELECT * FROM notifications";
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            final ResultSet resultSet = preparedStatement.executeQuery();
            final List<NotificationModel> notifications = new ArrayList<>();
            while (resultSet.next()) {
                notifications.add(mapNotification(resultSet));
            }
            resultSet.close();
            preparedStatement.close();
            return notifications;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<NotificationModel> readByUserId(final int userId) {
        final String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY sent_at DESC";
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userId);
            final ResultSet resultSet = preparedStatement.executeQuery();
            final List<NotificationModel> notifications = new ArrayList<>();
            while (resultSet.next()) {
                notifications.add(mapNotification(resultSet));
            }
            resultSet.close();
            preparedStatement.close();
            return notifications;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<NotificationModel> readUnreadByUserId(final int userId) {
        final String sql = "SELECT * FROM notifications WHERE user_id = ? AND is_read = false ORDER BY sent_at DESC";
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userId);
            final ResultSet resultSet = preparedStatement.executeQuery();
            final List<NotificationModel> notifications = new ArrayList<>();
            while (resultSet.next()) {
                notifications.add(mapNotification(resultSet));
            }
            resultSet.close();
            preparedStatement.close();
            return notifications;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateInformation(final int id, final NotificationModel entity) {
        final String sql = "UPDATE notifications SET user_id = ?, title = ?, message = ?, notification_type = ?, is_read = ?, sent_at = ? WHERE notification_id = ?";
        try {
            connection.setAutoCommit(false);
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, entity.getUserId());
            preparedStatement.setString(2, entity.getTitle());
            preparedStatement.setString(3, entity.getMessage());
            preparedStatement.setString(4, entity.getNotificationType());
            preparedStatement.setBoolean(5, entity.isRead());
            preparedStatement.setTimestamp(6, entity.getSentAt() != null ? Timestamp.valueOf(entity.getSentAt()) : null);
            preparedStatement.setInt(7, id);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.commit();
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public void markAsRead(final int id) {
        final String sql = "UPDATE notifications SET is_read = true WHERE notification_id = ?";
        try {
            connection.setAutoCommit(false);
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.commit();
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    private NotificationModel mapNotification(final ResultSet resultSet) throws SQLException {
        final NotificationModel notification = new NotificationModel();
        notification.setId(resultSet.getInt("notification_id"));
        notification.setUserId(resultSet.getInt("user_id"));
        notification.setTitle(resultSet.getString("title"));
        notification.setMessage(resultSet.getString("message"));
        notification.setNotificationType(resultSet.getString("notification_type"));
        notification.setRead(resultSet.getBoolean("is_read"));
        final Timestamp sentAt = resultSet.getTimestamp("sent_at");
        notification.setSentAt(sentAt != null ? sentAt.toLocalDateTime().format(formatter) : "");
        return notification;
    }
}
