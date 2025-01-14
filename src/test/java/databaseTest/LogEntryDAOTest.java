package databaseTest;

import database.DatabaseManager;
import database.LogEntryDAO;
import model.LogEntry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LogEntryDAO class with logging.
 */
class LogEntryDAOTest {

    private LogEntryDAO logEntryDAO; // DAO object responsible for database operations on `LogEntry` objects.
    private static final Logger logger = Logger.getLogger(LogEntryDAOTest.class.getName()); // Logger for debugging and test flow tracking.

    /**
     * Sets up the testing environment before each test method.
     * Ensures the database is in a consistent state by clearing the `logentry` table.
     */
    /* reference 21 - this method was created by using the code from this source as inspo: https://www.educative.io/answers/how-to-throw-a-sql-exception-in-java*/
    @BeforeEach
    void setUp() {
        logger.info("Setting up the test environment for LogEntryDAO."); // Logging test initialization.

        logEntryDAO = new LogEntryDAO(); // Instantiating the DAO for database interaction.
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            // Executing a SQL update to clear the `logentry` table.
            stmt.executeUpdate("DELETE FROM logentry;");
            logger.info("Database table 'logentry' cleared."); // Logging successful table clearing.
        } catch (SQLException e) {
            // Logging and failing the test in case of an exception during setup.
            logger.log(Level.SEVERE, "Failed to clear 'logentry' table in setUp.", e);
            fail("Database setup failed."); // Fails the test immediately with an error message.
        }
    }
    /* end of reference 21*/

    /**
     * Cleans up the testing environment after each test method.
     * Closes the database connection to release resources.
     */
    @AfterEach
    void tearDown() {
        logger.info("Tearing down the test environment."); // Logging teardown.
        DatabaseManager.getInstance().closeConnection(); // Closing the shared database connection.
    }

    /**
     * Tests the creation of a new `LogEntry` in the database.
     * Verifies that the entry is correctly inserted and its data is consistent.
     */
    @Test
    void testCreateLogEntry_InsertNewEntry() {
        logger.info("Starting test: testCreateLogEntry_InsertNewEntry"); // Test start logging.

        LogEntry newEntry = new LogEntry(); // Instantiating a new `LogEntry` object for testing.
        newEntry.setUserId(1);             // Assigning a user ID.
        newEntry.setDate("2025-01-12");    // Setting the log entry's date.
        newEntry.setTimeOfDay("Breakfast"); // Setting the meal time.
        newEntry.setBloodSugar(120.5);     // Assigning a blood sugar value.
        newEntry.setCarbsEaten(45.0);      // Setting carbs eaten.
        newEntry.setHoursSinceMeal(3);     // Specifying hours since the last meal.
        newEntry.setFoodDetails("Oatmeal and coffee"); // Adding food details.
        newEntry.setExerciseType("Walking"); // Adding exercise type.
        newEntry.setExerciseDuration(30);  // Specifying exercise duration in minutes.
        newEntry.setInsulinDose(2.5);      // Adding insulin dose.
        newEntry.setOtherMedications("Insulin"); // Specifying other medications.

        LogEntry createdEntry = logEntryDAO.createLogEntry(newEntry); // Persisting the log entry using the DAO.

        logger.info("Validating new entry creation."); // Logging validation process.
        assertNotNull(createdEntry.getId(), "New entry should have an ID."); // Ensures the entry is assigned a database-generated ID.
        assertThat(createdEntry.getUserId(), is(equalTo(1))); // Verifies the user ID is correct.
        assertThat(createdEntry.getDate(), is(equalTo("2025-01-12"))); // Ensures the date matches.
        assertThat(createdEntry.getTimeOfDay(), is(equalTo("Breakfast"))); // Confirms the time of day is correct.
    }

    /**
     * Tests updating an existing `LogEntry` in the database.
     * Confirms that updates are properly persisted and reflected in the database.
     */
    @Test
    void testCreateLogEntry_UpdateExistingEntry() {
        logger.info("Starting test: testCreateLogEntry_UpdateExistingEntry"); // Logging test initialization.

        LogEntry entry = new LogEntry(); // Creating a new `LogEntry`.
        entry.setUserId(1);              // Assigning user ID.
        entry.setDate("2025-01-12");     // Setting the date.
        entry.setTimeOfDay("Breakfast"); // Specifying the meal time.
        entry.setBloodSugar(100.0);      // Initial blood sugar level.
        logEntryDAO.createLogEntry(entry); // Inserting the initial entry into the database.

        entry.setBloodSugar(130.0); // Modifying the blood sugar level.
        entry.setCarbsEaten(50.0);  // Updating carbs eaten.
        LogEntry updatedEntry = logEntryDAO.createLogEntry(entry); // Updating the entry in the database.

        logger.info("Validating update of existing entry."); // Logging the validation step.
        assertNotNull(updatedEntry.getId(), "Updated entry should have an ID."); // Confirms the entry ID remains valid.
        assertThat(updatedEntry.getBloodSugar(), is(equalTo(130.0))); // Verifies the updated blood sugar value.
        assertThat(updatedEntry.getCarbsEaten(), is(equalTo(50.0))); // Confirms the carbs value is updated correctly.
    }

    /**
     * Tests retrieval of log entries by date.
     * Ensures the DAO retrieves all entries for a given user and date.
     */
    @Test
    void testGetEntriesByDate() {
        logger.info("Starting test: testGetEntriesByDate"); // Logging the test initialization.

        // Creating and inserting two entries for the same date.
        LogEntry entry1 = new LogEntry();
        entry1.setUserId(1);
        entry1.setDate("2025-01-12");
        entry1.setTimeOfDay("Breakfast");
        entry1.setBloodSugar(110.0);
        logEntryDAO.createLogEntry(entry1);

        LogEntry entry2 = new LogEntry();
        entry2.setUserId(1);
        entry2.setDate("2025-01-12");
        entry2.setTimeOfDay("Lunch");
        entry2.setBloodSugar(140.0);
        logEntryDAO.createLogEntry(entry2);

        List<LogEntry> entries = logEntryDAO.getEntriesByDate(1, "2025-01-12"); // Retrieving entries by date.

        logger.info("Validating retrieved entries."); // Logging the validation process.
        assertThat(entries, hasSize(2)); // Ensures two entries were retrieved.
        assertThat(entries.get(0).getTimeOfDay(), is(equalTo("Breakfast"))); // Validates the first entry's meal time.
        assertThat(entries.get(1).getTimeOfDay(), is(equalTo("Lunch"))); // Confirms the second entry's meal time.
    }

    /**
     * Tests retrieval of log entries for a date with no entries.
     * Ensures the DAO correctly returns an empty list.
     */
    @Test
    void testGetEntriesByDate_NoEntriesFound() {
        logger.info("Starting test: testGetEntriesByDate_NoEntriesFound"); // Logging the test start.

        List<LogEntry> entries = logEntryDAO.getEntriesByDate(1, "2025-01-12"); // Querying for a date with no entries.

        logger.info("Validating that no entries were found."); // Logging validation.
        assertThat(entries, is(empty())); // Confirms the retrieved list is empty.
    }
}

