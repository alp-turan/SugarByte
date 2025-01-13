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

    private DatabaseManager databaseManager;

    @BeforeEach
    void setUp() {
        databaseManager = DatabaseManager.getInstance();
    }

    @AfterEach
    void tearDown() {
        databaseManager.closeConnection();
    }

    @Test
    void testSingletonInstance() {
        DatabaseManager instance1 = DatabaseManager.getInstance();
        DatabaseManager instance2 = DatabaseManager.getInstance();

        assertSame(instance1, instance2, "DatabaseManager should follow the singleton pattern.");
    }

    @Test
    void testConnectionIsNotNull() {
        Connection connection = databaseManager.getConnection();

        assertNotNull(connection, "Database connection should not be null.");
    }

    @Test
    void testConnectionIsValid() throws SQLException {
        Connection connection = databaseManager.getConnection();

        assertTrue(connection.isValid(2), "Database connection should be valid.");
    }

    @Test
    void testInitDBCreatesUserTable() throws SQLException {
        Connection connection = databaseManager.getConnection();
        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='user';"
        );

        assertTrue(resultSet.next(), "'user' table should exist in the database.");

        resultSet.close();
        statement.close();
    }

    @Test
    void testInitDBCreatesLogEntryTable() throws SQLException {
        Connection connection = databaseManager.getConnection();
        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='logentry';"
        );

        assertTrue(resultSet.next(), "'logentry' table should exist in the database.");

        resultSet.close();
        statement.close();
    }

    @Test
    void testReconnectIfClosed() throws SQLException {
        Connection connection = databaseManager.getConnection();

        connection.close();
        assertTrue(connection.isClosed(), "Connection should be closed.");

        Connection newConnection = databaseManager.getConnection();
        assertNotNull(newConnection, "Reconnected database connection should not be null.");
        assertTrue(newConnection.isValid(2), "Reconnected database connection should be valid.");
    }

    @Test
    void testCloseConnection() throws SQLException {
        Connection connection = databaseManager.getConnection();
        databaseManager.closeConnection();

        assertTrue(connection.isClosed(), "Database connection should be closed after closeConnection() call.");
    }
}