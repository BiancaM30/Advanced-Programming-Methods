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

public class EventsController implements Observer<ChangeEvent> {
    private Service service;
    private User loggedUser;

    private Stage stage;

    @FXML
    TextArea detailsArea;

    @FXML
    TableColumn<Event, String> tableColumnName;

    @FXML
    TableColumn<Event, String> tableColumnLocation;

    @FXML
    TableColumn<Event, LocalDate> tableColumnDate;

    @FXML
    TableView<Event> tableView;

    @FXML
    TableColumn<Event, String> tableColumnName2;

    @FXML
    TableColumn<Event, String> tableColumnLocation2;

    @FXML
    TableColumn<Event, LocalDate> tableColumnDate2;

    @FXML
    TableView<Event> tableView2;


    ObservableList<Event> modelEvents = FXCollections.observableArrayList();
    ObservableList<Event> modelEvents2 = FXCollections.observableArrayList();

    @FXML
    TextField searchField;


    public void setService(Service service) {
        this.service = service;
        service.addObserver(this);
        initModel();
    }

    public void setUser(User user) {
        this.loggedUser = user;
    }

    private void initModel() {
        modelEvents.setAll(service.getAllEvents());
        modelEvents2.setAll(service.getAllSubscribedEvents(loggedUser.getId()));

    }

    private void handleFilter() {
        Predicate<Event> p = x -> x.getName().startsWith(searchField.getText());

        List<Event> events = service.getAllEvents();
        modelEvents.setAll(events
                .stream()
                .filter(p)
                .collect(Collectors.toList()));
    }

    @FXML
    public void initialize() {
        tableColumnName.setCellValueFactory(new PropertyValueFactory<Event, String>("name"));
        tableColumnLocation.setCellValueFactory(new PropertyValueFactory<Event, String>("location"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<Event, LocalDate>("date"));

        tableView.setItems(modelEvents);
        searchField.textProperty().addListener(o -> handleFilter());

        tableColumnName2.setCellValueFactory(new PropertyValueFactory<Event, String>("name"));
        tableColumnLocation2.setCellValueFactory(new PropertyValueFactory<Event, String>("location"));
        tableColumnDate2.setCellValueFactory(new PropertyValueFactory<Event, LocalDate>("date"));

        tableView2.setItems(modelEvents2);
    }

    public void clickOnCreateEvent(ActionEvent event) throws IOException {
        {
            FXMLLoader eventLoader=new FXMLLoader();
            eventLoader.setLocation(getClass().getResource("views/newEvent.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(eventLoader.load()));


            NewEventController eventController=eventLoader.getController();
            eventController.setUser(loggedUser);
            eventController.setService(service);


            stage.setTitle("Create new event");
            stage.setWidth(400);
            stage.setHeight(500);
            stage.setResizable(false);
            Image icon = new Image("file:src/mini_logo.png");
            stage.getIcons().add(icon);


            stage.show();
        }
    }

    @Override
    public void update(ChangeEvent eventChangeEvent) {
        initModel();
        tableView.setItems(modelEvents);
        tableView2.setItems(modelEvents2);
    }

    public void clickOnDetails(ActionEvent event) throws IOException {

        Event selectedEvent = tableView.getSelectionModel().getSelectedItem();
        String description = "Descriere eveniment: " + selectedEvent.getDescription() + '\n';
        String organizer = "Eveniment organizat de: " + selectedEvent.getOrganizer().getFirstName() + " " + selectedEvent.getOrganizer().getLastName() + '\n';
        detailsArea.setText(organizer + description);
    }

    public void clickOnSubscribe(ActionEvent event) throws IOException {
        try {
            Event selectedEvent = tableView.getSelectionModel().getSelectedItem();
            service.subscribeNotifications(loggedUser.getId(), selectedEvent.getId());

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Subscribed!");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        } catch ( ServiceException ex) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Invalid");
            alert.setHeaderText(ex.getMessage());
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        }

    }

    public void clickOnUnsubscribe(ActionEvent event) throws IOException {
        try {
            Event selectedEvent = tableView.getSelectionModel().getSelectedItem();
            service.unsubscribeNotifications(loggedUser.getId(), selectedEvent.getId());

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Unubscribed");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        } catch ( ServiceException ex) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Invalid");
            alert.setHeaderText(ex.getMessage());
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        }
    }
}






