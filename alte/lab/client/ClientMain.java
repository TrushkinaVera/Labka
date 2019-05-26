package alte.lab.client;

import alte.lab.User;
import alte.lab.localization.Localization;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Exchanger;

public class ClientMain {
    public static String hostname = "uriy.yuran.us";
    public static int port = 54105;
    public static User auth = null;
    public static Localization localization;
    public static boolean reconnected = false;
    public static Socket connection;
    public static void main(String[] args) {
        Socket connection;
        localization = new Localization();
        try {
            connection = new Socket(hostname, port);
            System.out.println(localization.getString("connecting"));
            new Thread(new ConsoleListener(connection)).start();
            new Thread(new ServerListner(connection, hostname, port)).start();

            System.out.println(localization.getString("connected"));


        } catch (UnknownHostException e) {
            System.out.println(localization.getString("host_down"));
        } catch (IOException e) {
            System.out.println(localization.getString("connection_error"));
        }
    }
}
