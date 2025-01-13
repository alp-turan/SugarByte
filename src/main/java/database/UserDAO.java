package database;

import model.User;

import java.sql.*;
import java.util.Optional;

public class UserDAO {

    /**
     * Retrieve a user by email, if present.
     */
    public Optional<User> getUserByEmail(String email) {
        String sql = "SELECT * FROM user WHERE email = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(extractUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user by email: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Insert a new user record.
     */
    public User createUser(User user) {
        // Check if the email already exists
        if (getUserByEmail(user.getEmail()).isPresent()) {
            System.err.println("Error: User with email '" + user.getEmail() + "' already exists.");
            return null;
        }

        String sql = "INSERT INTO user(" +
                "name, diabetesType, insulinType, insulinAdmin, " +
                "email, phone, doctorName, doctorEmail, doctorAddress, " +
                "doctorEmergencyPhone, logbookType, password" +
                ") VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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
            ps.setString(12, hashPassword(user.getPassword())); // Hash the password

            System.out.println("Executing SQL: " + ps.toString());
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Inserting user failed, no rows affected.");
            }

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                user.setId(keys.getInt(1));
            }
            return user;

        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Update an existing user in the database.
     * Relies on the user's 'id' field to identify which row to update.
     */
    public void updateUser(User user) {
        String sql = "UPDATE user SET " +
                "name = ?, diabetesType = ?, insulinType = ?, insulinAdmin = ?," +
                "email = ?, phone = ?, doctorName = ?, doctorEmail = ?, doctorAddress = ?," +
                "doctorEmergencyPhone = ?, logbookType = ?, password = ? " +
                "WHERE id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

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
            // Hash again in case the password changed
            ps.setString(12, hashPassword(user.getPassword()));
            ps.setInt(13, user.getId());

            System.out.println("Executing SQL: " + ps.toString());
            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated == 0) {
                System.err.println("No user found with id=" + user.getId() + "; update failed.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Extract a User object from the ResultSet row.
     */
    private User extractUser(ResultSet rs) throws SQLException {
        User u = new User();
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
        // This might already be hashed in DB
        u.setPassword(rs.getString("password"));
        return u;
    }

    /**
     * Hash the user's password using a secure algorithm (e.g., BCrypt).
     */
    private String hashPassword(String password) {
        // Use BCrypt or another hashing library to securely hash passwords
        // e.g.: return BCrypt.hashpw(password, BCrypt.gensalt());
        // For demonstration, returning as-is:
        return password;
    }
}
