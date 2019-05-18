package alte.lab.server;
import alte.lab.Human;

import java.sql.*;
import java.util.ArrayList;

public class ServerMain {
    private static String DB_URL = "jdbc:postgresql://uriy.yuran.us:5432/lab7";
    private static String USER = "blablabla";
    private static String PASS = "blablabla";

    public static void main(String[] args) {
        ArrayList<CollectionCommand> cmds = new ArrayList<>();


        //Adding commands
        cmds.add(new CollectionCommand() {
            @Override
            public String getName() {
                return "add";
            }

            @Override
            public Object doCommand(Connection conn, Human arg) throws SQLException {
                conn.prepareCall("INSERT INTO object values ('"+arg.getName()+"', "+ arg.getAge()+ ", "+ arg.getPosX() + ", " +arg.getPosY() +")");
                return "OK";
            }
        });
        cmds.add(new CollectionCommand() {
            @Override
            public String getName() {
                return "remove";
            }

            @Override
            public Object doCommand(Connection conn, Human arg) {
                return "OK";
            }
        });


        //TODO: Артем, ебашь серверную часть
        //Driver conn = new Driver();
        try {
            Class.forName("org.postgresql.Driver");

        } catch (ClassNotFoundException e) {
            System.out.println("JDBC for postgres was lost");
            System.exit(0);
        }
        Connection connection = null;

        try {
            connection = DriverManager
                    .getConnection(DB_URL, USER, PASS);

        } catch (SQLException e) {
            System.out.println("Connection Failed");
            e.printStackTrace();
            return;
        }
    }
}