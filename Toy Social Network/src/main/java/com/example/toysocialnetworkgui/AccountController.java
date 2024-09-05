package com.example.toysocialnetworkgui;

import com.example.toysocialnetworkgui.domain.User;
import com.example.toysocialnetworkgui.service.Service;
import com.example.toysocialnetworkgui.utils.events.ChangeEvent;
import com.example.toysocialnetworkgui.utils.observer.Observer;
import com.example.toysocialnetworkgui.validators.ValidationException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Collection;

public class AccountController implements Observer<ChangeEvent> {

    Service service;
    User loggedUser;

    @FXML
    AnchorPane mainAnchor;

    @FXML
    AnchorPane anchorLeft;

    @FXML
    AnchorPane anchorRight;

    @FXML
    private Label nameLabel;

    @FXML
    private Label locationLabel;

    @FXML
    private Label birthdayLabel;

    @FXML
    private Button NameButton;

    Stage stage;
    Scene scene;
    private Parent root;

    public void setService(Service service) throws IOException{
        this.service = service;
        this.service.addObserver(this);

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("views/profile.fxml"));
        anchorRight.getChildren().setAll((Node) fxmlLoader.load());

        ProfileController profileController = fxmlLoader.getController();
        profileController.setUser(loggedUser);
        profileController.setService(service);

        NameButton.setText(loggedUser.getFirstName() + " " +loggedUser.getLastName());

    }

    public void setUser(User user) {
        this.loggedUser = user;
    }

    public void clickOnSearch(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("views/search.fxml"));
        anchorRight.getChildren().setAll((Node) fxmlLoader.load());

        SearchController searchController = fxmlLoader.getController();
        searchController.setUser(loggedUser);
        searchController.setService(service);


    }

    public void clickOnFriends(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("views/friends.fxml"));
        anchorRight.getChildren().setAll((Node) fxmlLoader.load());

        FriendsController friendsController = fxmlLoader.getController();
        friendsController.setUser(loggedUser);
        friendsController.setService(service);

    }

    public void clickOnProfile(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("views/profile.fxml"));
        anchorRight.getChildren().setAll((Node) fxmlLoader.load());

        ProfileController profileController = fxmlLoader.getController();
        profileController.setUser(loggedUser);
        profileController.setService(service);


    }

    public void clickOnEvent(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("views/events.fxml"));
        anchorRight.getChildren().setAll((Node) fxmlLoader.load());

        EventsController eventsController = fxmlLoader.getController();
        eventsController.setUser(loggedUser);
        eventsController.setService(service);


    }

    public void clickOnLogout(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("You're about to logout!");
        alert.setContentText("Do you want to continue?");
        if (alert.showAndWait().get() == ButtonType.OK) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("views/login.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            LoginController loginController = loader.getController();
            loginController.setService(service);

            stage.setTitle("myzen");
            stage.setWidth(670);
            stage.setHeight(416);
            stage.setResizable(false);
            Image icon = new Image("file:src/mini_logo.png");
            stage.getIcons().add(icon);


            stage.show();
        }

    }


    @Override
    public void update(ChangeEvent changeEvent) {

    }

    public void clickOnChat(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("views/chat.fxml"));
        anchorRight.getChildren().setAll((Node) fxmlLoader.load());

        ChatController chatController = fxmlLoader.getController();
        chatController.setUser(loggedUser);
        chatController.setService(service);
    }

    public void clickOnChatGroup(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("views/chatGroup.fxml"));
        anchorRight.getChildren().setAll((Node) fxmlLoader.load());

        ChatGroupController chatController = fxmlLoader.getController();
        chatController.setUser(loggedUser);
        chatController.setService(service);
    }

    public void clickOnGeneratorPDF(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("views/raport.fxml"));
        anchorRight.getChildren().setAll((Node) fxmlLoader.load());

        RaportController raportController = fxmlLoader.getController();
        raportController.setUser(loggedUser);
        raportController.setService(service);
    }
}

