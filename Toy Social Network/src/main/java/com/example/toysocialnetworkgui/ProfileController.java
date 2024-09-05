package com.example.toysocialnetworkgui;

import com.example.toysocialnetworkgui.domain.*;
import com.example.toysocialnetworkgui.service.Service;
import com.example.toysocialnetworkgui.utils.events.ChangeEvent;
import com.example.toysocialnetworkgui.validators.ServiceException;
import com.example.toysocialnetworkgui.validators.ValidationException;
import com.example.toysocialnetworkgui.utils.observer.Observer;
import com.example.toysocialnetworkgui.utils.observer.Observable;

import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;


import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class ProfileController implements Observer<ChangeEvent> {

    private Service service;
    private User loggedUser;
    ObservableList<RequestDTO> modelRequests = FXCollections.observableArrayList();
    ObservableList<Event> modelEvents = FXCollections.observableArrayList();


    @FXML
    TableColumn<RequestDTO, String> tableColumnFrom;

    @FXML
    TableColumn<RequestDTO, String> tableColumnLocation;

    @FXML
    TableColumn<RequestDTO, LocalDate> tableColumnDate;

    @FXML
    TableView<RequestDTO> tableView;



    @FXML
    private ListView<Event> listView;


    private List<RequestDTO> getRequestDTOList() {

        List<FriendshipRequest> requests = service.getAllUserRequests(loggedUser.getId());
        return requests
                .stream()
                .map(n -> new RequestDTO(n.getId().getLeftMember(),
                        (service.getUser(n.getId().getLeftMember())).getFirstName(),
                        (service.getUser(n.getId().getLeftMember())).getLastName(),
                        (service.getUser(n.getId().getLeftMember())).getLocation(),
                        n.getSendingDate()))
                .collect(Collectors.toList());
    }

    public void setService(Service service) {
        this.service = service;
        service.addObserver(this);
        modelRequests.setAll(getRequestDTOList());
        modelEvents.setAll(service.getAllNextEvents(loggedUser.getId()));
    }


    public void setUser(User user) {
        this.loggedUser = user;
    }

    @FXML
    public void initialize() {


        tableColumnFrom.setCellValueFactory(cellData -> Bindings.createStringBinding(
                () -> cellData.getValue().getFirstName() + " " + cellData.getValue().getLastName()
        ));
        tableColumnLocation.setCellValueFactory(new PropertyValueFactory<RequestDTO, String>("location"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<RequestDTO, LocalDate>("SendingDate"));

        tableView.setItems(modelRequests);


        listView.setCellFactory(list -> new ListCell<Event>() {
            @Override
            protected void updateItem(Event item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getName() + "  " + item.getDate());
                }
            }
        });

        listView.setItems(modelEvents);

    }

    public void clickOnAccept(ActionEvent event) throws IOException {
        RequestDTO selectedUser = tableView.getSelectionModel().getSelectedItem();

        try {
            service.acceptRequest(loggedUser.getId(), selectedUser.getIdFrom());
            modelRequests.setAll(getRequestDTOList());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Request accepted!");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });

        } catch (ValidationException | ServiceException | IllegalArgumentException ex) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Error accepting request");
            alert.setHeaderText(ex.getMessage());
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        }
    }

    public void clickOnDecline(ActionEvent event) throws IOException {
        RequestDTO selectedUser = tableView.getSelectionModel().getSelectedItem();

        try {
            service.rejectRequest(loggedUser.getId(), selectedUser.getIdFrom());
            modelRequests.setAll(getRequestDTOList());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Request declined!");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        } catch (ValidationException | ServiceException | IllegalArgumentException ex) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Error rejecting request");
            alert.setHeaderText(ex.getMessage());
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        }
    }

    @Override
    public void update(ChangeEvent changeEvent) {
        modelRequests.setAll(getRequestDTOList());
        tableView.setItems(modelRequests);
        modelEvents.setAll(service.getAllNextEvents(loggedUser.getId()));
    }


}






