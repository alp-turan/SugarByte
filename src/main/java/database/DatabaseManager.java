package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DatabaseManager Class
 * A robust implementation of the singleton pattern for managing SQLite database connections.
 * Handles connection lifecycle, schema initialization, and provides thread-safe access.

 */
public class DatabaseManager {
    // DATABASE path string that defines the connection URL for our SQLite database
    private static final String DB_URL = "jdbc:sqlite:diabetesapp.db";

    // INSTANCE reference maintains the singleton pattern throughout the application
    private static DatabaseManager instance;
    // CONNECTION object serves as our primary interface with the SQLite database
    private Connection connection;

    /**
     * Private Constructor
     * Implements the singleton pattern by preventing external instantiation.
     * Initializes the SQLite driver and establishes the database connection.
     * Sets up the initial database schema if needed.
     */
    private DatabaseManager() {
        try {
            // LOADING operation pulls the SQLite JDBC driver into memory
            Class.forName("org.sqlite.JDBC");
            // CONFIRMATION message indicates successful driver initialization
            System.out.println("SQLite driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            // ERROR handling prints the stack trace for debugging purposes
            e.printStackTrace();
            // MESSAGE notification alerts about the missing JDBC driver dependency
            System.err.println("Could not load org.sqlite.JDBC driver. " +
                    "Make sure sqlite-jdbc is on the classpath.");
        }

        try {
            // ESTABLISHMENT process creates a new connection to our database file
            connection = DriverManager.getConnection(DB_URL);
            // SUCCESS message confirms the database connection is ready
            System.out.println("Connection established to " + DB_URL);
            // INITIALIZATION call sets up our database schema
            initDB();
        } catch (SQLException e) {
            // FAILURE handling prints detailed error information
            e.printStackTrace();
            // ERROR message indicates connection problems
            System.err.println("Failed to connect to the database: " + DB_URL);
            // NULLIFICATION ensures we don't keep invalid connection references
            connection = null;
        }
    }

    /**
     * Instance Accessor
     * Provides thread-safe access to the singleton DatabaseManager instance.
     * Creates the instance if it doesn't exist, following lazy initialization.
     *
     * @return DatabaseManager The singleton instance of the database manager
     */
    public static synchronized DatabaseManager getInstance() {
        // VERIFICATION check determines if we need to create a new instance
        if (instance == null) {
            // CREATION process instantiates our singleton manager
            instance = new DatabaseManager();
        }
        // RETURN statement provides the singleton instance
        return instance;
    }

    /**
     * Connection Provider
     * Supplies the active database connection, attempting reconnection if needed.
     * Implements connection recovery logic for resilient database operations.
     *
     * @return Connection The active database connection or null if unavailable
     */
    public Connection getConnection() {
        try {
            // VALIDATION check ensures our connection is still valid
            if (connection == null || connection.isClosed()) {
                // WARNING message indicates connection loss
                System.err.println("Database connection is closed. Attempting to reconnect...");
                // RECONNECTION attempt establishes a fresh database connection
                connection = DriverManager.getConnection(DB_URL);
                // VERIFICATION process confirms successful reconnection
                if (connection != null) {
                    // SUCCESS message indicates restored connectivity
                    System.out.println("Successfully reconnected to the database.");
                } else {
                    // FAILURE message signals unsuccessful reconnection
                    System.err.println("Reconnection to the database failed.");
                }
            }
        } catch (SQLException e) {
            // ERROR handling captures connection problems
            e.printStackTrace();
            // MESSAGE notification indicates reconnection failure
            System.err.println("Failed to reconnect to the database.");
        }
        // RETURN statement provides the current connection state
        return connection;
    }

    /**
     * Connection Terminator
     * Safely closes the database connection and releases associated resources.
     * Implements proper cleanup procedures for database management.
     */
    public void closeConnection() {
        // VALIDATION check ensures we have an active connection
        if (connection != null) {
            try {
                // TERMINATION process closes the database connection
                connection.close();
                // CONFIRMATION message indicates successful closure
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                // ERROR handling captures closure problems
                e.printStackTrace();
                // MESSAGE notification indicates closure failure
                System.err.println("Failed to close the database connection.");
            }
        }
    }

    /**
     * Schema Initializer
     * Creates and maintains the database schema for the application.
     * Establishes tables for users and log entries with appropriate constraints.
     */
    private void initDB() {
        // DEFINITION string specifies the user table schema
        String createUserTable = "CREATE TABLE IF NOT EXISTS user (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "diabetesType TEXT," +
                "insulinType TEXT," +
                "insulinAdmin TEXT," +
                "email TEXT UNIQUE NOT NULL," +
                "phone TEXT," +
                "doctorName TEXT," +
                "doctorEmail TEXT," +
                "doctorAddress TEXT," +
                "doctorEmergencyPhone TEXT," +
                "logbookType TEXT," +
                "password TEXT NOT NULL" +
                ");";

        // DEFINITION string specifies the log entry table schema
        String createLogEntryTable = "CREATE TABLE IF NOT EXISTS logentry (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "userId INTEGER NOT NULL," +
                "date TEXT NOT NULL," +
                "timeOfDay TEXT NOT NULL," +
                "bloodSugar REAL," +
                "carbsEaten REAL," +
                "hoursSinceMeal INTEGER," +
                "foodDetails TEXT," +
                "exerciseType TEXT," +
                "exerciseDuration INTEGER," +
                "insulinDose REAL," +
                "otherMedications TEXT," +
                "FOREIGN KEY(userId) REFERENCES user(id) ON DELETE CASCADE" +
                ");";

        try (Statement stmt = connection.createStatement()) {
            // EXECUTION process creates the user table
            stmt.execute(createUserTable);
            // CONFIRMATION message indicates successful user table creation
            System.out.println("Ensured 'user' table exists with updated schema.");

            // EXECUTION process creates the log entry table
            stmt.execute(createLogEntryTable);
            // CONFIRMATION message indicates successful log entry table creation
            System.out.println("Ensured 'logentry' table exists.");
        } catch (SQLException e) {
            // ERROR handling captures schema creation problems
            e.printStackTrace();
            // MESSAGE notification indicates initialization failure
            System.err.println("initDB() failed while creating/updating tables.");
        }
    }
}