package alte.lab.client;

import alte.lab.connection.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
                        //TODO:делай все, что тебе надо с пакетами, приходящими с сервера
                        System.out.println(input);
                    }
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
