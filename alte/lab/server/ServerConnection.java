package alte.lab.server;

import alte.lab.Command;
import alte.lab.connection.Header;
import alte.lab.connection.Packet;
import alte.lab.connection.ResponseCode;
import javafx.util.Pair;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.util.concurrent.Semaphore;

public class ServerConnection implements Runnable{
    private Semaphore smp;
    private Socket socket;
    public ServerConnection(Semaphore smp, Socket socket) {
        System.out.println("new connection detected");
        this.smp = smp;
        this.socket = socket;
        new Thread(this).start();
    }
    @Override
    public void run() {

        try {
            System.out.println("waiting for input");
            OutputStream ts = socket.getOutputStream();
            InputStream is = socket.getInputStream();
            ObjectOutputStream oos = new ObjectOutputStream(ts);
            ObjectInputStream ois = new ObjectInputStream(is);
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
                    boolean executed = false;
                    try {
                        for (CollectionCommand e : ServerMain.cmds) {
                            if (e.getName().equals(cmd.getText())) {
                                razvrat = e.doCommand(ServerMain.conn, cmd.getArgument(), input.getUser());
                                executed = true;
                                break;
                            }
                        }
                        if (executed != true) {

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
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        } catch (ClassNotFoundException exception) {
            exception.printStackTrace();
        }
        catch (IOException ex) {
            System.out.println("Кажется, мы сломались");
            ex.printStackTrace();
            System.exit(0);
        }
    }

}
