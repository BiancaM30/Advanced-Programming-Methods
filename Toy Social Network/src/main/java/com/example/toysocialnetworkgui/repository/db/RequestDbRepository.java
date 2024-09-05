package com.example.toysocialnetworkgui.repository.db;


import com.example.toysocialnetworkgui.domain.*;
import com.example.toysocialnetworkgui.repository.Repository;
import com.example.toysocialnetworkgui.validators.Validator;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class RequestDbRepository implements Repository<Tuple<Long, Long>, FriendshipRequest> {
    private String url;
    private String username;
    private String password;
    private Validator<FriendshipRequest> validator;

    public RequestDbRepository(String url, String username, String password, Validator<FriendshipRequest> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    // Search for a request with pending status
    @Override
    public FriendshipRequest findOne(Tuple<Long, Long> tuple) {

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM requests WHERE (id1=? AND id2=? ) OR (id1=? AND id2=?)")) {
            statement.setLong(1, tuple.getLeftMember());
            statement.setLong(2, tuple.getRightMember());

            statement.setLong(3, tuple.getRightMember());
            statement.setLong(4, tuple.getLeftMember());

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {

                String status = resultSet.getString("status");
                LocalDate date = resultSet.getDate("sending_date").toLocalDate();

                FriendshipRequest req = new FriendshipRequest(tuple.getLeftMember(), tuple.getRightMember());
                req.setStatus(Status.valueOf(status));
                req.setSendingDate(date);
                req.setId(tuple);
                return req;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public Iterable<FriendshipRequest> findAll() {
        Set<FriendshipRequest> requests = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM requests");

             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                String status = resultSet.getString("status");
                LocalDate date = resultSet.getDate("sending_date").toLocalDate();

                FriendshipRequest req = new FriendshipRequest(id1, id2);
                req.setId(new Tuple<Long, Long>(id1, id2));
                req.setStatus(Status.valueOf(status));
                req.setSendingDate(date);
                requests.add(req);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }


    @Override
    public FriendshipRequest save(FriendshipRequest entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity must not be null!");

        validator.validate(entity);

        /*if (this.findOne(entity.getId()) != null) {
            return this.findOne(entity.getId());
        }*/

        String sql = "insert into requests (id1, id2, status, sending_date) values (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, entity.getId().getLeftMember());
            ps.setLong(2, entity.getId().getRightMember());
            ps.setString(3, entity.getStatus().toString());
            ps.setDate(4, Date.valueOf(entity.getSendingDate()));

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public FriendshipRequest delete(Tuple<Long, Long> tuple) {
        if (tuple == null)
            throw new IllegalArgumentException("Id must not be null");

        FriendshipRequest deletedRequest = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM requests WHERE (id1 = ? AND id2 = ?) OR  (id1 = ? AND id2 = ?) RETURNING * ")) {
            statement.setLong(1, tuple.getLeftMember());
            statement.setLong(2, tuple.getRightMember());

            statement.setLong(3, tuple.getRightMember());
            statement.setLong(4, tuple.getLeftMember());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String status = resultSet.getString("status");
                LocalDate date = resultSet.getDate("sending_date").toLocalDate();

                deletedRequest = new FriendshipRequest(tuple.getLeftMember(), tuple.getRightMember());
                deletedRequest.setStatus(Status.valueOf(status));
                deletedRequest.setId(tuple);
                deletedRequest.setSendingDate(date);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deletedRequest;
    }


    @Override
    public FriendshipRequest update(FriendshipRequest entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity must not be null");

        validator.validate(entity);

        String sql = "update requests SET status = ? WHERE (id1 = ? AND id2 = ?) OR  (id1 = ? AND id2 = ?)";

        int row_count = 0;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {


            statement.setString(1, entity.getStatus().toString());
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
             PreparedStatement statement = connection.prepareStatement("DELETE FROM requests")) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getNumberOfEntites() {
        int size = 0;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from requests");
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

