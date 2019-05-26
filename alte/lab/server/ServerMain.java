package alte.lab.server;
import alte.lab.Human;
import alte.lab.Mail;
import alte.lab.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.*;
import java.util.*;
import java.util.concurrent.Semaphore;

public class ServerMain {

    static boolean auth(Connection conn, String login, String password) {
        int rows = 0;
        try {
            String sql = "SELECT COUNT(*) from users WHERE login = ? and password = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            ResultSet s = preparedStatement.executeQuery();
            s.next();
            rows = s.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return !(rows == 0);
    }

    private static String DB_URL = "jdbc:postgresql://localhost:5432/labaa";//"jdbc:postgresql://pg/studs";
    private static String USER = "smarts";//"s";//"smarts";
    private static String PASS = "difpas2";//difpas2";
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

        syncher = new Semaphore(1);
        cmds = new ArrayList<>();
        cmds.add(new CollectionCommand() {
            @Override
            public String getName() {
                return "login";
            }

            @Override
            public Object doCommand(Connection conn, Object arg, User usr) throws SQLException {
                return Boolean.toString(auth(conn, usr.getLogin(), usr.getPassword()));
            }
        });
        cmds.add(new CollectionCommand() {

            @Override
            public String getName() {
                return "show";
            }

            @Override
            public Object doCommand(Connection conn, Object arg, User usr) throws SQLException {

                String sql = "SELECT * from objects where login = ?";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, usr.getLogin());
                ResultSet s = preparedStatement.executeQuery();
                int id, x, y, age;
                String login, name, date;

                Map<Integer, Human> result = new HashMap<>();
                while (s.next() != false) {
                    id = s.getInt("id");
                    x = s.getInt("x");
                    y = s.getInt("y");
                    age = s.getInt("age");
                    name = s.getString("name");
                    login = s.getString("login");
                    date = s.getString("createdate");
                    result.put(new Integer(id), new Human(name, age, x, y));
                }
                return null;
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
                String sql = "INSERT INTO users (login, password) Values (?, ?)";
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
                String sql = "DELETE * FROM user WHERE id=?";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setInt(1, harg);
                int rows = preparedStatement.executeUpdate();

                return (rows > 0 ? "OK" : "Not found");

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
            stmt = connection.createStatement();
        } catch (SQLException e) {
            System.out.println("Error creating tables");
            e.printStackTrace();
        }
        ServerMain.conn = connection;
        //допустим здесь начинаем сервер

        int port = 54105;
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