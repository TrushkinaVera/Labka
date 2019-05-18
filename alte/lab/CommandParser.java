package alte.lab;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CommandParser {

    private static Human decodeHumanArgument(String d){
        try {
            JSONParser parser = new JSONParser();
            JSONObject JValues;
            JValues = (JSONObject) parser.parse(d);
       /*catch (ParseException e1) {
            pushMessage("Invalid JSON data");
            return true;
        }*/
            String Name = "";
            int Age;
            double x, y;
            Name = (String) JValues.get("Name");
            Age = ((Long) JValues.get("Age")).intValue();
            x = (double) JValues.get("PosX");
            y = (double) JValues.get("PosY");
        /*catch (NullPointerException e2){
            pushMessage("Missing arguments");
            return true;
        }*/
            return new Human(Name, Age, x, y);
        }
        catch (Exception e){
            return null;
        }

    }
    private static User decodeUserArgument(String d){
        try {
            JSONParser parser = new JSONParser();
            JSONObject JValues;
            JValues = (JSONObject) parser.parse(d);
            String login = "";
            String password;
            login = (String) JValues.get("Login");
            System.out.println(login);
            password = ((String) JValues.get("Password"));
            System.out.println(password);
            User razvrat = new User(login);
            razvrat.hashAndSetPassword(password);
            return razvrat;//ДЕЛАЮТ РАЗВРАТЫ В ТРЕНАЖЕРНОМ ЗАЛЕ
        }
        catch (NullPointerException e){
            return null;
        }
        catch (Exception e){
            return null;
        }
    }
    /*public String decodeStringArgument(String d){
        return d;
    }
*/
    /**
     *
     * @param d входная команда из консоли
     * @return возвращает объект alte.lab.Command, который содержит в себе аргумент соответствующего типа
     */
    public static Command parseAndExecute(String d){
        try{
            String cmd = d.toLowerCase();
            int start = d.indexOf(" ");
            cmd = cmd.substring(0, start);
            String JData = d.substring(start+1).trim();
            Human hArg;
            User uArg;
            hArg = decodeHumanArgument(JData);
            uArg = decodeUserArgument(JData);
            if(hArg != null){
                return new Command(cmd, hArg);
            }
            else if (uArg != null){
                return new Command(cmd, uArg);
            }
            else return null;
        }
        catch(StringIndexOutOfBoundsException e){
            return null;
        }
    }
}