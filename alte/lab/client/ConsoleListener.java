package alte.lab.client;
import alte.lab.Command;
import alte.lab.CommandParser;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

import static jdk.nashorn.internal.objects.Global.print;

public class ConsoleListener implements Runnable{
    private ObjectOutputStream out;
    public ConsoleListener(ObjectOutputStream out) {
        this.out = out;
    }

    @Override
    public void run() {
        Scanner reader = new Scanner(System.in);
        String input;
        while(true){
            input = reader.nextLine();
            System.out.println(input);
            Command cmd = CommandParser.parse(input);
            try{
                System.out.println(cmd.getText());
                System.out.println(cmd.getArgument().toString());
            }
            catch(NullPointerException e){
                //Command not found
            }
        }
    }
}
