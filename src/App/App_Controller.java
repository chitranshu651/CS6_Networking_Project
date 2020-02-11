package App;

import Login_Signup.User;
import Misc.SessionInfo;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import sample.Main;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class App_Controller {
    @FXML
    private JFXListView list;

    @FXML
    private JFXTextArea text;

    @FXML
    private Label name;

    @FXML
    private Label username;

    @FXML
    private JFXListView display;

    @FXML
    private JFXTextField search;


    public void initialize(){
        Main.user.sendString("GetUsers");
        ArrayList<User> users = (ArrayList)Main.user.recieveObject();
        ArrayList<String> add = new ArrayList<>();
        for(User s: users){
            if(!s.getUsername().equals(SessionInfo.getUsername())){
                add.add(s.getUsername());
            }
        }
        list.setItems(FXCollections.observableArrayList(add));
        list.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                username.setText(list.getSelectionModel().getSelectedItem().toString());
                Main.user.sendString("GetProfile");
                Main.user.sendString(username.getText());
                User profile = (User) Main.user.recieveObject();
                name.setText(profile.getFirst() + " " + profile.getLast());
                Main.user.sendString("GetMessages");
                Main.user.sendString(username.getText());
                Main.user.sendString(SessionInfo.getUsername());
                ArrayList<Message> messages = (ArrayList<Message>)Main.user.recieveObject();
                ArrayList<String> todisplay = new ArrayList<>();
                for (Message ms : messages) {
                    String text = "[" + ms.getTime() + "] " + ms.getSender() + " : " + ms.getText();
                    todisplay.add(text);
                }
                display.setItems(FXCollections.observableArrayList(todisplay));
            }
    });
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Runnable updater = new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                    }
                };
                while (true) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ex) {
                    }
                    Platform.runLater(updater);
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }


    @FXML
    void close(MouseEvent event) {
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.close();
    }

    @FXML
    void minimize(MouseEvent event) {
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.toBack();
    }

    @FXML
    void refresh() {
        if(username.getText().equals("username")){
            return;
        }
        Main.user.sendString("GetMessages");
        Main.user.sendString(Misc.SessionInfo.getUsername());
        Main.user.sendString(username.getText());
        ArrayList<Message> messages= (ArrayList<Message>) Main.user.recieveObject();
        ArrayList<String> todisplay = new ArrayList<>();
        for (Message ms : messages) {
            String text = "[" + ms.getTime() + "] " + ms.getSender() + " : " + ms.getText();
            todisplay.add(text);
        }
        display.setItems(FXCollections.observableArrayList(todisplay));
    }

    @FXML
    void search(KeyEvent event) {
        if (search.getText().equals("")) {

        } else {
            Main.user.sendString("Search");
            Main.user.sendString(search.getText());
            Main.user.sendString(SessionInfo.getUsername());
            ArrayList<User> searchResults = (ArrayList) Main.user.recieveObject();
            ArrayList a = new ArrayList();
            if (searchResults.size() != 0) {
               for (User s : searchResults) {
                   a.add(s.getUsername());
               }
            }
           list.setItems(FXCollections.observableArrayList(a));

        }
    }

    @FXML
    void send(ActionEvent event) {
        Message message = new Message();
        message.setReciever(username.getText());
        message.setSender(SessionInfo.getUsername());
        message.setType(0);
        message.setText(text.getText());
        message.setTime(new Timestamp(System.currentTimeMillis()));
        Main.user.sendString("SendMessage");
        try {
            Main.user.sendObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        text.setText("");
    }

}
