package alte.lab.server;

import alte.lab.Command;
import alte.lab.connection.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class ServerConnection implements Runnable{
    private Semaphore smp;
    private Socket socket;
    public ServerConnection(Semaphore smp, Socket socket) {
        this.smp = smp;
        this.socket = socket;
    }
    @Override
    public void run() {

        try {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            Packet input;
            while(true){
                if((input = (Packet) ois.readObject()) != null){
                    //вот твой инпут пакет

                    //блокиров очка
                    smp.acquire();
                    //работаем с чем нам надо

                    smp.release();
                    //передер
                }
            }
        }
        catch (IOException ex) {
            System.out.println("Кажется, мы сломались");
            ex.printStackTrace();
            System.exit(0);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
