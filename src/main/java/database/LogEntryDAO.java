package database;

import model.LogEntry;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LogEntryDAO {

    /**
     * Create (insert) a new log entry.
     */
    public LogEntry createLogEntry(LogEntry entry) {
        String sql = "INSERT INTO logentry(" +
                "userId, date, timeOfDay, bloodSugar, carbsEaten, foodDetails," +
                "exerciseType, exerciseDuration, insulinDose, otherMedications) " +
                "VALUES(?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, entry.getUserId());
            ps.setString(2, entry.getDate());
            ps.setString(3, entry.getTimeOfDay());
            ps.setDouble(4, entry.getBloodSugar());
            ps.setDouble(5, entry.getCarbsEaten());
            ps.setString(6, entry.getFoodDetails());
            ps.setString(7, entry.getExerciseType());
            ps.setInt(8, entry.getExerciseDuration());
            ps.setDouble(9, entry.getInsulinDose());
            ps.setString(10, entry.getOtherMedications());

            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                entry.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entry;
    }

    /**
     * Retrieve all logs for a given userId/date.
     */
    public List<LogEntry> getEntriesByDate(int userId, String date) {
        String sql = "SELECT * FROM logentry WHERE userId = ? AND date = ? ORDER BY timeOfDay ASC";
        List<LogEntry> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, date);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractLogEntry(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

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
