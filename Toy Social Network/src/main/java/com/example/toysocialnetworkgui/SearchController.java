package com.example.toysocialnetworkgui;

import com.example.toysocialnetworkgui.domain.Friendship;
import com.example.toysocialnetworkgui.domain.FriendshipRequest;
import com.example.toysocialnetworkgui.domain.User;
import com.example.toysocialnetworkgui.domain.UsersRequestDTO;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SearchController implements Observer<ChangeEvent> {
    private Service service;
    private User loggedUser;

    ObservableList<UsersRequestDTO> modelSearch = FXCollections.observableArrayList();

    @FXML
    TextField testField;

    @FXML
    TextField searchField;

    @FXML
    TableColumn<UsersRequestDTO, String> tableColumnFirstName;

    @FXML
    TableColumn<UsersRequestDTO, String> tableColumnLastName;

    @FXML
    TableColumn<UsersRequestDTO, String> tableColumnLocation;

    @FXML
    TableColumn<UsersRequestDTO, String> tableColumnStatus;

    @FXML
    TableView<UsersRequestDTO> tableView;

    /*private List<User> getAllUsers(){
        Iterable<User> itrUsers = service.getAllUsers();
        List<User> users = new ArrayList<>();
        itrUsers.forEach(users::add);

        return users;
    }*/
    private List<UsersRequestDTO> getAllUsers() {
        Iterable<User> itrUsers = service.getAllUsers();
        List<User> users = new ArrayList<>();
        itrUsers.forEach(users::add);

        List<UsersRequestDTO> usersReq = new ArrayList<>();
        for(User us: users){
            //verify if the logged user and the current user are friends/have a pending request/rejected request/no request
            String status = service.getTwoUsersRequestStatus(this.loggedUser.getId(), us.getId());
            usersReq.add(new UsersRequestDTO(this.loggedUser.getId(), us.getId(), us.getFirstName(), us.getLastName(), us.getLocation(), status));
        }
        return usersReq;
    }

    public void setService(Service service) {
        this.service = service;
        service.addObserver(this);
        modelSearch.setAll(getAllUsers());
    }

    public void setUser(User user) {
        this.loggedUser = user;
    }

    private void handleFilter() {
        Predicate<UsersRequestDTO> p = x -> x.getFirstName2().startsWith(searchField.getText());
        List<UsersRequestDTO> users = getAllUsers();

        modelSearch.setAll(users
                .stream()
                .filter(p)
                .collect(Collectors.toList()));
    }

    @FXML
    public void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<UsersRequestDTO, String>("FirstName2"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<UsersRequestDTO, String>("LastName2"));
        tableColumnLocation.setCellValueFactory(new PropertyValueFactory<UsersRequestDTO, String>("Location2"));
        tableColumnStatus.setCellValueFactory(new PropertyValueFactory<UsersRequestDTO, String>("status"));

        tableView.setItems(modelSearch);
        searchField.textProperty().addListener(o -> handleFilter());

    }

    public void clickOnSendRequest(ActionEvent event) throws IOException {
        UsersRequestDTO selectedUser = tableView.getSelectionModel().getSelectedItem();

        try {
            service.sendRequest(loggedUser.getId(), selectedUser.getId2());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Request successfully sent!");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        } catch (ValidationException | ServiceException | IllegalArgumentException ex) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Error sending request");
            alert.setHeaderText(ex.getMessage());
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        }
    }

    public void clickOnUnsendRequest(ActionEvent event) throws IOException {
        UsersRequestDTO selectedUser = tableView.getSelectionModel().getSelectedItem();

        try {
            service.unsendRequest(loggedUser.getId(), selectedUser.getId2());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Request successfully deleted!");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        } catch (ValidationException | ServiceException | IllegalArgumentException ex) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Error unsending request");
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
        modelSearch.setAll(getAllUsers());
        tableView.setItems(modelSearch);

    }


}






