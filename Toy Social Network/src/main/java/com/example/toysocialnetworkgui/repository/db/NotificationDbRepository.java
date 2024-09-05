package com.example.toysocialnetworkgui.repository.db;

import com.example.toysocialnetworkgui.domain.Notification;
import com.example.toysocialnetworkgui.domain.Notification;
import com.example.toysocialnetworkgui.domain.Tuple;
import com.example.toysocialnetworkgui.domain.User;
import com.example.toysocialnetworkgui.repository.Repository;
import com.example.toysocialnetworkgui.validators.Validator;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class NotificationDbRepository implements Repository<Tuple<Long, Long>, Notification> {
    private String url;
    private String username;
    private String password;
    private Validator<Notification> validator;

    public NotificationDbRepository(String url, String username, String password, Validator<Notification> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Notification findOne(Tuple<Long, Long> tuple) {

        Notification Notification;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM events_users WHERE (id_user=? AND id_event=?)")) {
            statement.setLong(1, tuple.getLeftMember());
            statement.setLong(2, tuple.getRightMember());

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {

                LocalDate date = resultSet.getDate("date").toLocalDate();

                Notification = new Notification(tuple.getLeftMember(), tuple.getRightMember(), date);
                Notification.setId(tuple);
                return Notification;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Notification> findAll() {
        Set<Notification> notifications = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM events_users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id_user");
                Long id2 = resultSet.getLong("id_event");
                LocalDate date = resultSet.getDate("date").toLocalDate();

                Notification notification = new Notification(id1, id2, date);
                notification.setId(new Tuple<Long, Long>(id1, id2));
                notifications.add(notification);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }

    @Override
    public Notification save(Notification entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity must not be null!");

        validator.validate(entity);

        if (this.findOne(entity.getId()) != null) {
            return this.findOne(entity.getId());
        }

        String sql = "insert into events_users (id_user, id_event, date ) values (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, entity.getId().getLeftMember());
            ps.setLong(2, entity.getId().getRightMember());
            ps.setDate(3, Date.valueOf(entity.getDate()));
            ps.setDate(3, Date.valueOf(entity.getDate()));

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Notification delete(Tuple<Long, Long> tuple) {
        if (tuple == null)
            throw new IllegalArgumentException("Id must not be null");

        Notification deletedNotification = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM events_users WHERE (id_user = ? AND id_event = ?) RETURNING * ")) {
            statement.setLong(1, tuple.getLeftMember());
            statement.setLong(2, tuple.getRightMember());

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                LocalDate date = resultSet.getDate("date").toLocalDate();

                deletedNotification = new Notification(tuple.getLeftMember(), tuple.getRightMember(), date);
                deletedNotification.setId(tuple);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deletedNotification;
    }

    @Override
    public Notification update(Notification entity) {
        return null;
    }

    @Override
    public void clear() {
        int size = 0;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM events_users")) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getNumberOfEntites() {
        int size = 0;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from events_users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                size++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return size;
    }

    @Override
    public Tuple<Long, Long> nextId() {
        return null;
    }




}

