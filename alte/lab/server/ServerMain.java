package alte.lab.server;
import alte.lab.Human;
import alte.lab.Mail;
import alte.lab.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class ServerMain {

    static boolean auth(Connection conn, String login, String password) {
        int rows = 0;
        try {
            String sql = "SELECT * from users WHERE login = ? and pass = ? LIMIT 1";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            rows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return !(rows == 0);
    }

    private static String DB_URL = "jdbc:postgresql://localhost:5432/labaa";
    private static String USER = "smarts";
    private static String PASS = "difpas2";
    public static Connection conn;
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
        cmds.add(new CollectionCommand() {
            @Override
            public String getName() {
                return "login";
            }

            @Override
            public Object doCommand(Connection conn, Object arg, User usr) throws SQLException {
                return auth(conn, usr.getLogin(), usr.getPassword());
            }
        });
        cmds.add(new CollectionCommand() {
            @Override
            public String getName() {
                return "register";
            }

            @Override
            public Object doCommand(Connection conn, Object arg, User usr) throws SQLException {
                //TODO: регистрация

                String login = (String)arg;
                String sql = "INSERT INTO users (login, passwords) Values (?, ?)";
                PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                preparedStatement.setString(1, login);

                byte[] bytes = new byte[16];
                new Random().nextBytes(bytes);
                String password = new String(Base64.getEncoder().encode(bytes));
                preparedStatement.setString(2, User.encrypt(password));

                int rows = preparedStatement.executeUpdate();
                ResultSet razvrat = preparedStatement.getGeneratedKeys();
                razvrat.next();
                long id = razvrat.getLong(1);

                Mail.mail(login, "Вам письмо", "Ну здарова. Пароль сматри сюда: "+password);
                return "Account created, your unique ID is "+ id + " (and password is "+ password + ")";
            }
        });

        syncher = new Semaphore(1);


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
                PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, usr.getLogin());
                preparedStatement.setString(2, harg.getName());
                preparedStatement.setInt(3, harg.getAge());
                preparedStatement.setInt(4, harg.getPosX());
                preparedStatement.setInt(5, harg.getPosY());
                preparedStatement.setString(6, harg.getDate().toString());
                int rows = preparedStatement.executeUpdate();
                if (rows == 0) {
                    System.out.println("Creating RAZVRAT failed");
                }
                ResultSet razvrat = preparedStatement.getGeneratedKeys();
                razvrat.next();
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
        ServerMain.conn = connection;
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