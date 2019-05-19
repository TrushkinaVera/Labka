package alte.lab.server;
import alte.lab.Human;

import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class ServerMain {
    private static String DB_URL = "jdbc:postgresql://uriy.yuran.us:5432/lab7";
    private static String USER = "blablabla";
    private static String PASS = "blablabla";
    public static ArrayList<CollectionCommand> cmds;
    public static Semaphore syncher;
    public static void main(String[] args) {
        cmds = new ArrayList<>();
        syncher = new Semaphore(1);

        //Adding commands
        cmds.add(new CollectionCommand() {
            @Override
            public String getName() {
                return "add";
            }

            @Override
            public Object doCommand(Connection conn, Object arg) throws SQLException {
                Human harg = (Human)arg;
                conn.prepareCall("INSERT INTO object values ('"+harg.getName()+"', "+ harg.getAge()+ ", "+ harg.getPosX() + ", " +harg.getPosY() +")");
                return "OK";
            }
        });
        cmds.add(new CollectionCommand() {
            @Override
            public String getName() {
                return "remove";
            }

            @Override
            public Object doCommand(Connection conn, Object arg) {
                return "OK";
            }
        });
        cmds.add(new CollectionCommand() {
            @Override
            public String getName() {
                return "login";
            }

            @Override
            public Object doCommand(Connection conn, Object arg) throws SQLException {
                Human harg = (Human)arg;
                conn.prepareCall("INSERT INTO object values ('"+harg.getName()+"', "+ harg.getAge()+ ", "+ harg.getPosX() + ", " +harg.getPosY() +")");
                return "OK";
            }
        });
        cmds.add(new CollectionCommand() {
            @Override
            public String getName() {
                return "save";
            }

            @Override
            public Object doCommand(Connection conn, Object arg) {
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