package com.example.toysocialnetworkgui.repository.db;

import com.example.toysocialnetworkgui.domain.User;
import com.example.toysocialnetworkgui.repository.Repository;
import com.example.toysocialnetworkgui.validators.Validator;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class UserDbRepository implements Repository<Long, User> {
    public String url;
    public String username;
    public String password;
    private Validator<User> validator;

    public UserDbRepository(String url, String username, String password, Validator<User> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public User findOne(Long aLong) {
        User user;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users where id = ?")) {
            statement.setLong(1, aLong);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String gender = resultSet.getString("gender");
                LocalDate birthday = resultSet.getDate("birthday").toLocalDate();
                String location = resultSet.getString("location");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");


                user = new User(firstName, lastName, gender, birthday, location, email, password);
                user.setId(aLong);
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<User> findAll() {
        Set<User> users = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String gender = resultSet.getString("gender");
                LocalDate birthday = resultSet.getDate("birthday").toLocalDate();
                String location = resultSet.getString("location");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");

                User User = new User(firstName, lastName, gender, birthday, location, email, password);
                User.setId(id);
                users.add(User);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public User save(User entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity must not be null!");

        validator.validate(entity);

        if (this.findOne(entity.getId()) != null) {
            return this.findOne(entity.getId());
        }

        String sql = "insert into users (id, first_name, last_name, gender, birthday, location, email, password ) values (?,?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, entity.getId());
            ps.setString(2, entity.getFirstName());
            ps.setString(3, entity.getLastName());
            ps.setString(4, entity.getGender());
            ps.setDate(5, Date.valueOf(entity.getBirthday()));
            ps.setString(6, entity.getLocation());
            ps.setString(7, entity.getEmail());
            ps.setString(8, entity.getPassword());

            ps.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Long nextId() {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT MAX(id) as max from users");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                return resultSet.getLong("max") + 1;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1L;
    }



    @Override
    public User delete(Long aLong) {
        if (aLong == null)
            throw new IllegalArgumentException("Id must not be null");

        User deletedUser = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE id = ? RETURNING * ")) {
            statement.setLong(1, aLong);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String gender = resultSet.getString("gender");
                LocalDate birthday = resultSet.getDate("birthday").toLocalDate();
                String location = resultSet.getString("location");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");

                deletedUser = new User(firstName, lastName, gender, birthday, location, email, password);
                deletedUser.setId(aLong);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deletedUser;
    }

    @Override
    public User update(User entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity must not be null");

        validator.validate(entity);

        String sql = "update users SET first_name = ?, last_name = ?, gender = ?, birthday = ?, location = ?, email = ?, password = ? where ID = ?";

        int row_count = 0;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(8, entity.getId());
            ps.setString(1, entity.getFirstName());
            ps.setString(2, entity.getLastName());
            ps.setString(3, entity.getGender());
            ps.setDate(4, Date.valueOf(entity.getBirthday()));
            ps.setString(5, entity.getLocation());
            ps.setString(6, entity.getEmail());
            ps.setString(7, entity.getPassword());

            row_count = ps.executeUpdate();
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
             PreparedStatement statement = connection.prepareStatement("DELETE FROM users")) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getNumberOfEntites() {
        int size = 0;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                size++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return size;
    }
}

