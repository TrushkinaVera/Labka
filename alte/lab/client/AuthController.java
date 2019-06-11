package alte.lab.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AuthController {
    @FXML
    private TextField login;
    @FXML
    private PasswordField password;

    @FXML
    private TextField mail;

    private BiConsumer<String, String> auth_listener;
    private Consumer<String> register_listener;

    void setListeners(BiConsumer<String, String> auth,
                      Consumer<String> register) {
        this.auth_listener = auth;
        this.register_listener = register;
    }

    @FXML
    protected void auth(ActionEvent event) {
        auth_listener.accept(login.getText(), password.getText());
    }

    @FXML
    protected void register(ActionEvent event) {
        register_listener.accept(mail.getText());
    }
}
