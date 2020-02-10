package Login_Signup;

import Misc.SceneChange;
import Misc.SessionInfo;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import sample.Main;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

public class Login {

    @FXML
    private JFXTextField user;
    @FXML
    private JFXPasswordField pass;
    private SceneChange changer = new SceneChange();

    @FXML
    public void initialize() {
        //Initialize the Textfields with Required Field Validator checks
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("Input Required");
        user.getValidators().add(validator);
        user.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal)
                user.validate();
        });
        user.getValidators().add(validator);
        user.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal)
                user.validate();
        });
    }


    @FXML
    private void Login(ActionEvent click) throws IOException, LineUnavailableException {
        //Send Data to Server
        String username = user.getText();
        String password = pass.getText();
        Main.user.sendString("Login");
        Main.user.sendString(username);
        Main.user.sendString(password);

        //Recieve if user exists or not
        boolean login = Main.user.recieveBoolean();
        if (login) {
            System.out.println("Login Successful");
            SessionInfo.setUsername(username);
            changer.changeScene("../App/App.fxml", click, "Hi");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Username Password didn't match");
            alert.showAndWait();
            System.out.println("Unsuccessful");
        }
    }

    @FXML
    private void Signup(MouseEvent click) {
        changer.changeScene("../Login_Signup/Signup.fxml", click, "Signup");
    }

    //Window Controls
    public void close(MouseEvent click) {
        Stage window = (Stage) ((Node) click.getSource()).getScene().getWindow();
        window.close();
    }

    public void minimize(MouseEvent click) {
        Stage window = (Stage) ((Node) click.getSource()).getScene().getWindow();
        window.toBack();
    }
}
