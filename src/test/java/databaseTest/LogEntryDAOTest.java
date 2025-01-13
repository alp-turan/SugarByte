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

    private LogEntryDAO logEntryDAO;
    private static final Logger logger = Logger.getLogger(LogEntryDAOTest.class.getName());

    @BeforeEach
    void setUp() {
        logger.info("Setting up the test environment for LogEntryDAO.");

        logEntryDAO = new LogEntryDAO();
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM logentry;");
            logger.info("Database table 'logentry' cleared.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to clear 'logentry' table in setUp.", e);
            fail("Database setup failed.");
        }
    }

    @AfterEach
    void tearDown() {
        logger.info("Tearing down the test environment.");
        DatabaseManager.getInstance().closeConnection();
    }

    @Test
    void testCreateLogEntry_InsertNewEntry() {
        logger.info("Starting test: testCreateLogEntry_InsertNewEntry");

        LogEntry newEntry = new LogEntry();
        newEntry.setUserId(1);
        newEntry.setDate("2025-01-12");
        newEntry.setTimeOfDay("Breakfast");
        newEntry.setBloodSugar(120.5);
        newEntry.setCarbsEaten(45.0);
        newEntry.setHoursSinceMeal(3);
        newEntry.setFoodDetails("Oatmeal and coffee");
        newEntry.setExerciseType("Walking");
        newEntry.setExerciseDuration(30);
        newEntry.setInsulinDose(2.5);
        newEntry.setOtherMedications("Insulin");

        LogEntry createdEntry = logEntryDAO.createLogEntry(newEntry);

        logger.info("Validating new entry creation.");
        assertNotNull(createdEntry.getId(), "New entry should have an ID.");
        assertThat(createdEntry.getUserId(), is(equalTo(1)));
        assertThat(createdEntry.getDate(), is(equalTo("2025-01-12")));
        assertThat(createdEntry.getTimeOfDay(), is(equalTo("Breakfast")));
    }

    @Test
    void testCreateLogEntry_UpdateExistingEntry() {
        logger.info("Starting test: testCreateLogEntry_UpdateExistingEntry");

        LogEntry entry = new LogEntry();
        entry.setUserId(1);
        entry.setDate("2025-01-12");
        entry.setTimeOfDay("Breakfast");
        entry.setBloodSugar(100.0);
        logEntryDAO.createLogEntry(entry);

        entry.setBloodSugar(130.0);
        entry.setCarbsEaten(50.0);
        LogEntry updatedEntry = logEntryDAO.createLogEntry(entry);

        logger.info("Validating update of existing entry.");
        assertNotNull(updatedEntry.getId(), "Updated entry should have an ID.");
        assertThat(updatedEntry.getBloodSugar(), is(equalTo(130.0)));
        assertThat(updatedEntry.getCarbsEaten(), is(equalTo(50.0)));
    }

    @Test
    void testGetEntriesByDate() {
        logger.info("Starting test: testGetEntriesByDate");

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

        List<LogEntry> entries = logEntryDAO.getEntriesByDate(1, "2025-01-12");

        logger.info("Validating retrieved entries.");
        assertThat(entries, hasSize(2));
        assertThat(entries.get(0).getTimeOfDay(), is(equalTo("Breakfast")));
        assertThat(entries.get(1).getTimeOfDay(), is(equalTo("Lunch")));
    }

    @Test
    void testGetEntriesByDate_NoEntriesFound() {
        logger.info("Starting test: testGetEntriesByDate_NoEntriesFound");

        List<LogEntry> entries = logEntryDAO.getEntriesByDate(1, "2025-01-12");

        logger.info("Validating that no entries were found.");
        assertThat(entries, is(empty()));
    }
}
