package com.example.toysocialnetworkgui;

import com.google.common.hash.Hashing;
import com.example.toysocialnetworkgui.domain.User;
import com.example.toysocialnetworkgui.service.Service;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class LoginController {

    Service service;

    @FXML
    TextField emailTextField;

    @FXML
    TextField passwordTextField;

    private Stage stage;
    private Scene scene;
    private Parent root;

    public void setService(Service service) {
        this.service = service;
    }

    private User verifyCredetials(String email, String password) {
        return service.verifyCredentials(email, password);
    }

    public void login(ActionEvent event) throws IOException {
        String userEmail = emailTextField.getText();
        String userPassword = passwordTextField.getText();

        String hash = Hashing.sha256()
                .hashString(userPassword, StandardCharsets.UTF_8)
                .toString();

        if (verifyCredetials(userEmail, hash) == null) {
            emailTextField.setText("");
            passwordTextField.setText("");
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Invalid credentials");
            alert.setHeaderText("Invalid email or password!");
            alert.setContentText("Do you want to try again?");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        } else {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("views/account.fxml"));
            root = loader.load();

            AccountController accountController = loader.getController();

            User us = verifyCredetials(userEmail, hash);

            accountController.setUser(us);
            accountController.setService(service);

            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("Account");
            stage.setWidth(800);
            stage.setHeight(600);
            stage.setResizable(false);

            Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
            stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);

            Image icon = new Image("file:src/mini_logo.png");
            stage.getIcons().add(icon);
            stage.show();

        }
    }

        public void submit(ActionEvent event) throws IOException
        {
            //FXMLLoader loader = new FXMLLoader(getClass().getResource("views/submit.fxml"));
            //root = loader.load();

            FXMLLoader submitLoader=new FXMLLoader();
            submitLoader.setLocation(getClass().getResource("views/submit.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(submitLoader.load()));


            SubmitController submitController=submitLoader.getController();
            submitController.setService(service);

            stage.setTitle("Submit");
            stage.setWidth(670);
            stage.setHeight(440);
            stage.setResizable(false);
            Image icon = new Image("file:src/mini_logo.png");
            stage.getIcons().add(icon);


            stage.show();
        }
    }

