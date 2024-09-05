package com.example.toysocialnetworkgui.repository.db;

import com.example.toysocialnetworkgui.domain.Friendship;
import com.example.toysocialnetworkgui.domain.Tuple;
import com.example.toysocialnetworkgui.domain.User;
import com.example.toysocialnetworkgui.repository.Repository;
import com.example.toysocialnetworkgui.validators.Validator;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class FriendshipDbRepository implements Repository<Tuple<Long, Long>, Friendship> {
    private String url;
    private String username;
    private String password;
    private Validator<Friendship> validator;

    public FriendshipDbRepository(String url, String username, String password, Validator<Friendship> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Friendship findOne(Tuple<Long, Long> tuple) {

        Friendship friendship;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships WHERE (id1=? AND id2=?) OR (id1=? AND id2=?)")) {
            statement.setLong(1, tuple.getLeftMember());
            statement.setLong(2, tuple.getRightMember());

            statement.setLong(3, tuple.getRightMember());
            statement.setLong(4, tuple.getLeftMember());

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {

                LocalDate date = resultSet.getDate("date").toLocalDate();

                friendship = new Friendship(tuple.getLeftMember(), tuple.getRightMember(), date);
                friendship.setId(tuple);
                return friendship;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Friendship> findAll() {
        Set<Friendship> friendships = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                LocalDate date = resultSet.getDate("date").toLocalDate();

                Friendship friendship = new Friendship(id1, id2, date);
                friendship.setId(new Tuple<Long, Long>(id1, id2));
                friendships.add(friendship);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }

    @Override
    public Friendship save(Friendship entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity must not be null!");

        validator.validate(entity);

        if (this.findOne(entity.getId()) != null) {
            return this.findOne(entity.getId());
        }

        String sql = "insert into friendships (id1, id2, date ) values (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, entity.getId().getLeftMember());
            ps.setLong(2, entity.getId().getRightMember());
            ps.setDate(3, Date.valueOf(entity.getDate()));

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Friendship delete(Tuple<Long, Long> tuple) {
        if (tuple == null)
            throw new IllegalArgumentException("Id must not be null");

        Friendship deletedFriendship = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM friendships WHERE (id1 = ? AND id2 = ?) OR  (id1 = ? AND id2 = ?) RETURNING * ")) {
            statement.setLong(1, tuple.getLeftMember());
            statement.setLong(2, tuple.getRightMember());

            statement.setLong(3, tuple.getRightMember());
            statement.setLong(4, tuple.getLeftMember());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                LocalDate date = resultSet.getDate("date").toLocalDate();

                deletedFriendship = new Friendship(tuple.getLeftMember(), tuple.getRightMember(), date);
                deletedFriendship.setId(tuple);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deletedFriendship;
    }

    @Override
    public Friendship update(Friendship entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity must not be null");

        validator.validate(entity);

        String sql = "update friendships SET date = ? WHERE (id1 = ? AND id2 = ?) OR  (id1 = ? AND id2 = ?)";

        int row_count = 0;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDate(1, Date.valueOf(entity.getDate()));
            statement.setLong(2, entity.getId().getLeftMember());
            statement.setLong(3, entity.getId().getRightMember());
            statement.setLong(4, entity.getId().getRightMember());
            statement.setLong(5, entity.getId().getLeftMember());

            row_count = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (row_count > 0) {
            return null;
        }
        return entity;
    }

    @Override
    public void clear() {
        int size = 0;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM friendships")) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getNumberOfEntites() {
        int size = 0;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships");
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

