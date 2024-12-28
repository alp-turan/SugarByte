package service;

import database.LogEntryDAO;
import model.LogEntry;
import model.User;

import java.util.List;

/**
 * Provides higher-level methods for creating and retrieving logs,
 * and triggers AlarmService if needed.
 */
public class LogService {

    private static LogEntryDAO logEntryDAO = new LogEntryDAO();

    /**
     * Create a new log entry, then check for alarms.
     */
    public static LogEntry createEntry(LogEntry entry, User user) {
        //ADDED:

        // 1. Insert the entry in DB
        LogEntry saved = logEntryDAO.createLogEntry(entry);
        // 2. Alarm check
        AlarmService.checkAndSendAlarm(saved, user);
        return saved;
    }

    /**
     * Retrieve log entries for a specific date and user.
     */
    public static List<LogEntry> getEntriesForDate(int userId, String date) {
        System.out.println("Fetching entries for user " + userId + " on date " + date);
        return logEntryDAO.getEntriesByDate(userId, date);
    }
}