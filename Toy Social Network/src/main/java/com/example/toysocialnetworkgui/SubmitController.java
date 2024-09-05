package com.example.toysocialnetworkgui;

import com.example.toysocialnetworkgui.service.Service;
import com.example.toysocialnetworkgui.validators.ServiceException;
import com.example.toysocialnetworkgui.validators.ValidationException;
import com.google.common.hash.Hashing;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

public class SubmitController {
    Service service;
    ObservableList<String> genderList = FXCollections.observableArrayList("man", "woman", "other");

    @FXML
    TextField FirstName;

    @FXML
    TextField LastName;

    @FXML
    TextField Location;

    @FXML
    DatePicker Birthday;

    @FXML
    ComboBox genderBox;

    @FXML
    TextField Email;

    @FXML
    PasswordField Password;

    @FXML
    Button submitButton;

    @FXML
    Button backButton;

    private Stage stage;
    private Scene scene;
    private Parent root;

    public void setService(Service service) {
        this.service = service;
    }

    public void initialize() {
        genderBox.setItems(genderList);
    }


    public void submitClicked(ActionEvent event) throws IOException {
        String firstName = FirstName.getText();
        String lastName = LastName.getText();
        String gender = genderBox.getValue().toString();
        LocalDate data = Birthday.getValue();
        String location = Location.getText();
        String userEmail = Email.getText();

        String userPassword = Password.getText();
        String hash = Hashing.sha256()
                .hashString(userPassword, StandardCharsets.UTF_8)
                .toString();

        try {
            service.addUser(firstName, lastName, gender, data, location, userEmail, hash);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Account created!");
            //alert.setHeaderText(ex.getMessage());
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        } catch (ValidationException | ServiceException ex) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Invalid data");
            alert.setHeaderText(ex.getMessage());
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        }
    }

    public void backToLogin(ActionEvent event) throws IOException
    {
        FXMLLoader loginLoader=new FXMLLoader();
        loginLoader.setLocation(getClass().getResource("views/login.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(loginLoader.load()));


        LoginController loginController=loginLoader.getController();
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

