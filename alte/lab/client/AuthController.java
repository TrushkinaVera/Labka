package alte.lab.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import alte.lab.Pair;

import java.util.function.BiFunction;
import java.util.function.Function;

public class AuthController {
    @FXML private TextField login;
    @FXML private PasswordField password;

    @FXML private TextField mail;

    private BiFunction<String, String, Pair<String, String>> auth;
    private Function<String, Pair<String, String>> register;

    void setListeners(BiFunction<String, String, Pair<String, String>> auth,
                      Function<String, Pair<String, String>> register) {
        this.auth = auth;
        this.register = register;
    }

    @FXML protected void auth(ActionEvent event) {
        Pair<String, String> response = auth.apply(login.getText(), password.getText());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        System.out.println(response.getKey());
        System.out.println(response.getValue());
        alert.setTitle(response.getKey());
        alert.setContentText(response.getValue());
        alert.showAndWait();
    }
    @FXML protected void register(ActionEvent event) {
        register.apply(mail.getText());
    }
}
