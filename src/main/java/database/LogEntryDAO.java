package database;

import model.LogEntry;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * LogEntry Data Access Object (DAO)
 * Implements the DAO pattern to abstract and encapsulate all access to the logentry table.
 * This class provides an interface between the application logic and the database,
 * handling all CRUD (Create, Read, Update, Delete) operations for LogEntry objects.
 *
 * The DAO pattern helps maintain separation of concerns by isolating database operations
 * from the business logic, making the code more maintainable and testable.
 *
 */
public class LogEntryDAO {

    /**
     * Entry Creator and Updater
     * Implements an "upsert" pattern - attempts to update an existing entry,
     * creates a new one if none exists. This maintains data consistency by
     * preventing duplicate entries for the same user, date, and time.
     *
     * @param entry The LogEntry object containing all entry details
     * @return LogEntry The processed entry with updated ID if newly created
     */
    public LogEntry createLogEntry(LogEntry entry) {
        // SQL query looks for existing entries with matching key fields
        String checkSql = "SELECT id FROM logentry WHERE userId = ? AND date = ? AND timeOfDay = ?";

        try (
                // CONNECTION establishment leverages the DatabaseManager singleton
                Connection conn = DatabaseManager.getInstance().getConnection();
                // STATEMENT preparation creates a secure, parameterized query
                PreparedStatement checkPs = conn.prepareStatement(checkSql)
        ) {
            // PARAMETER binding prevents SQL injection by safely inserting values
            checkPs.setInt(1, entry.getUserId());
            // DATE value insertion uses string format expected by database
            checkPs.setString(2, entry.getDate());
            // TIME specification completes the unique identifier triplet
            checkPs.setString(3, entry.getTimeOfDay());

            try (
                    // EXECUTION retrieves any matching existing entries
                    ResultSet rs = checkPs.executeQuery()
            ) {
                // EXISTENCE check determines if we should update or insert
                if (rs.next()) {
                    // UPDATE LOGIC BLOCK: Modifies existing entry

                    // RETRIEVAL extracts the existing entry's identifier
                    int existingId = rs.getInt("id");
                    // SQL construction builds the update statement with all fields
                    String updateSql = "UPDATE logentry SET bloodSugar = ?, carbsEaten = ?, " +
                            "hoursSinceMeal = ?, foodDetails = ?, exerciseType = ?, " +
                            "exerciseDuration = ?, insulinDose = ?, otherMedications = ? " +
                            "WHERE id = ?";

                    try (
                            // STATEMENT preparation creates parameterized update query
                            PreparedStatement updatePs = conn.prepareStatement(updateSql)
                    ) {
                        // BINDING block: Sets all parameters for the update
                        updatePs.setDouble(1, entry.getBloodSugar());
                        updatePs.setDouble(2, entry.getCarbsEaten());
                        updatePs.setDouble(3, entry.getHoursSinceMeal());
                        updatePs.setString(4, entry.getFoodDetails());
                        updatePs.setString(5, entry.getExerciseType());
                        updatePs.setInt(6, entry.getExerciseDuration());
                        updatePs.setDouble(7, entry.getInsulinDose());
                        updatePs.setString(8, entry.getOtherMedications());
                        updatePs.setInt(9, existingId);

                        // EXECUTION performs the update operation
                        int rowsUpdated = updatePs.executeUpdate();
                        // VERIFICATION ensures the update was successful
                        if (rowsUpdated > 0) {
                            // CONFIRMATION message indicates successful update
                            System.out.println("Successfully updated entry with ID: " + existingId);
                        }
                    }
                } else {
                    // INSERT LOGIC BLOCK: Creates new entry

                    // SQL construction builds the insert statement with all fields
                    String insertSql = "INSERT INTO logentry(userId, date, timeOfDay, bloodSugar, " +
                            "carbsEaten, hoursSinceMeal, foodDetails, exerciseType, " +
                            "exerciseDuration, insulinDose, otherMedications) " +
                            "VALUES(?,?,?,?,?,?,?,?,?,?,?)";

                    try (
                            // STATEMENT preparation includes request for generated keys
                            PreparedStatement insertPs = conn.prepareStatement(insertSql,
                                    Statement.RETURN_GENERATED_KEYS)
                    ) {
                        // BINDING block: Sets all parameters for the insert
                        insertPs.setInt(1, entry.getUserId());
                        insertPs.setString(2, entry.getDate());
                        insertPs.setString(3, entry.getTimeOfDay());
                        insertPs.setDouble(4, entry.getBloodSugar());
                        insertPs.setDouble(5, entry.getCarbsEaten());
                        insertPs.setDouble(6, entry.getHoursSinceMeal());
                        insertPs.setString(7, entry.getFoodDetails());
                        insertPs.setString(8, entry.getExerciseType());
                        insertPs.setInt(9, entry.getExerciseDuration());
                        insertPs.setDouble(10, entry.getInsulinDose());
                        insertPs.setString(11, entry.getOtherMedications());

                        // EXECUTION performs the insert operation
                        int rowsInserted = insertPs.executeUpdate();
                        // VERIFICATION confirms successful insertion
                        if (rowsInserted > 0) {
                            try (
                                    // KEYS retrieval obtains the generated ID
                                    ResultSet keys = insertPs.getGeneratedKeys()
                            ) {
                                // IDENTITY update sets the new entry's ID
                                if (keys.next()) {
                                    entry.setId(keys.getInt(1));
                                    // CONFIRMATION message indicates successful creation
                                    System.out.println("Successfully created new log entry with ID: " +
                                            entry.getId());
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            // ERROR handling captures and logs database operation failures
            e.printStackTrace();
        }

        // RETURN provides the processed entry back to the caller
        return entry;
    }

    /**
     * Entry Retriever
     * Fetches all log entries for a specific user on a given date.
     * Results are ordered by time of day to provide a chronological view
     * of the user's diabetes management activities.
     *
     * @param userId The ID of the user whose entries we want to retrieve
     * @param date The date for which to retrieve entries
     * @return List<LogEntry> Collection of all matching log entries
     */
    public List<LogEntry> getEntriesByDate(int userId, String date) {
        // SQL query defines the selection criteria and ordering
        String sql = "SELECT * FROM logentry WHERE userId = ? AND date = ? ORDER BY timeOfDay ASC";
        // LIST initialization prepares for collecting results
        List<LogEntry> list = new ArrayList<>();

        try (
                // CONNECTION establishment uses the database manager
                Connection conn = DatabaseManager.getInstance().getConnection();
                // STATEMENT preparation creates a parameterized query
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            // PARAMETER binding sets the search criteria
            ps.setInt(1, userId);
            ps.setString(2, date);

            try (
                    // EXECUTION retrieves matching entries
                    ResultSet rs = ps.executeQuery()
            ) {
                // ITERATION processes each returned row
                while (rs.next()) {
                    // CONSTRUCTION creates LogEntry objects from results
                    list.add(extractLogEntry(rs));
                }
            }
        } catch (SQLException e) {
            // ERROR handling captures database operation failures
            e.printStackTrace();
        }
        // RETURN provides the collected entries
        return list;
    }

    /**
     * ResultSet Processor
     * Helper method that maps a database result row to a LogEntry object.
     * Encapsulates the logic for translating between database and object representations.
     *
     * @param rs The ResultSet positioned at the row to process
     * @return LogEntry A new object populated with the row's data
     * @throws SQLException If any database access errors occur
     */
    private LogEntry extractLogEntry(ResultSet rs) throws SQLException {
        // OBJECT initialization creates empty LogEntry
        LogEntry e = new LogEntry();

        // MAPPING block: Transfers database values to object fields
        e.setId(rs.getInt("id"));
        e.setUserId(rs.getInt("userId"));
        e.setDate(rs.getString("date"));
        e.setTimeOfDay(rs.getString("timeOfDay"));
        e.setBloodSugar(rs.getDouble("bloodSugar"));
        e.setCarbsEaten(rs.getDouble("carbsEaten"));
        e.setHoursSinceMeal(rs.getInt("hoursSinceMeal"));
        e.setFoodDetails(rs.getString("foodDetails"));
        e.setExerciseType(rs.getString("exerciseType"));
        e.setExerciseDuration(rs.getInt("exerciseDuration"));
        e.setInsulinDose(rs.getDouble("insulinDose"));
        e.setOtherMedications(rs.getString("otherMedications"));

        // RETURN provides the populated entry
        return e;
    }
}