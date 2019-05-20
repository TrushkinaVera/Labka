package alte.lab.client;

import alte.lab.connection.Packet;
import alte.lab.connection.ResponseCode;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static alte.lab.client.ClientMain.localization;
import static alte.lab.connection.ResponseCode.*;

public class ServerListner implements Runnable{
    ObjectInputStream in;
    public ServerListner(Socket connection) {
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
        try {
            Packet input;
            while (true) {
                if ((input = (Packet) in.readObject()) != null) {
                    ResponseCode code = input.getReponseCode();
                    switch(code) {
                        case OK:
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
            }
        }
        catch (IOException | ClassNotFoundException | NullPointerException e) {
            e.printStackTrace();
        }
    }
}
