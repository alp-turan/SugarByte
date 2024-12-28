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
            System.out.println("Connection established to " + DB_URL);
            initDB(); // Initialize the database schema
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to connect to the database: " + DB_URL);
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
        try {
            if (connection == null || connection.isClosed()) {
                System.err.println("Database connection is closed. Attempting to reconnect...");
                connection = DriverManager.getConnection(DB_URL);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to reconnect to the database.");
        }
        return connection;
    }

    /**
     * Close the database connection gracefully.
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Failed to close the database connection.");
            }
        }
    }

    /**
     * Initialize the database schema.
     * Creates necessary tables if they do not exist.
     */
    private void initDB() {
        // SQL to create the 'user' table
        String createUserTable = "CREATE TABLE IF NOT EXISTS user (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "diabetesType TEXT," +
                "insulinType TEXT," +
                "insulinAdmin TEXT," +
                "email TEXT UNIQUE NOT NULL," +
                "phone TEXT," +
                "doctorName TEXT," + // Added field for doctorName
                "doctorEmail TEXT," +
                "doctorAddress TEXT," +
                "doctorEmergencyPhone TEXT," +
                "logbookType TEXT," + // Added field for logbookType
                "password TEXT NOT NULL" +
                ");";

        // SQL to create the 'logentry' table
        String createLogEntryTable = "CREATE TABLE IF NOT EXISTS logentry (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "userId INTEGER NOT NULL," +
                "date TEXT NOT NULL," +
                "timeOfDay TEXT NOT NULL," +
                "bloodSugar REAL," +
                "carbsEaten REAL," +
                "foodDetails TEXT," +
                "exerciseType TEXT," +
                "exerciseDuration INTEGER," +
                "insulinDose REAL," +
                "otherMedications TEXT," +
                "FOREIGN KEY(userId) REFERENCES user(id) ON DELETE CASCADE" + // Ensure data integrity
                ");";

        try (Statement stmt = connection.createStatement()) {
            // Execute both create table statements
            stmt.execute(createUserTable);
            System.out.println("Ensured 'user' table exists with updated schema.");

            stmt.execute(createLogEntryTable);
            System.out.println("Ensured 'logentry' table exists.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("initDB() failed while creating/updating tables.");
        }
    }
}
