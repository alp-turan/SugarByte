package serviceTest;


import model.LogEntry;
import database.LogEntryDAO;
import model.User;
import service.AlarmService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.junit.jupiter.api.AfterEach;
import service.LogService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Unit test class for testing the functionality of the LogService class
class LogServiceTest {

    private LogEntryDAO mockLogEntryDAO; // Mock object for LogEntryDAO to isolate database interactions
    private User mockUser;              // Mocked User object to simulate a user entity
    private LogEntry mockLogEntry;      // Mocked LogEntry object to simulate log entries

    private MockedStatic<AlarmService> mockedAlarmService; // Static mock for AlarmService to intercept static method calls

    /**
     * Sets up the mock objects and static mocks before each test.
     * Ensures a clean state for each test case by using JUnit's @BeforeEach annotation.
     */
    @BeforeEach
    void setUp() {
        mockLogEntryDAO = mock(LogEntryDAO.class); // Mockito's mock() creates a mocked instance of LogEntryDAO
        mockUser = mock(User.class);              // Creates a mocked instance of User
        mockLogEntry = mock(LogEntry.class);      // Creates a mocked instance of LogEntry

        // Mocking static methods of AlarmService to prevent actual method execution
        mockedAlarmService = Mockito.mockStatic(AlarmService.class);
    }

    /**
     * Tests the createEntry method of LogService.
     * Ensures that the method returns a LogEntry object with the expected properties when given valid input.
     */
    @Test
    void testCreateEntry() {
        // Creating a new LogEntry object to simulate input
        LogEntry entry = new LogEntry();
        entry.setDate("2025-01-13"); // Setting the date property for the LogEntry

        // Creating a User object to simulate the associated user
        User user = new User();

        // Configures the mocked DAO to return the input entry when createLogEntry is called
        when(mockLogEntryDAO.createLogEntry(any(LogEntry.class))).thenReturn(entry); // syntax acquired from ChatGPT

        // Calls the method under test
        LogEntry savedEntry = LogService.createEntry(entry, user);

        // Asserts that the returned LogEntry is not null
        assertNotNull(savedEntry);

        // Asserts that the date of the returned LogEntry matches the expected value
        assertEquals("2025-01-13", savedEntry.getDate());
    }

    /**
     * Verifies that the getEntriesForDate method returns an empty list
     * when no entries exist for the specified user ID and date.
     */
    @Test
    void testGetEntriesForDate_ShouldReturnEmptyList() {
        int userId = 1;                  // Simulated user ID
        String date = "2025-01-13";      // Simulated date for querying log entries

        // Configures the mocked DAO to return an empty list when queried
        when(mockLogEntryDAO.getEntriesByDate(userId, date)).thenReturn(Collections.emptyList()); // syntax acquired from ChatGPT

        // Calls the method under test
        List<LogEntry> entries = LogService.getEntriesForDate(userId, date);

        // Asserts that the returned list is empty
        assertTrue(entries.isEmpty(), "Entries should be empty");
    }

    /**
     * Cleans up resources and mocks after each test.
     * Ensures the static mock for AlarmService is properly closed to avoid interference with other tests.
     */
    @AfterEach
    void tearDown() {
        mockedAlarmService.close(); // Closes the static mock to release resources
    }
}

