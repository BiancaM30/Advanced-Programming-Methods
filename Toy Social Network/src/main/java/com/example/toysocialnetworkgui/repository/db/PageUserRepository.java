package com.example.toysocialnetworkgui.repository.db;

import com.example.toysocialnetworkgui.domain.Friendship;
import com.example.toysocialnetworkgui.domain.Tuple;
import com.example.toysocialnetworkgui.domain.User;
import com.example.toysocialnetworkgui.repository.paging.Page;
import com.example.toysocialnetworkgui.repository.paging.Pageable;
import com.example.toysocialnetworkgui.repository.paging.Paginator;
import com.example.toysocialnetworkgui.repository.paging.PagingRepository;
import com.example.toysocialnetworkgui.validators.ServiceException;
import com.example.toysocialnetworkgui.validators.Validator;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PageUserRepository extends UserDbRepository implements PagingRepository<Long, User> {

    public PageUserRepository(String url, String username, String password, Validator<User> validator) {
        super(url, username, password, validator);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        Paginator<User> paginator = new Paginator<>(pageable, findAll());
        return paginator.paginate();
    }

    private List<User> findAllFriends(Long aLong){
        List<User> friends = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships where id1 = ?")){

            statement.setLong(1, aLong);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id_friend = resultSet.getLong("id2");
                friends.add(findOne(id_friend));
            }
        }

        catch (SQLException e) {
            e.printStackTrace();
        }

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships where id2 = ?")){

             statement.setLong(1, aLong);

             ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id_friend = resultSet.getLong("id1");
                friends.add(findOne(id_friend));
                }
            }

       catch (SQLException e) {
            e.printStackTrace();
        }


        return friends;
    }

    public Page<User> findAllFriends(Pageable pageable, Long id_user) {

        Paginator<User> paginator = new Paginator<>(pageable, findAllFriends(id_user));
        return paginator.paginate();
    }

}



