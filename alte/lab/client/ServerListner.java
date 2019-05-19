package alte.lab.client;

import alte.lab.connection.Packet;
import alte.lab.connection.ResponseCode;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static alte.lab.client.ClientMain.localization;
import static alte.lab.connection.ResponseCode.*;

public class ServerListner implements Runnable{
    ObjectInputStream in;
    public ServerListner(ObjectInputStream in ) {
        this.in = in;
    }

    @Override
    public void run() {
        while(true){
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
                        }
                    }
                }
            }
            catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
