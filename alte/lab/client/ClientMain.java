package alte.lab.client;

import alte.lab.Pair;
import alte.lab.User;
import alte.lab.localization.Localization;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Exchanger;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ClientMain extends Application {
    public static String hostname = "uriy.yuran.us";
    public static int port = 54105;
    public static User auth = null;
    public static Localization localization;
    public static boolean reconnected = false;
    public static Socket connection;
    public static ConsoleListener consoleCommandListener;

    public static void main(String[] args) {

        Socket connection;
        localization = new Localization();
        try {
            connection = new Socket(hostname, port);
            System.out.println(localization.getString("connecting"));

            consoleCommandListener = new ConsoleListener(connection, false);
            new Thread(consoleCommandListener).start();
            new Thread(new ServerListner(connection, hostname, port)).start();

            System.out.println(localization.getString("connected"));


        } catch (UnknownHostException e) {
            System.out.println(localization.getString("host_down"));
        } catch (IOException e) {
            System.out.println(localization.getString("connection_error"));
        }

        Application.launch(args);

    }



    @Override
    public void start(Stage stage) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/auth.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);

            AuthController controller = loader.getController();
            controller.setListeners(new BiFunction<String, String, Pair<String, String>>() {
                @Override
                public Pair<String, String> apply(String s, String s2) {

                    String response = "";
                    try {
                        StringBuilder command = new StringBuilder();
                        command.append("login {'Login':'").append(s).append("', 'Password':'").append(s2).append("'}");
                        response = consoleCommandListener.parseCommand(command.toString());

                        if(response.equals(localization.getString("everything_is_ok"))) {

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
                        }
                    }
                    catch(Exception e) {
                        response = e.getMessage();
                    }

                    return new Pair<>(localization.getString("authorization"), response); //AUTH
                }
            }, s -> {
                String response = "";
                try {
                    response = consoleCommandListener.parseCommand("login {'Login':'" + s + "'}");
                }
                catch(Exception e) {
                    response = e.getMessage();
                }

                return new Pair<>(localization.getString("registration"), response); //AUTH
            });

            stage.setTitle(localization.getString("authorization"));

            stage.setWidth(400);
            stage.setHeight(750);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
