package com.example.toysocialnetworkgui.repository.db;

import com.example.toysocialnetworkgui.domain.User;
import com.example.toysocialnetworkgui.repository.Repository;
import com.example.toysocialnetworkgui.domain.Event;
import com.example.toysocialnetworkgui.validators.Validator;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.function.LongToIntFunction;

public class EventDbRepository implements Repository<Long, Event> {
    private String url;
    private String username;
    private String password;
    private Validator<Event> validator;

    public EventDbRepository(String url, String username, String password, Validator<Event> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Event findOne(Long aLong) {

        Event event;

        try (Connection connection = DriverManager.getConnection(url, username, password)){
             PreparedStatement statement = connection.prepareStatement("SELECT * from events where id = ?");
            statement.setLong(1, aLong);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {

                Long id = resultSet.getLong("id");
                Long idorg = resultSet.getLong("organizer_id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                String location = resultSet.getString("location");
                LocalDate date = resultSet.getDate("date").toLocalDate();

                //luam userul din tabela users

                PreparedStatement statement2 = connection.prepareStatement("SELECT * from users where id = ?");
                statement2.setLong(1, idorg);
                ResultSet resultSet2 = statement2.executeQuery();
                while (resultSet2.next()) {

                    String firstName = resultSet2.getString("first_name");
                    String lastName = resultSet2.getString("last_name");
                    String gender = resultSet2.getString("gender");
                    LocalDate birthday = resultSet2.getDate("birthday").toLocalDate();
                    String locationUs = resultSet2.getString("location");
                    String email = resultSet2.getString("email");
                    String password = resultSet2.getString("password");

                    User organizer = new User(idorg, firstName, lastName, gender, birthday, locationUs, email, password);

                    event = new Event(organizer, name, description, location, date);
                    event.setId(id);
                    return event;
                }


            }

         }catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Event> findAll() {
        Set<Event> events = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from events");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                Long id = resultSet.getLong("id");
                Long idorg = resultSet.getLong("organizer_id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                String location = resultSet.getString("location");
                LocalDate date = resultSet.getDate("date").toLocalDate();

                //luam userul din tabela users

                PreparedStatement statement2 = connection.prepareStatement("SELECT * from users where id = ?");
                statement2.setLong(1, idorg);
                ResultSet resultSet2 = statement2.executeQuery();
                while (resultSet2.next()) {

                    String firstName = resultSet2.getString("first_name");
                    String lastName = resultSet2.getString("last_name");
                    String gender = resultSet2.getString("gender");
                    LocalDate birthday = resultSet2.getDate("birthday").toLocalDate();
                    String locationUs = resultSet2.getString("location");
                    String email = resultSet2.getString("email");
                    String password = resultSet2.getString("password");

                    User organizer = new User(idorg, firstName, lastName, gender, birthday, locationUs, email, password);

                    Event event = new Event(organizer, name, description, location, date);
                    event.setId(id);
                    events.add(event);
                }


            }
            return events;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    @Override
    public Event save(Event entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity must not be null!");

        validator.validate(entity);

        if (this.findOne(entity.getId()) != null) {
            return this.findOne(entity.getId());
        }

        String sql = "insert into events (id, organizer_id, name, description, location, date) values (?,?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, entity.getId());
            ps.setLong(2, entity.getOrganizer().getId());
            ps.setString(3, entity.getName());
            ps.setString(4, entity.getDescription());
            ps.setString(5, entity.getLocation());
            ps.setDate(6, Date.valueOf(entity.getDate()));
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Long nextId() {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT MAX(id) as max from events");
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
    public Event delete(Long aLong) {
        return null;
    }

    @Override
    public Event update(Event entity) {
        return null;
    }

    @Override
    public void clear() {
        int size = 0;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM events")) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getNumberOfEntites() {
        int size = 0;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from events");
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

