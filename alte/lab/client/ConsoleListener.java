package alte.lab.client;
import alte.lab.Command;
import alte.lab.CommandParser;
import java.util.Scanner;

import static jdk.nashorn.internal.objects.Global.print;

public class ConsoleListener extends Thread{
    @Override
    public void run() {
        Scanner reader = new Scanner(System.in);
        String input;
        while(true){
            input = reader.nextLine();
            System.out.println(input);
            Command cmd = CommandParser.parseAndExecute(input);
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