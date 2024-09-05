package com.example.toysocialnetworkgui;

import com.example.toysocialnetworkgui.domain.*;
import com.example.toysocialnetworkgui.repository.Repository;
import com.example.toysocialnetworkgui.repository.db.*;
import com.example.toysocialnetworkgui.service.Service;
import com.example.toysocialnetworkgui.validators.*;
import com.google.common.hash.Hashing;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

public class HelloApplication extends Application {
    PageUserRepository userRepo;
    Repository<Tuple<Long, Long>, Friendship> friendshipRepo;
    Repository<Long, Message> messRepo;
    Repository<Tuple<Long, Long>, FriendshipRequest> requestRepo;
    Repository<Long, Event> eventRepo;
    Repository<Tuple<Long, Long>, Notification> notificationRepo;

    Service service;

    @Override
    public void start(Stage stage) throws IOException {
        userRepo=new PageUserRepository("jdbc:postgresql://localhost:5432/toynetworklab4", "postgres", "postgresbianca", new UserValidator());
        friendshipRepo = new FriendshipDbRepository("jdbc:postgresql://localhost:5432/toynetworklab4", "postgres", "postgresbianca", new FriendshipValidator());
        messRepo = new MessageDbRepository("jdbc:postgresql://localhost:5432/toynetworklab4", "postgres", "postgresbianca", new MessageValidator());
        requestRepo = new RequestDbRepository("jdbc:postgresql://localhost:5432/toynetworklab4", "postgres", "postgresbianca", new RequestValidator());
        eventRepo = new EventDbRepository("jdbc:postgresql://localhost:5432/toynetworklab4", "postgres", "postgresbianca", new EventValidator());
        notificationRepo = new NotificationDbRepository("jdbc:postgresql://localhost:5432/toynetworklab4", "postgres", "postgresbianca", new NotificationValidator());
        service = new Service(userRepo, friendshipRepo, messRepo, requestRepo, eventRepo,notificationRepo);

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("views/login.fxml"));
        stage.setScene(new Scene(fxmlLoader.load()));

        LoginController loginController = fxmlLoader.getController();
        loginController.setService(service);

        stage.setTitle("myzen");
        stage.setWidth(670);
        stage.setHeight(416);
        stage.setResizable(false);
        Image icon = new Image("file:src/mini_logo.png");
        stage.getIcons().add(icon);


        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}