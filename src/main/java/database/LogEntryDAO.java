package database;

import model.LogEntry;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for LogEntry.
 */
public class LogEntryDAO {

    /**
     * Insert a new log entry into the DB.
     */
    //changed1
    public LogEntry createLogEntry(LogEntry entry) {
        String checkSql = "SELECT id FROM logentry WHERE userId = ? AND date = ? AND timeOfDay = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement checkPs = conn.prepareStatement(checkSql)) {

            // Set parameters for checking the existing entry
            checkPs.setInt(1, entry.getUserId());
            checkPs.setString(2, entry.getDate());
            checkPs.setString(3, entry.getTimeOfDay());

            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next()) {
                    // If the entry exists, update it
                    int existingId = rs.getInt("id");
                    String updateSql = "UPDATE logentry SET bloodSugar = ?, carbsEaten = ?, foodDetails = ?, " +
                            "exerciseType = ?, exerciseDuration = ?, insulinDose = ?, otherMedications = ? WHERE id = ?";

                    try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                        updatePs.setDouble(1, entry.getBloodSugar());
                        updatePs.setDouble(2, entry.getCarbsEaten());
                        updatePs.setString(3, entry.getFoodDetails());
                        updatePs.setString(4, entry.getExerciseType());
                        updatePs.setInt(5, entry.getExerciseDuration());
                        updatePs.setDouble(6, entry.getInsulinDose());
                        updatePs.setString(7, entry.getOtherMedications());
                        updatePs.setInt(8, existingId);

                        // Execute the update and verify it worked
                        int rowsUpdated = updatePs.executeUpdate();
                        if (rowsUpdated > 0) {
                            System.out.println("Successfully updated entry with ID: " + existingId);
                        }
                    }
                } else {
                    // If the entry doesn't exist, insert a new one
                    String insertSql = "INSERT INTO logentry(userId, date, timeOfDay, bloodSugar, carbsEaten, foodDetails, " +
                            "exerciseType, exerciseDuration, insulinDose, otherMedications) VALUES(?,?,?,?,?,?,?,?,?,?)";

                    try (PreparedStatement insertPs = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                        insertPs.setInt(1, entry.getUserId());
                        insertPs.setString(2, entry.getDate());
                        insertPs.setString(3, entry.getTimeOfDay());
                        insertPs.setDouble(4, entry.getBloodSugar());
                        insertPs.setDouble(5, entry.getCarbsEaten());
                        insertPs.setString(6, entry.getFoodDetails());
                        insertPs.setString(7, entry.getExerciseType());
                        insertPs.setInt(8, entry.getExerciseDuration());
                        insertPs.setDouble(9, entry.getInsulinDose());
                        insertPs.setString(10, entry.getOtherMedications());

                        int rowsInserted = insertPs.executeUpdate();
                        if (rowsInserted > 0) {
                            try (ResultSet keys = insertPs.getGeneratedKeys()) {
                                if (keys.next()) {
                                    entry.setId(keys.getInt(1));
                                    System.out.println("Successfully created new log entry with ID: " + entry.getId());
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return entry;
    }




    /**
     * Retrieve all logs for a given userId and date, ordered by timeOfDay.
     */
    public List<LogEntry> getEntriesByDate(int userId, String date) {
        String sql = "SELECT * FROM logentry WHERE userId = ? AND date = ? ORDER BY timeOfDay ASC";
        List<LogEntry> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, date);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractLogEntry(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Helper to build a LogEntry from a ResultSet row
    private LogEntry extractLogEntry(ResultSet rs) throws SQLException {
        LogEntry e = new LogEntry();
        e.setId(rs.getInt("id"));
        e.setUserId(rs.getInt("userId"));
        e.setDate(rs.getString("date"));
        e.setTimeOfDay(rs.getString("timeOfDay"));
        e.setBloodSugar(rs.getDouble("bloodSugar"));
        e.setCarbsEaten(rs.getDouble("carbsEaten"));
        e.setFoodDetails(rs.getString("foodDetails"));
        e.setExerciseType(rs.getString("exerciseType"));
        e.setExerciseDuration(rs.getInt("exerciseDuration"));
        e.setInsulinDose(rs.getDouble("insulinDose"));
        e.setOtherMedications(rs.getString("otherMedications"));
        return e;
    }
}