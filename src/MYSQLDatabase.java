import java.sql.*;
public class MYSQLDatabase {
    private final String host;
    private final int port;
    private final String databaseName;
    private final String user;
    private final String password;
    private Connection connection;
    private static boolean driverLoaded;

    public MYSQLDatabase(String host, int port, String databaseName, String user, String password) {
        this.host = host;
        this.port = port;
        this.databaseName = databaseName;
        this.user = user;
        this.password = password;
        this.connection = null;
        this.driverLoaded = false;
    }

    public void connect() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port+ "/" + databaseName + "?allowMultiQueries=true",
                    user,
                    password);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public Statement createStatement() {
        try {
            return connection.createStatement();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static void loadDriver() {
        if (!driverLoaded) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                System.out.println(e.getMessage());
                return;
            }
            driverLoaded = true;
        }
    }
    public PreparedStatement prepareStatement(String data) {
        try {
            return connection.prepareStatement(data);
        } catch (SQLException e) {

            throw new RuntimeException(e);
        }
    }
}
