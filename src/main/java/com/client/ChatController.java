package com.client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import rumorsapp.BubbledLabel;
import tray.animations.AnimationType;
import tray.notification.TrayNotification;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;

public class ChatController {

    public BufferedReader in;

    @FXML private TextArea messageBox;
    @FXML public TextArea chatFlow;
    @FXML private Label usernameLabel;
    @FXML private Label onlineCountLabel;
    @FXML private ListView userList;
    @FXML private ImageView userImageView;
    @FXML
    VBox chatPane;

    ObservableList<String> items = FXCollections.observableArrayList ();

    public void sendButtonAction() {
        String msg = messageBox.getText();
        if(!messageBox.getText().isEmpty()) {
            Listener.send(msg);
            messageBox.setText("");
        }
    }

    public void addToChat(String msg) {

        Task<HBox> task = new Task<HBox>() {
            @Override
            public HBox call() throws Exception {
                Image image = new Image(getClass().getClassLoader().getResource("images/profile_circle.png").toString());
                ImageView profileImage = new ImageView(image);
                profileImage.setFitHeight(32);
                profileImage.setFitWidth(32);

                BubbledLabel bl6 = new BubbledLabel();

                bl6.setText(msg);
                bl6.setBackground(new Background(new BackgroundFill(Color.WHITE,
                        null, null)));
                HBox x = new HBox();
                x.getChildren().addAll(profileImage, bl6);

                System.out.println("hi");
                return x ;
            }
        };

        task.setOnSucceeded(event -> {
            chatPane.getChildren().add(task.getValue());
        });

        Thread t = new Thread(task);
        t.setDaemon(true); // thread will not prevent application shutdown
        t.start();

    }

    public void setUsernameLabel(String username) {
        this.usernameLabel.setText(username);
    }

    public void setImageLabel(String imageURL) throws IOException {
        this.userImageView.setImage(new Image(imageURL));
    }

    public void setOnlineLabel(String usercount) {
        Platform.runLater(() -> onlineCountLabel.setText(usercount));
    }

    public void setUserList(String userListnames) {
        Platform.runLater(() -> {
            String[] userlist = userListnames.split(",");
            Collections.addAll(items, userlist);
            userList.setItems(items);
            userList.setCellFactory(list -> new CellRenderer()
            );
            newUserNotification(userlist);
        });
    }

    private void newUserNotification(String[] userlist) {
        //Image profileImg = new Image(,50,50,false,false);
        TrayNotification tray = new TrayNotification();
        tray.setTitle("A new user has joined!");
        tray.setMessage(userlist[userlist.length-1] + " has joined the JavaFX Chatroom!");
        tray.setRectangleFill(Paint.valueOf("#2A9A84"));
        tray.setAnimationType(AnimationType.POPUP);
       // tray.setImage(profileImg);
        tray.showAndDismiss(Duration.seconds(5));
    }

    public void clearUserList() {
        Platform.runLater(() -> {
            userList.getItems().clear();
            items.removeAll();
            userList.getItems().removeAll();
            System.out.println("cleared lists");
        });
        }

    public void sendMethod(KeyEvent event){
        if (event.getCode() == KeyCode.ENTER) {
            sendButtonAction();
            messageBox.setText("");
        }
    }

    public void closeApplication(){
        System.exit(1);
    }



}
