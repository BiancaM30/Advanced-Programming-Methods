package com.example.toysocialnetworkgui;

import com.example.toysocialnetworkgui.domain.Message;
import com.example.toysocialnetworkgui.domain.User;
import com.example.toysocialnetworkgui.service.Service;
import com.example.toysocialnetworkgui.utils.events.ChangeEvent;
import com.example.toysocialnetworkgui.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ChatController implements Observer<ChangeEvent> {
    private Service service;
    private User loggedUser;
    private User selectedUser;
    List<Message> mesaje;

    ObservableList<User> modelSearch = FXCollections.observableArrayList();
    ObservableList<User> modelSearch2 = FXCollections.observableArrayList();

    private long nr = 0;


    Long selectedLabel;

    @FXML
    private TextField textField;

    @FXML
    private TextField memberText;

    @FXML
    TableColumn<User, String> tableColumnName;

    @FXML
    TableColumn<User, String> tableColumnName2;

    @FXML
    private TableView<User> tableView;

    @FXML
    private TableView<User> tableView2;

    @FXML
    private AnchorPane chatAnchor;

    @FXML
    private ScrollPane chatScroll;

    public void setService(Service service) {
        this.service = service;
        service.addObserver(this);
        List<User> friends = service.getAllFriends(loggedUser.getId());
        modelSearch.setAll(friends);
        modelSearch2.setAll(service.getUserConversations(loggedUser.getId()));
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

        List<Message> msg = service.displayConversation(loggedUser.getId(), selectedUser.getId());
        mesaje = StreamSupport.stream(msg.spliterator(), false)
                .collect(Collectors.toList());
        Collections.reverse(mesaje);

        chatAnchor.getChildren().clear();
        double pos = 300;
        for (int i = 0; i < mesaje.size(); i++) {
            Message m = mesaje.get(i);
            Rectangle r = new Rectangle(150, 25, Paint.valueOf("#6B6B9F"));
            r.setArcWidth(25);
            r.setArcHeight(20);
            r.setLayoutY(pos - 2);
            Label l = new Label();
            l.setOnMouseClicked((click) -> selectedLabel=m.getId());
            if(m.getIdReply()!=0)
            {
                r.setHeight(35);
                l.setText("Reply at: "+ service.findIdReply(m.getIdReply()).getMessage()+"\n"+m.getMessage());

            }
            else
            {
                l.setText(m.getMessage());

            }

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

        tableColumnName2.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        tableView2.setItems(modelSearch2);
    }


    public void clickOnSimple(ActionEvent event) throws IOException {
        selectedUser = null;
        selectedUser = tableView.getSelectionModel().getSelectedItem();
        memberText.setText(selectedUser.getFirstName() + " " + selectedUser.getLastName());

        initModel();
    }

    public void clickOnSelect(ActionEvent event) throws IOException {
        selectedUser = null;
        selectedUser = tableView2.getSelectionModel().getSelectedItem();
        memberText.setText(selectedUser.getFirstName() + " " + selectedUser.getLastName());

        initModel();
    }

    public void clickOnSend(ActionEvent event) throws IOException {
        service.sendMessage(loggedUser.getId(), Collections.singletonList(selectedUser.getId()), textField.getText());

        textField.setText("");
        initModel();
    }



    public void clickOnReply(ActionEvent event) throws IOException {

        service.sendReply(loggedUser.getId(),selectedLabel, textField.getText());
        textField.setText("");
        initModel();
    }


    @Override
    public void update(ChangeEvent messagesChangeEvent) {
        modelSearch2.setAll(service.getUserConversations(loggedUser.getId()));
        tableView2.setItems(modelSearch2);
        initModel();
    }
}