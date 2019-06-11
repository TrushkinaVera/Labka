package alte.lab.client;

import alte.lab.Command;
import alte.lab.Pair;
import alte.lab.User;
import alte.lab.connection.Header;
import alte.lab.connection.Packet;
import alte.lab.connection.ResponseCode;
import alte.lab.localization.Localization;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import static alte.lab.connection.ResponseCode.OK;

public class ClientMain extends Application {
    public static String hostname = "uriy.yuran.us";
    public static int port = 54105;
    public static User auth = null;
    public static Localization localization;
    public static boolean reconnected = false;
    public static Socket connection;
    public static ObjectOutputStream out;
    public static MainController mainFrame = null;

    public static void alert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {

        Application.launch(args);
    }

    /*
    if(cmd != null) {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        MainController controller = loader.getController();
        controller.drawCanvas();

        stage.setWidth(750);
        stage.setHeight(750);
        stage.setScene(scene);
        stage.setTitle(localization.getString("main_window"));
        stage.show();


        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        System.out.println(response.getKey());
        System.out.println(response.getValue());
        alert.setTitle(response.getKey());
        alert.setContentText(response.getValue());
        alert.showAndWait();
    }*/

    @Override
    public void start(Stage stage) {

        Socket connection;
        localization = new Localization();
        try {
            connection = new Socket(hostname, port);
            System.out.println(localization.getString("connecting"));

            out = new ObjectOutputStream(connection.getOutputStream());
            new Thread(new ServerListner(connection, hostname, port, input -> {

                switch(input.getCommand().getText()) {
                    case "login":

                        try {
                            if (input.getReponseCode() == OK) {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
                                Parent root = loader.load();
                                Scene scene = new Scene(root);

                                MainController controller = loader.getController();
                                controller.drawCanvas();

                                stage.setWidth(750);
                                stage.setHeight(750);
                                stage.setScene(scene);
                                stage.setTitle(localization.getString("main_window"));
                                stage.show();
                            } else {

                            }
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }

                        break;
                    case "register":
                        break;
                    case "show":
                        break;
                }

                if(mainFrame == null) return;

                ResponseCode code = input.getReponseCode();
                switch (code) {
                    case OK:
                        String data = input.getStringResponse();
                        mainFrame.addLogs(data);
                        break;
                    case UNATHORIZED:
                    case BAD_REQUEST:
                        mainFrame.addLogs(code.getMessage(localization));
                        break;
                    default:
                        mainFrame.addLogs("ping got");
                        break;
                }
            })).start();

            System.out.println(localization.getString("connected"));

            /* --------------------------- */

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/auth.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);

            AuthController controller = loader.getController();
            controller.setListeners((s, s2) -> {

                try {
                    auth = new User(s);
                    auth.hashAndSetPassword(s2);
                    Command cmd = new Command("login", auth);

                    Packet packet = Packet.formPacket(new Pair<>(Header.USER, auth), new Pair<>(Header.COMMAND, cmd));
                    out.writeObject(packet);
                    out.flush();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }, s -> {

                try {
                    auth = new User(s);
                    Command cmd = new Command("register", auth);

                    Packet packet = Packet.formPacket(new Pair<>(Header.USER, auth), new Pair<>(Header.COMMAND, cmd));
                    out.writeObject(packet);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });

            stage.setTitle(localization.getString("authorization"));

            stage.setWidth(400);
            stage.setHeight(750);

            stage.show();

        } catch (UnknownHostException e) {
            alert("UnknownHostException", localization.getString("host_down"));
        } catch (IOException e) {
            alert("IOException", localization.getString("connection_error"));
        }
    }

}
