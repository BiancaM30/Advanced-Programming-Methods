package com.example.toysocialnetworkgui;

import com.example.toysocialnetworkgui.domain.Message;
import com.example.toysocialnetworkgui.domain.User;
import com.example.toysocialnetworkgui.service.Service;
import com.example.toysocialnetworkgui.utils.events.ChangeEvent;
import com.example.toysocialnetworkgui.utils.observer.Observer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ChatGroupController implements Observer<ChangeEvent> {
    private Service service;
    private User loggedUser;
    private List<User> selectedUsers;
    List<Message> mesaje;

    ObservableList<User> modelSearch = FXCollections.observableArrayList();
    ObservableList<String> modelSearch2 = FXCollections.observableArrayList();


    @FXML
    private TextField textField;

    @FXML
    private TextField memberText;

    @FXML
    TableColumn<User, String> tableColumnName;

    @FXML
    TableColumn<String, String> tableColumnName2;

    @FXML
    private TableView<User> tableView;

    @FXML
    private TableView<String> tableView2;

    @FXML
    private AnchorPane chatAnchor;

    @FXML
    private ScrollPane chatScroll;


    public void setService(Service service) {
        this.service = service;
        service.addObserver(this);
        List<User> friends = service.getAllFriends(loggedUser.getId());
        modelSearch.setAll(friends);
        modelSearch2.setAll(service.getUsersFromGroupConversations(loggedUser.getId()));
        selectedUsers = new ArrayList<>();
        //initModel();
    }

    public void setUser(User user) {
        this.loggedUser = user;
    }

    private void handleFilter() {

        List<User> friends = service.getAllFriends(loggedUser.getId());
        modelSearch.setAll(friends
                .stream()
                //.filter(p)
                .collect(Collectors.toList()));
    }


    private void initModel() {

        chatScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        chatScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        List<Message> msg = service.displayGroupConversation(loggedUser.getId(), getIdSelectedUsers());
        mesaje = StreamSupport.stream(msg.spliterator(), false)
                .collect(Collectors.toList());
        Collections.reverse(mesaje);

        chatAnchor.getChildren().clear();
        double pos = 300;
        for (int i = 0; i < mesaje.size(); i++) {
            Message m = mesaje.get(i);
            Rectangle r = new Rectangle(150, 25, Paint.valueOf("#6B6B9F"));
            r.setArcWidth(20);
            r.setArcHeight(20);
            r.setLayoutY(pos - 2);
            Label l = new Label();
            l.setText(service.getUser(m.getFrom()).getFirstName() + ": " + m.getMessage());
            l.setLayoutY(pos);
            pos -= 35;
            if (m.getFrom().equals(loggedUser.getId())) {
                r.setLayoutX(75);
                l.setLayoutX(85);
                r.setStyle("-fx-fill: white");

            } else {
                r.setLayoutX(15);
                l.setLayoutX(25);
                l.setStyle("-fx-text-fill: white");
            }
            chatAnchor.getChildren().add(r);
            chatAnchor.getChildren().add(l);
        }


    }

    @FXML
    public void initialize() {
        tableColumnName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        tableView.setItems(modelSearch);

        tableColumnName2.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        tableView2.setItems(modelSearch2);
    }


    public void clickOnGroup(ActionEvent event) throws IOException {
        selectedUsers.add(tableView.getSelectionModel().getSelectedItem());
        String text = "";
        for (User user : selectedUsers) {
            text = text + user.getFirstName() + " " + user.getLastName() + ";";
        }
        memberText.setText(text);
    }

    public void clickOnFinish(ActionEvent event) throws IOException {
        initModel();
    }

    public void clickOnSelect(ActionEvent event) throws IOException {
        String members = tableView2.getSelectionModel().getSelectedItem();

        List<String> rez = new ArrayList<>();
        String[] aux = members.split(";");
        for (String elem : aux) {
            rez.add(elem);
        }

        for (String elem:rez)
        {
            User us=service.getUserByName(elem);
            selectedUsers.add(us);
        }
        //memberText.setText(selectedUsers.getFirstName() + " " + selectedUsers.getLastName());

        initModel();
    }

    public List<Long> getIdSelectedUsers() {
        List<Long> idUsers = new ArrayList<>();
        for (User user : selectedUsers) {
            idUsers.add(user.getId());
        }
        return idUsers;
    }

    public void clickOnSend(ActionEvent event) throws IOException {
        service.sendMessage(loggedUser.getId(), getIdSelectedUsers(), textField.getText());
        initModel();
        textField.setText("");
    }

    @Override
    public void update(ChangeEvent messagesChangeEvent) {

    }
}