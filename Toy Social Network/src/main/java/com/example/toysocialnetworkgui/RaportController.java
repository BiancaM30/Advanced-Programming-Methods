package com.example.toysocialnetworkgui;

import com.example.toysocialnetworkgui.domain.Message;
import com.example.toysocialnetworkgui.domain.User;
import com.example.toysocialnetworkgui.service.Service;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;


import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static java.awt.Color.red;

public class RaportController {
    private Service service;
    private User loggedUser;

    @FXML
    DatePicker startPicker;

    @FXML
    DatePicker endPicker;


    @FXML
    TableColumn<User, String> tableColumnFirstName;

    @FXML
    TableColumn<User, String> tableColumnLastName;

    @FXML
    TableView<User> tableView;

    ObservableList<User> modelFriends = FXCollections.observableArrayList();

    private void initModel() {
        List<User> friends = service.getAllFriends(loggedUser.getId());
        modelFriends.setAll(friends);
    }

    public void setService(Service service) {
        this.service = service;
        initModel();
    }

    @FXML
    public void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));

        tableView.setItems(modelFriends);
    }


    public void setUser(User user) {
        this.loggedUser = user;
    }



    public void clickOnRaport1(ActionEvent event) throws IOException {

        PDDocument document=new PDDocument();
        PDPage page=new PDPage();
        document.addPage(page);

        PDPageContentStream contents = new PDPageContentStream(document, page);
        contents.beginText();
        PDFont font = PDType1Font.HELVETICA;
        contents.setLeading(30f);
        contents.setFont(font, 20);

        contents.newLineAtOffset(50, 700);

        contents.showText(loggedUser.getFirstName()+" "+loggedUser.getLastName());
        contents.newLine();
        contents.showText("Period:"+" from "+ " "+ startPicker.getValue().toString()+" to "+endPicker.getValue().toString());
        contents.newLine();
        contents.newLine();
        contents.newLine();
        contents.newLine();
        contents.showText("New friendships: ");
        contents.newLine();

        /*luam prietenii creati dintr-o anumita perioada calendaristica*/
        List<User> friends=service.getFriendsBetweenTwoDates(loggedUser.getId(),startPicker.getValue(),endPicker.getValue());

        for(User user: friends)
        {
            contents.showText(user.getFirstName()+" "+user.getLastName());
            contents.newLine();
        }
        /*luam mesajele intre doi utilizatori*/
        contents.newLine();
        contents.newLine();
        contents.newLine();
        contents.newLine();
        contents.showText("New messages: ");
        contents.newLine();
        List<Message> messages=service.getMessageBetweenTwoDates(loggedUser.getId(), startPicker.getValue(),endPicker.getValue());
        for(Message msg: messages)
        {

            contents.showText(service.getUser(msg.getFrom()).getFirstName()+": "+msg.getMessage()+"     "+msg.getData());
            contents.newLine();
        }
        contents.endText();
        contents.close();

        document.save("D:\\IdeaProjects\\Teme\\"+ "raport1.pdf");
        document.close();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Generated raport!");
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
                System.out.println("Pressed OK.");
            }
        });
    }

    public void clickOnRaport2(ActionEvent event) throws IOException {

        PDDocument document=new PDDocument();
        PDPage page=new PDPage();
        document.addPage(page);

        PDPageContentStream contents = new PDPageContentStream(document, page);
        contents.beginText();
        PDFont font = PDType1Font.HELVETICA;
        contents.setLeading(30f);
        contents.setFont(font, 20);

        contents.newLineAtOffset(50, 700);

        contents.showText(loggedUser.getFirstName()+" "+loggedUser.getLastName());
        contents.newLine();
        contents.showText("Period:"+" from "+ " "+ startPicker.getValue().toString()+" to "+endPicker.getValue().toString());
        contents.newLine();
        contents.newLine();
        contents.newLine();
        contents.newLine();
        contents.showText("New messages: ");
        contents.newLine();
        User selectedUser = tableView.getSelectionModel().getSelectedItem();
        List<Message> messages=service.getMessageFromUserBetweenTwoDates(loggedUser.getId(),selectedUser.getId(),startPicker.getValue(),endPicker.getValue());
        for(Message msg: messages)
        {
            contents.showText(service.getUser(msg.getFrom()).getFirstName()+": "+msg.getMessage()+"     "+msg.getData());
            contents.newLine();
        }
        contents.endText();
        contents.close();

        document.save("D:\\IdeaProjects\\Teme\\"+ "raport2.pdf");

        document.close();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Generated raport!");
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
                System.out.println("Pressed OK.");
            }
        });
    }


}
