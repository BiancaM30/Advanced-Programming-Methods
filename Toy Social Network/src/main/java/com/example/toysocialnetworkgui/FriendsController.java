package com.example.toysocialnetworkgui;

import com.example.toysocialnetworkgui.domain.User;
import com.example.toysocialnetworkgui.repository.paging.Page;
import com.example.toysocialnetworkgui.service.Service;
import com.example.toysocialnetworkgui.utils.events.ChangeEvent;
import com.example.toysocialnetworkgui.utils.observer.Observer;
import com.example.toysocialnetworkgui.validators.ServiceException;
import com.example.toysocialnetworkgui.validators.ValidationException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class FriendsController implements Observer<ChangeEvent> {


    private Service service;
    private User loggedUser;

    int from=0,to=0;
    ObservableList<User> modelFriends = FXCollections.observableArrayList();

    @FXML
    AnchorPane anchor1;

    @FXML
    TextField searchFriend;

    @FXML
    TableColumn<User, String> tableColumnFirstName;

    @FXML
    TableColumn<User, String> tableColumnLastName;

    @FXML
    TableColumn<User, String> tableColumnLocation;

    @FXML
    TableView<User> tableView;

    @FXML
    Pagination page;


    private void initModel() {
        page.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) ->
                pageHandler());
        Page<User> friendsPaginated = service.getAllFriendsPaginated(page.getCurrentPageIndex(), loggedUser.getId());
        modelFriends.setAll(friendsPaginated.getContent().collect(Collectors.toList()));
    }

    public void pageHandler() {
        initModel();
        tableView.setItems(modelFriends);

    }
    public void setService(Service service) {
        this.service = service;
        service.addObserver(this);
        initModel();

    }

    public void setUser(User user) {
        this.loggedUser = user;
    }


    private void handleFilter() {
        Predicate<User> p = x -> x.getFirstName().startsWith(searchFriend.getText());

        if (searchFriend.getText() == "") initModel();
        else {
            List<User> friends = service.getAllFriends(loggedUser.getId());
            List<User> filtered = friends
                    .stream()
                    .filter(p)
                    .collect(Collectors.toList());
            modelFriends.setAll(filtered);
        }

    }


    @FXML
    public void initialize() {

        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        tableColumnLocation.setCellValueFactory(new PropertyValueFactory<User, String>("location"));

        tableView.setItems(modelFriends);
        searchFriend.textProperty().addListener(o -> handleFilter());

    }

    public void clickedOnDelete(ActionEvent event) throws IOException {
        User selectedUser = tableView.getSelectionModel().getSelectedItem();

        try {
            service.removeFriendship(loggedUser.getId(), selectedUser.getId());
            initModel();
            tableView.setItems(modelFriends);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Friend removed!");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }

            });
        } catch (ServiceException ex) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Error removing friend!");
            alert.setHeaderText(ex.getMessage());
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        }

    }

    @Override
    public void update(ChangeEvent friendshipChangeEvent) {
        initModel();
        tableView.setItems(modelFriends);
    }
}







