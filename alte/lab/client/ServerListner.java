package alte.lab.client;

import alte.lab.Human;
import alte.lab.connection.Packet;
import alte.lab.connection.ResponseCode;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.concurrent.Exchanger;

import static alte.lab.client.ClientMain.localization;
import static alte.lab.connection.ResponseCode.*;

public class ServerListner implements Runnable{
    private ObjectInputStream in;
    private Socket conn;
    private String hostname;
    private int port;

    public ServerListner(Socket connection, String hostname, int port) {
        this.conn = connection;
        this.hostname = hostname;
        this.port = port;
        try {
            System.out.println("Creating IN");
            this.in = new ObjectInputStream(connection.getInputStream());
            System.out.println("IN "+in.available());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

            Packet input;
            while (true) {
                try {
                    if ((input = (Packet) in.readObject()) != null) {
                        ResponseCode code = input.getReponseCode();
                        switch (code) {
                            case OK:
                                String data = input.getStringResponse();
                                System.out.println(input.getStringResponse());
                                break;
                            case UNATHORIZED:
                            case BAD_REQUEST:
                                System.out.println(code.getMessage(localization));
                                break;
                            default:
                                System.out.println("ping got");
                                break;
                        }
                    }
                } catch (EOFException e) {
                    try {
                        conn.close();
                        System.out.println(localization.getString("lost_conn"));
                        System.out.println("reconnecting");
                        while (true) {
                            try {
                                conn = new Socket(hostname, port);
                                System.out.println("Creating IN");
                                this.in = new ObjectInputStream(conn.getInputStream());
                                System.out.println("IN " + in.available());
                                ClientMain.reconnected = true;
                                ClientMain.connection = conn;
                                break;
                            } catch (IOException d) {
                                continue;
                            }
                        }
                    } catch (Exception d) {
                        d.printStackTrace();
                    }
                } catch (IOException | ClassNotFoundException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
    }
}
