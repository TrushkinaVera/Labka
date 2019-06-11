package alte.lab.client;
import alte.lab.Command;
import alte.lab.CommandParser;
import alte.lab.Pair;
import alte.lab.User;
import alte.lab.connection.Header;
import alte.lab.connection.Packet;

import javax.naming.ldap.SortKey;
import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Exchanger;

import static alte.lab.client.ClientMain.*;

public class ConsoleListener implements Runnable{
    private ObjectOutputStream out;
    private Socket conn;
    private boolean console;

    public ConsoleListener(Socket connection, boolean console) {
        try {
            this.conn = connection;
            this.out = new ObjectOutputStream(connection.getOutputStream());
            this.out.flush();

            this.console = console;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        Scanner reader = new Scanner(System.in);
        String input;

        if (console) {
            while (true) {

                input = reader.nextLine();
                if (ClientMain.reconnected) {
                    try {
                        this.conn = connection;
                        this.out = new ObjectOutputStream(conn.getOutputStream());
                        reconnected = false;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(parseCommand(input));
            }
        } else { //UI

            while (true) {
                if (ClientMain.reconnected) {
                    try {
                        this.conn = connection;
                        this.out = new ObjectOutputStream(conn.getOutputStream());
                        reconnected = false;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    String parseCommand(String input) {
        //if(ClientMain.reconnected == false) return localization.getString("lost_conn");

        try {
            Command cmd = CommandParser.parse(input);
            if ("login".equals(cmd.getText())) {
                auth = (User) cmd.getArgument();
                return localization.getString("auth_saved");
            }
            else if ("register".equals(cmd.getText())) {
                auth = new User((String)cmd.getArgument());
                return  localization.getString("try_register");
            }


            if(ClientMain.auth != null) {
                Packet packet = Packet.formPacket(new Pair<>(Header.USER, auth), new Pair<>(Header.COMMAND, cmd));
                out.writeObject(packet);
                out.flush();
            }
            else return localization.getString("auth_null");

        } catch (IOException e) {
            e.printStackTrace();
            return  "ты хуй";
        } catch (NullPointerException e) {
            return localization.getString("wrong_command");
        }

        return localization.getString("everything_is_ok");
    }
}
