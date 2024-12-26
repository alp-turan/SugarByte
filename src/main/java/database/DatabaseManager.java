package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A singleton DatabaseManager that handles the SQLite connection.
 */
public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:diabetesapp.db";

    private static DatabaseManager instance;
    private Connection connection;

    /**
     * Private constructor for singleton pattern.
     */
    private DatabaseManager() {
        try {
            // Load the SQLite driver
            Class.forName("org.sqlite.JDBC");
            System.out.println("SQLite driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("Could not load org.sqlite.JDBC driver. " +
                    "Make sure sqlite-jdbc is on the classpath.");
        }

        try {
            // Attempt to open/create the database
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Connection established: " + connection);
            initDB(); // optional: create tables
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to connect to " + DB_URL);
            connection = null;
        }
    }

    /**
     * Get the single instance of DatabaseManager.
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Provides the active database connection (or null if something failed).
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Example method to create a user table if it doesn't exist.
     * Adjust or remove as needed.
     */
    private void initDB() {
        String createUserTable = "CREATE TABLE IF NOT EXISTS user (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "diabetesType TEXT," +
                "insulinType TEXT," +
                "insulinAdmin TEXT," +
                "email TEXT UNIQUE," +
                "phone TEXT," +
                "doctorEmail TEXT," +
                "doctorAddress TEXT," +
                "doctorEmergencyPhone TEXT," +
                "password TEXT" +
                ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createUserTable);
            System.out.println("Ensured user table exists.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("initDB() failed while creating tables.");
        }
    }
}
