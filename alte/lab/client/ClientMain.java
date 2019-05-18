package alte.lab.client;

public class ClientMain {
    public static void main(String[] args) {
        ConsoleListener l = new ConsoleListener();
        l.start();

        try {
            Class.forName("org.postgresql.Driver");

        } catch (ClassNotFoundException e) {
            System.out.println("JDBC for postgres was lost");
            System.exit(0);
        }
    }
}
