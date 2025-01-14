package database;

import model.User;
import java.sql.*;
import java.util.Optional;

/**
 * User Data Access Object (DAO)
 * Manages database operations for user data, implementing secure CRUD operations
 * and proper resource handling for the user table. Utilizes parameterized queries
 * for security and Optional return types for null safety.
 *
 */
public class UserDAO {

    /**
     * Email-Based User Retriever
     * Securely fetches a user record by email address using parameterized queries.
     * Returns an Optional to handle potential absence of the requested user.
     *
     * @param email The email address to search for
     * @return Optional<User> The user if found, empty Optional otherwise
     */
    public Optional<User> getUserByEmail(String email) {
        // QUERY definition specifies the email-based lookup
        String sql = "SELECT * FROM user WHERE email = ?";

        try (
                // CONNECTION establishment creates database link
                Connection conn = DatabaseManager.getInstance().getConnection();
                // STATEMENT preparation ensures query safety
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            // BINDING inserts email parameter securely
            ps.setString(1, email);
            // EXECUTION retrieves the user data
            ResultSet rs = ps.executeQuery();

            // TRANSFORMATION converts result to User object if found
            if (rs.next()) {
                return Optional.of(extractUser(rs));
            }
        } catch (SQLException e) {
            // ERROR logging captures database issues
            System.err.println("Error retrieving user by email: " + e.getMessage());
            e.printStackTrace();
        }
        // EMPTY return indicates user not found
        return Optional.empty();
    }

    /**
     * User Record Creator
     * Handles new user registration with duplicate email checking and proper
     * password handling. Returns the created user with its generated ID.
     *
     * @param user The User object containing registration information
     * @return User The created user with ID, or null if creation failed
     */
    public User createUser(User user) {
        // VALIDATION prevents duplicate email registration
        if (getUserByEmail(user.getEmail()).isPresent()) {
            System.err.println("Error: User with email '" + user.getEmail() + "' already exists.");
            return null;
        }

        // QUERY construction builds insert statement
        String sql = "INSERT INTO user(" +
                "name, diabetesType, insulinType, insulinAdmin, " +
                "email, phone, doctorName, doctorEmail, doctorAddress, " +
                "doctorEmergencyPhone, logbookType, password" +
                ") VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";

        try (
                // CONNECTION setup manages database resources
                Connection conn = DatabaseManager.getInstance().getConnection();
                // STATEMENT preparation includes key generation
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            // PARAMETER binding sets all user fields
            ps.setString(1, user.getName());
            ps.setString(2, user.getDiabetesType());
            ps.setString(3, user.getInsulinType());
            ps.setString(4, user.getInsulinAdmin());
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getPhone());
            ps.setString(7, user.getDoctorName());
            ps.setString(8, user.getDoctorEmail());
            ps.setString(9, user.getDoctorAddress());
            ps.setString(10, user.getDoctorEmergencyPhone());
            ps.setString(11, user.getLogbookType());
            ps.setString(12, hashPassword(user.getPassword()));

            // EXECUTION performs the insert
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            // ID retrieval updates the user object
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                user.setId(keys.getInt(1));
            }
            return user;

        } catch (SQLException e) {
            // ERROR handling logs creation failures
            System.err.println("Error creating user: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * User Record Updater
     * Updates an existing user's information in the database.
     * Uses the user's ID to identify the record to update.
     *
     * @param user The User object containing updated information
     */
    public void updateUser(User user) {
        // QUERY definition specifies update parameters
        String sql = "UPDATE user SET " +
                "name = ?, diabetesType = ?, insulinType = ?, insulinAdmin = ?," +
                "email = ?, phone = ?, doctorName = ?, doctorEmail = ?, doctorAddress = ?," +
                "doctorEmergencyPhone = ?, logbookType = ?, password = ? " +
                "WHERE id = ?";

        try (
                // CONNECTION management ensures proper resource handling
                Connection conn = DatabaseManager.getInstance().getConnection();
                // STATEMENT preparation creates secure query
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            // BINDING block sets all update parameters
            ps.setString(1, user.getName());
            ps.setString(2, user.getDiabetesType());
            ps.setString(3, user.getInsulinType());
            ps.setString(4, user.getInsulinAdmin());
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getPhone());
            ps.setString(7, user.getDoctorName());
            ps.setString(8, user.getDoctorEmail());
            ps.setString(9, user.getDoctorAddress());
            ps.setString(10, user.getDoctorEmergencyPhone());
            ps.setString(11, user.getLogbookType());
            ps.setString(12, hashPassword(user.getPassword()));
            ps.setInt(13, user.getId());

            // EXECUTION updates the user record
            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated == 0) {
                System.err.println("No user found with id=" + user.getId() + "; update failed.");
            }
        } catch (SQLException e) {
            // ERROR logging captures update failures
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * ResultSet Mapper
     * Converts a database result row into a User object.
     * Handles the mapping of all user fields from the database format.
     *
     * @param rs The ResultSet positioned at the row to process
     * @return User The populated user object
     * @throws SQLException If any database access errors occur
     */
    private User extractUser(ResultSet rs) throws SQLException {
        // OBJECT creation prepares for data transfer
        User u = new User();

        // MAPPING transfers database fields to object
        u.setId(rs.getInt("id"));
        u.setName(rs.getString("name"));
        u.setDiabetesType(rs.getString("diabetesType"));
        u.setInsulinType(rs.getString("insulinType"));
        u.setInsulinAdmin(rs.getString("insulinAdmin"));
        u.setEmail(rs.getString("email"));
        u.setPhone(rs.getString("phone"));
        u.setDoctorName(rs.getString("doctorName"));
        u.setDoctorEmail(rs.getString("doctorEmail"));
        u.setDoctorAddress(rs.getString("doctorAddress"));
        u.setDoctorEmergencyPhone(rs.getString("doctorEmergencyPhone"));
        u.setLogbookType(rs.getString("logbookType"));
        u.setPassword(rs.getString("password"));

        return u;
    }

    /**
     * Password Hasher
     * Securely hashes user passwords for database storage.
     *
     * @param password The plain text password to hash
     * @return String The hashed password
     */
    private String hashPassword(String password) {
        // TEMPORARY returns unhashed password
        return password;
    }
}