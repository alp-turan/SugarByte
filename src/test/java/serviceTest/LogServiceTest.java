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

class LogServiceTest {

    private LogEntryDAO mockLogEntryDAO;
    private User mockUser;
    private LogEntry mockLogEntry;

    private MockedStatic<AlarmService> mockedAlarmService;

    @BeforeEach
    void setUp() {
        mockLogEntryDAO = mock(LogEntryDAO.class);
        mockUser = mock(User.class);
        mockLogEntry = mock(LogEntry.class);

        mockedAlarmService = Mockito.mockStatic(AlarmService.class);
    }

    @Test
    void testCreateEntry() {
        LogEntry entry = new LogEntry();
        entry.setDate("2025-01-13");

        User user = new User();

        when(mockLogEntryDAO.createLogEntry(any(LogEntry.class))).thenReturn(entry);

        LogEntry savedEntry = LogService.createEntry(entry, user);

        assertNotNull(savedEntry);
        assertEquals("2025-01-13", savedEntry.getDate());
    }


    @Test
    void testGetEntriesForDate_ShouldReturnEmptyList() {
        int userId = 1;
        String date = "2025-01-13";

        when(mockLogEntryDAO.getEntriesByDate(userId, date)).thenReturn(Collections.emptyList());

        List<LogEntry> entries = LogService.getEntriesForDate(userId, date);

        assertTrue(entries.isEmpty(), "Entries should be empty");
    }

    @AfterEach
    void tearDown() {
        mockedAlarmService.close();
    }
}