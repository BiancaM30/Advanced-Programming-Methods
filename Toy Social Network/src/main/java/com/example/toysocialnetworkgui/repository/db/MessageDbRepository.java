package com.example.toysocialnetworkgui.repository.db;

import com.example.toysocialnetworkgui.domain.Message;
import com.example.toysocialnetworkgui.domain.Tuple;
import com.example.toysocialnetworkgui.domain.User;
import com.example.toysocialnetworkgui.repository.Repository;
import com.example.toysocialnetworkgui.validators.Validator;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MessageDbRepository implements Repository<Long, Message> {
    private String url;
    private String username;
    private String password;
    private Validator<Message> validator;

    public MessageDbRepository(String url, String username, String password, Validator<Message> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Message findOne(Long aLong) {
        User user;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from messages where id = ?")) {
            statement.setLong(1, aLong);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long idUser = resultSet.getLong("id_user");
                String recipientList = resultSet.getString("recipient_list");
                String mess = resultSet.getString("message");
                LocalDateTime data = resultSet.getTimestamp("data").toLocalDateTime();
                Long idReply = resultSet.getLong("id_reply");

                List<Long> rez = new ArrayList<>();
                String[] aux = recipientList.split(";");
                for (String elem : aux) {
                    rez.add(Long.valueOf(elem));
                }

                Message message = new Message(idUser, rez, mess, data);
                message.setId(aLong);
                message.setIdReply(idReply);
                return message;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Message> findAll() {
        Set<Message> messages = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from messages");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long idUser = resultSet.getLong("id_user");
                String recipientList = resultSet.getString("recipient_list");
                String mess = resultSet.getString("message");
                LocalDateTime data = resultSet.getTimestamp("data").toLocalDateTime();
                Long idReply = resultSet.getLong("id_reply");

                List<Long> rez = new ArrayList<>();
                String[] aux = recipientList.split(";");
                for (String elem : aux) {
                    rez.add(Long.valueOf(elem));
                }

                Message message = new Message(idUser, rez, mess, data);
                message.setId(id);
                message.setIdReply(idReply);
                messages.add(message);
            }
            return messages;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    @Override
    public Message delete(Long aLong) {
        if (aLong == null)
            throw new IllegalArgumentException("Id must not be null");

        Message deletedMessage = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM messages WHERE id = ? RETURNING * ")) {
            statement.setLong(1, aLong);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long idUser = resultSet.getLong("id_user");
                String recipientList = resultSet.getString("recipient_list");
                String mess = resultSet.getString("message");
                LocalDateTime data = resultSet.getTimestamp("data").toLocalDateTime();
                Long idReply = resultSet.getLong("id_reply");

                List<Long> rez = new ArrayList<>();
                String[] aux = recipientList.split(";");
                for (String elem : aux) {
                    rez.add(Long.valueOf(elem));
                }

                deletedMessage = new Message(idUser, rez, mess, data);
                deletedMessage.setId(aLong);
                deletedMessage.setIdReply(idReply);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deletedMessage;
    }

    @Override
    public Message update(Message entity) {
        return null;
    }

    @Override
    public Message save(Message entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity must not be null!");

        validator.validate(entity);

        if (this.findOne(entity.getId()) != null) {
            return this.findOne(entity.getId());
        }

        String sql = "insert into messages (id, id_user, recipient_list, message, data, id_reply) values (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            String rez = entity.getTo().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(";"));

            ps.setLong(1, entity.getId());
            ps.setLong(2, entity.getFrom());
            ps.setString(3, rez);
            ps.setString(4, entity.getMessage());
            ps.setTimestamp(5, Timestamp.valueOf(entity.getData()));

            try {
                ps.setLong(6, entity.getIdReply());
            }
            catch (Exception ex) {
                ps.setNull(6, java.sql.Types.INTEGER);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void clear() {
        int size = 0;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM messages")) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getNumberOfEntites() {
        int size = 0;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from messages");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                size++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return size;
    }

    public Long nextId() {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT MAX(id) as max from messages");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                return resultSet.getLong("max") + 1;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1L;
    }



}
