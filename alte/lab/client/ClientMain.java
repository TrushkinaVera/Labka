package alte.lab.client;

import alte.lab.User;
import alte.lab.localization.Localization;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientMain {
    public static String hostname = "localhost";
    public static int port = 7777;
    public static User auth = null;
    public static Localization localization;

    public static void main(String[] args) {
        Socket connection;
        localization = new Localization();
        try {
            connection = new Socket(hostname, port);
            ObjectInputStream in;
            ObjectOutputStream out;
            System.out.println(localization.getString("connecting"));
            in = new ObjectInputStream(connection.getInputStream());
            out = new ObjectOutputStream(connection.getOutputStream());
            System.out.println(localization.getString("connected"));
            new Thread(new ConsoleListener(out)).start();
            new Thread(new ServerListner(in)).start();


        } catch (UnknownHostException e) {
            System.out.println(localization.getString("host_down"));
        } catch (IOException e) {
            System.out.println(localization.getString("connection_error"));
        }
    }
}
