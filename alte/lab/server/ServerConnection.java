package alte.lab.server;

import alte.lab.Command;
import alte.lab.connection.Header;
import alte.lab.connection.Packet;
import alte.lab.connection.ResponseCode;
import javafx.util.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Semaphore;

public class ServerConnection implements Runnable{
    private Semaphore smp;
    private Socket socket;
    private Connection conn;
    public ServerConnection(Semaphore smp, Socket socket) {
        System.out.println("new connection detected");
        this.smp = smp;
        this.socket = socket;
    }
    @Override
    public void run() {

        try {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(new Packet(Pair));
            Packet input;
            while(true){
                if((input = (Packet) ois.readObject()) != null){
                    System.out.println(input.getCommand().getText());
                    //вот твой инпут пакет
                    Command cmd = input.getCommand();
                    //блокиров очка
                    smp.acquire();
                    //работаем с чем нам надо
                    Object razvrat = null;
                    try {
                        for (CollectionCommand e : ServerMain.cmds) {
                            if (e.getName().equals(cmd.getText())) {
                                razvrat = e.doCommand(conn, cmd.getArgument(), input.getUser());
                                break;
                            }
                        }
                    } catch (SQLException e) {
                        //TODO: todo
                        e.printStackTrace();
                    }

                    Packet response = Packet.formPacket(new Pair<>(Header.CODE, razvrat!=null? ResponseCode.OK : ResponseCode.BAD_REQUEST), new Pair<>(Header.DATA, razvrat));
                    oos.writeObject(response);
                    oos.flush();

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
