package alte.lab.client;
import alte.lab.Command;
import alte.lab.CommandParser;
import alte.lab.User;
import alte.lab.connection.Header;
import alte.lab.connection.Packet;
import javafx.util.Pair;

import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import static alte.lab.client.ClientMain.localization;
import static alte.lab.client.ClientMain.auth;

public class ConsoleListener implements Runnable{
    private ObjectOutputStream out;
    public ConsoleListener(Socket connection) {
        try {
            this.out = new ObjectOutputStream(connection.getOutputStream());
            this.out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        Scanner reader = new Scanner(System.in);
        String input;
        while(true){
            input = reader.nextLine();
            System.out.println(input);
            Command cmd = CommandParser.parse(input);
            try {
                if ("login".equals(cmd.getText())) {
                    auth = (User) cmd.getArgument();
                    System.out.println(localization.getString("auth_saved"));
                }

                if(ClientMain.auth != null) {
                    Packet packet = Packet.formPacket(new Pair<>(Header.USER, auth), new Pair<>(Header.COMMAND, cmd));
                    out.writeObject(packet);
                    out.flush();
                }
                else System.out.println(localization.getString("auth_null"));
            }
            catch(NullPointerException | IOException e) {
                System.out.println(localization.getString("wrong_command"));
            }
        }
    }
}
