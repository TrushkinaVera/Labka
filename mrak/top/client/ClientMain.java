package mrak.top.client;

import mrak.top.Command;
import mrak.top.Human;
import mrak.top.Pair;
import mrak.top.User;
import mrak.top.connection.Header;
import mrak.top.connection.Packet;
import mrak.top.connection.ResponseCode;
import mrak.top.localization.Localization;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

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

    void loadScene(Stage stage, String header, String sceneName, double sx, double sy, boolean resize) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(sceneName));
                Parent root = loader.load();
                Scene scene = new Scene(root);

                MainController controller = loader.getController();
                mainFrame = controller;
                controller.drawCanvas(new Callable() {
                    @Override
                    public Object call() throws Exception {

                        Command cmd = new Command("show", auth);
                        Packet packet = Packet.formPacket(new Pair<>(Header.USER, auth), new Pair<>(Header.COMMAND, cmd));
                        out.writeObject(packet);
                        out.flush();
                        return null;
                    }
                });

                stage.setResizable(resize);
                stage.setWidth(sx);
                stage.setHeight(sy);
                stage.setScene(scene);
                stage.setTitle(header);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    void initStage(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/auth.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle(localization.getString("authorization"));
        stage.setWidth(400);
        stage.setHeight(750);
        stage.show();

        AuthController controller = loader.getController();
        controller.setAuthData("saleen658@gmail.com","hZy+b0ky1vABeE/g5GiaYg==");
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
                Command cmd = new Command("register", s);

                Packet packet = Packet.formPacket(new Pair<>(Header.USER, auth), new Pair<>(Header.COMMAND, cmd));
                out.writeObject(packet);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }

    @Override
    public void start(Stage stage) {

        Socket connection;
        localization = new Localization();
        try {
            connection = new Socket(hostname, port);
            System.out.println(localization.getString("connecting"));

            out = new ObjectOutputStream(connection.getOutputStream());
            new Thread(new ServerListner(connection, hostname, port, input -> {

                switch (input.getCommand().getText()) {
                    case "login":

                        if (input.getReponseCode() == ResponseCode.OK)
                            loadScene(stage, localization.getString("main_window"), "/fxml/main.fxml", 750, 750, false);
                        else alert("ERROR", input.getStringResponse());

                        break;
                    case "register":
                        break;
                    case "show":
                        List<Human> humans = (ArrayList<Human>)input.getResponse();
                        System.out.println(humans);
                        mainFrame.setPlayers(humans);
                        break;
                }

                if (mainFrame == null) return;

                ResponseCode code = input.getReponseCode();
                switch (code) {
                    case OK:
                        //String data = input.getStringResponse();
                        //mainFrame.addLogs(data);
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

            initStage(stage);

            /* --------------------------- */

        } catch (UnknownHostException e) {
            alert("UnknownHostException", localization.getString("host_down"));
        } catch (IOException e) {
            alert("IOException", localization.getString("connection_error"));
        }
    }
}
