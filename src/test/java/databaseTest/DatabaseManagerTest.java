package databaseTest;

import database.DatabaseManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the DatabaseManager class.
 */
class DatabaseManagerTest {

    private DatabaseManager databaseManager; // Reference to the singleton DatabaseManager instance

    /**
     * Setting up a fresh DatabaseManager instance before each test.
     * Ensures tests interact with the same singleton instance.
     */
    @BeforeEach
    void setUp() {
        databaseManager = DatabaseManager.getInstance(); // Accessing the singleton instance using the getInstance() method
    }

    /**
     * Cleaning up resources and closing the database connection after each test.
     * Guarantees proper cleanup and prevents resource leaks.
     */
    @AfterEach
    void tearDown() {
        databaseManager.closeConnection(); // Calling the closeConnection() method to release database resources
    }

    /**
     * Validates that DatabaseManager implements the Singleton design pattern.
     * Ensures multiple calls to getInstance() return the same object reference.
     */
    @Test
    void testSingletonInstance() {
        // Retrieving the singleton instance twice
        DatabaseManager instance1 = DatabaseManager.getInstance();
        DatabaseManager instance2 = DatabaseManager.getInstance();

        // Asserting that both instances refer to the same object in memory
        assertSame(instance1, instance2, "DatabaseManager should follow the singleton pattern.");
    }

    /**
     * Confirms that getConnection() provides a non-null Connection object.
     * Ensures a valid connection is available for database operations.
     */
    @Test
    void testConnectionIsNotNull() {
        Connection connection = databaseManager.getConnection(); // Obtaining a Connection object from DatabaseManager

        // Asserting that the Connection object is not null
        assertNotNull(connection, "Database connection should not be null.");
    }

    /**
     * Verifies that the database connection is valid.
     * Uses the isValid(int timeout) method, which checks the connection's health.
     */
    @Test
    void testConnectionIsValid() throws SQLException {
        Connection connection = databaseManager.getConnection(); // Fetching the Connection object

        // Checking if the connection is valid with a timeout of 2 seconds
        assertTrue(connection.isValid(2), "Database connection should be valid.");
    }

    /**
     * Ensures that the 'user' table is correctly created in the database.
     * Queries the SQLite system table (sqlite_master) to confirm the table's existence.
     */
    @Test
    void testInitDBCreatesUserTable() throws SQLException {
        Connection connection = databaseManager.getConnection(); // Fetching the database connection
        Statement statement = connection.createStatement();      // Creating a Statement object to execute SQL queries

        // Querying SQLite's system catalog to check for the existence of the 'user' table
        ResultSet resultSet = statement.executeQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='user';"
        );

        // Validating that the ResultSet contains at least one row, indicating the table exists
        assertTrue(resultSet.next(), "'user' table should exist in the database.");

        // Closing the ResultSet and Statement objects to free resources
        resultSet.close();
        statement.close();
    }

    /**
     * Ensures that the 'logentry' table is correctly created in the database.
     * Similar to the test for the 'user' table, but for a different table.
     */
    @Test
    void testInitDBCreatesLogEntryTable() throws SQLException {
        Connection connection = databaseManager.getConnection(); // Fetching the database connection
        Statement statement = connection.createStatement();      // Creating a Statement object for SQL execution

        // Querying SQLite's system catalog for the existence of the 'logentry' table
        ResultSet resultSet = statement.executeQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='logentry';"
        );

        // Asserting that the 'logentry' table exists
        assertTrue(resultSet.next(), "'logentry' table should exist in the database.");

        // Closing resources after the assertion
        resultSet.close();
        statement.close();
    }

    /**
     * Tests the automatic reconnection feature of DatabaseManager.
     * Validates that a new connection is established if the current one is closed.
     */
    @Test
    void testReconnectIfClosed() throws SQLException {
        Connection connection = databaseManager.getConnection(); // Fetching the current connection

        connection.close(); // Explicitly closing the connection to simulate a closed state
        assertTrue(connection.isClosed(), "Connection should be closed."); // Confirming the connection is closed

        // Fetching a new connection after the previous one was closed
        Connection newConnection = databaseManager.getConnection();

        // Ensuring the new connection is not null and valid
        assertNotNull(newConnection, "Reconnected database connection should not be null.");
        assertTrue(newConnection.isValid(2), "Reconnected database connection should be valid.");
    }

    /**
     * Confirms that the closeConnection() method properly closes the database connection.
     * Ensures that resources are released as expected.
     */
    @Test
    void testCloseConnection() throws SQLException {
        Connection connection = databaseManager.getConnection(); // Fetching the current connection

        databaseManager.closeConnection(); // Closing the connection using the closeConnection() method

        // Asserting that the connection is now closed
        assertTrue(connection.isClosed(), "Database connection should be closed after closeConnection() call.");
    }
}

