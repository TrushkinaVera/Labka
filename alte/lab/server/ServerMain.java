package alte.lab.server;
import alte.lab.Human;
import alte.lab.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class ServerMain {
    private static String DB_URL = "jdbc:postgresql://localhost:5432/labaa";
    private static String USER = "smarts";
    private static String PASS = "difpas2";
    public static String createUserBd = new String("Create table if not exists users(id SERIAL PRIMARY KEY, login TEXT NOT NULL UNIQUE, password TEXT NOT NULL)");
    public static String createObjectsBd = new String("Create table if not exists objects(id SERIAL PRIMARY KEY," +
            "login TEXT NOT NULL," +
            "name TEXT NOT NULL UNIQUE," +
            "age int4 NOT NULL," +
            "x int4," +
            "y int4," +
            "createdate TEXT NOT NULL)");
    public static ArrayList<CollectionCommand> cmds;
    public static Semaphore syncher;
    public static void main(String[] args) {
        cmds = new ArrayList<>();
        syncher = new Semaphore(1);
        CollectionCommand reg_checker = new CollectionCommand() {
            @Override
            public String getName() {
                return null;
            }

            @Override
            public Object doCommand(Connection conn, Object arg, User usr) throws SQLException {
                String sql = "SELECT COUNT() from users WHERE login = ? and pass = ?";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, usr.getLogin());
                preparedStatement.setString(2, usr.getPassword());
                return new Boolean(false);
            }
        };
        //Adding commands
        cmds.add(new CollectionCommand() {
            @Override
            public String getName() {
                return "add";
            }

            @Override
            public Object doCommand(Connection conn, Object arg, User usr) throws SQLException {
                Human harg = (Human)arg;
                String sql = "INSERT INTO objects (login, name, age, x, y, createdate) Values (?, ?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, usr.getLogin());
                preparedStatement.setString(2, harg.getName());
                preparedStatement.setInt(3, harg.getAge());
                preparedStatement.setInt(4, harg.getPosX());
                preparedStatement.setInt(5, harg.getPosY());
                preparedStatement.setString(6, harg.getDate().toString());
                int rows = preparedStatement.executeUpdate();
                ResultSet razvrat = preparedStatement.getResultSet();
                Long id = razvrat.getLong(1);
                return "Created object with id "+ id;
            }
        });
        cmds.add(new CollectionCommand() {
            @Override
            public String getName() {
                return "remove";
            }

            @Override
            public Object doCommand(Connection conn, Object arg, User usr) throws SQLException{
                Integer harg = (Integer) arg;
                String sql = "SELECT * FROM user LIMIT 1";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setInt(1, harg.intValue());
                int rows = preparedStatement.executeUpdate();

                return "OK";

            }
        });
        cmds.add(new CollectionCommand() {
            @Override
            public String getName() {
                return "login";
            }

            @Override
            public Object doCommand(Connection conn, Object arg, User usr) throws SQLException {
                User harg = usr;

                //conn.prepareCall("INSERT INTO object values ('"+harg.getName()+"', "+ harg.getAge()+ ", "+ harg.getPosX() + ", " +harg.getPosY() +")");
                return "OK";
            }
        });
        cmds.add(new CollectionCommand() {
            @Override
            public String getName() {
                return "save";
            }

            @Override
            public Object doCommand(Connection conn, Object arg, User usr) {
                return "OK";
            }
        });


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
        try {
            Statement stmt;
            stmt = connection.createStatement();
            stmt.executeUpdate(createUserBd);
            stmt = connection.createStatement();
            stmt.executeUpdate(createObjectsBd);
        } catch (SQLException e) {
            System.out.println("Error creating tables");
            e.printStackTrace();
        }

        //допустим здесь начинаем сервер

        int port = 7777;
        ServerSocket server = null;
        try {
            try {
                server  = new ServerSocket(port);
                System.out.println("Сервер запущен!");
                int id = 0;
                while(true){
                    new ServerConnection(syncher, server.accept());
                    id++;
                }
            }
            finally {
                System.out.println("Сервер выключается");
                server.close();
            }
        }
        catch (IOException e) {
            System.out.println("Сервер не был запущен из-за ошибки");
        }



    }
}