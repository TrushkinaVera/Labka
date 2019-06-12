package mrak.top.client;

import mrak.top.connection.Packet;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import static mrak.top.client.ClientMain.localization;

interface PacketDecoder {
    void decode(Packet input);
}

public class ServerListner implements Runnable {
    private ObjectInputStream in;
    private Socket conn;
    private String hostname;
    private int port;
    private PacketDecoder decoder;

    public ServerListner(Socket connection, String hostname, int port, PacketDecoder decoder) {
        this.conn = connection;
        this.hostname = hostname;
        this.port = port;
        this.decoder = decoder;

        try {
            System.out.println("Creating IN");
            this.in = new ObjectInputStream(connection.getInputStream());
            System.out.println("IN " + in.available());
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
                    decoder.decode(input);
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
