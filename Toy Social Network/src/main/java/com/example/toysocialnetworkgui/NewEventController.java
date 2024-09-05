package com.example.toysocialnetworkgui;

import com.example.toysocialnetworkgui.domain.*;
import com.example.toysocialnetworkgui.service.Service;
import com.example.toysocialnetworkgui.utils.events.ChangeEvent;
import com.example.toysocialnetworkgui.utils.observer.Observer;
import com.example.toysocialnetworkgui.validators.ServiceException;
import com.example.toysocialnetworkgui.validators.ValidationException;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class NewEventController {
    private Service service;
    private User loggedUser;
    private Stage stage;

    @FXML
    TextField txtFieldName;

    @FXML
    TextField txtFieldLocation;

    @FXML
    TextArea descriptionArea;

    @FXML
    TextArea detailsArea;

    @FXML
    DatePicker datePicker;


    public void setService(Service service) {
        this.service = service;
    }

    public void setUser(User user) {
        this.loggedUser = user;
    }


    public void clickOnCreate(ActionEvent event) throws IOException {
        String Name = txtFieldName.getText();
        String Description = descriptionArea.getText();
        String Location = txtFieldLocation.getText();
        LocalDate date = datePicker.getValue();

        try {
            service.addEvent(loggedUser, Name, Description, Location, date);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Event created!");
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

    public void clickOnBack(ActionEvent event) throws IOException {
        FXMLLoader eventLoader = new FXMLLoader();
        eventLoader.setLocation(getClass().getResource("views/account.fxml"));

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(eventLoader.load()));


        AccountController accountController = eventLoader.getController();
        accountController.setUser(loggedUser);
        accountController.setService(service);

        stage.setTitle("myzen");
        stage.setWidth(800);
        stage.setHeight(600);
        stage.setResizable(false);
        Image icon = new Image("file:src/mini_logo.png");
        stage.getIcons().add(icon);


        stage.show();
    }
}






