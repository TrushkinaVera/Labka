package alte.lab.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientMain {
    public static String hostname = "localhost";
    public static int port = 7777;

    public static void main(String[] args) {
        Socket connection;
        try {
            connection = new Socket(hostname, port);
            ObjectInputStream in;
            ObjectOutputStream out;
            System.out.println("Подключаемся к серверу  ...");
            in = new ObjectInputStream(connection.getInputStream());
            out = new ObjectOutputStream(connection.getOutputStream());

            System.out.println("Подключено. Ожидаем отправки команд на сервер...");
            /*cListener = new ClientFuncs.ConsoleInputListener();
            cListener.start();
            sListener = new ClientFuncs.ServerInputListener();
            sListener.start();

            */
        } catch (UnknownHostException e) {
            System.out.println("Хост не найден");
        } catch (IOException e) {
            System.out.println("Ошибка подключения");
        }
        ConsoleListener l = new ConsoleListener();
        l.start();

    }
}
