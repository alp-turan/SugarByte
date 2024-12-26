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
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Insert a new user record.
     */
    public User createUser(User user) {
        String sql = "INSERT INTO user(" +
                "name, diabetesType, insulinType, insulinAdmin," +
                "email, phone, doctorEmail, doctorAddress, doctorEmergencyPhone," +
                "password) " +
                "VALUES(?,?,?,?,?,?,?,?,?,?)";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getDiabetesType());
            ps.setString(3, user.getInsulinType());
            ps.setString(4, user.getInsulinAdmin());
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getPhone());
            ps.setString(7, user.getDoctorEmail());
            ps.setString(8, user.getDoctorAddress());
            ps.setString(9, user.getDoctorEmergencyPhone());
            ps.setString(10, user.getPassword());

            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                user.setId(keys.getInt(1));
            }
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Update existing user (if you want to allow editing in Profile).
     */
    public void updateUser(User user) {
        String sql = "UPDATE user SET " +
                "name=?, diabetesType=?, insulinType=?, insulinAdmin=?," +
                "email=?, phone=?, doctorEmail=?, doctorAddress=?, doctorEmergencyPhone=?," +
                "password=? WHERE id=?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getDiabetesType());
            ps.setString(3, user.getInsulinType());
            ps.setString(4, user.getInsulinAdmin());
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getPhone());
            ps.setString(7, user.getDoctorEmail());
            ps.setString(8, user.getDoctorAddress());
            ps.setString(9, user.getDoctorEmergencyPhone());
            ps.setString(10, user.getPassword());
            ps.setInt(11, user.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
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
        u.setDoctorEmail(rs.getString("doctorEmail"));
        u.setDoctorAddress(rs.getString("doctorAddress"));
        u.setDoctorEmergencyPhone(rs.getString("doctorEmergencyPhone"));
        u.setPassword(rs.getString("password"));
        return u;
    }
}
