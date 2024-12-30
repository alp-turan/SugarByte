package ui;

import database.UserDAO;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * An editable Profile screen that lets the user view and change
 * personal/doctor information, then save it to the database.
 */
public class Profile extends BaseUI {

    // Reference to the current user
    private User currentUser;

    // Fields for user’s information
    private JTextField nameField;
    private JTextField emailField;
    private JComboBox<String> diabetesCombo;
    private JComboBox<String> insulinTypeCombo;
    private JComboBox<String> insulinAdminCombo;
    private JTextField phoneField;
    private JPasswordField passwordField;

    // Fields for doctor’s information
    private JTextField doctorNameField;     // from first version
    private JTextField doctorEmailField;
    private JTextField doctorAddressField;
    private JTextField doctorEmergencyField;

    public Profile(User user) {
        super("Profile");
        this.currentUser = user;

        // Main gradient background
        JPanel mainPanel = createGradientPanel(Color.WHITE, Color.WHITE);
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);

        // Fonts
        Font lobsterFont = loadCustomFont(38f);
        Font poppinsBold = new Font("SansSerif", Font.BOLD, 16);
        Font poppinsNormal = new Font("SansSerif", Font.PLAIN, 14);

        // Title
        JLabel titleLabel = createTitleLabel("SugarByte", lobsterFont, Color.BLACK);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Center Panel (BoxLayout: vertical stacking)
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // =============== USER INFO PANEL ===============
        JPanel userPanel = new JPanel(new GridBagLayout());
        userPanel.setOpaque(false);
        userPanel.setBorder(BorderFactory.createTitledBorder("User’s Information"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // "Full Name"
        userPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(15);
        nameField.setText(safeValue(currentUser.getName(), "Name"));
        userPanel.add(nameField, gbc);

        // Next row
        gbc.gridy++;
        gbc.gridx = 0;
        userPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(15);
        emailField.setText(safeValue(currentUser.getEmail(), "email@example.com"));
        userPanel.add(emailField, gbc);

        // Next row: Type of Diabetes
        gbc.gridy++;
        gbc.gridx = 0;
        userPanel.add(new JLabel("Type of Diabetes:"), gbc);
        gbc.gridx = 1;
        diabetesCombo = new JComboBox<>(new String[]{"Type 1", "Type 2"});
        // pre-select user’s current value if not null
        if (currentUser.getDiabetesType() != null) {
            diabetesCombo.setSelectedItem(currentUser.getDiabetesType());
        }
        userPanel.add(diabetesCombo, gbc);

        // Next row: Type of Insulin
        gbc.gridy++;
        gbc.gridx = 0;
        userPanel.add(new JLabel("Type of Insulin:"), gbc);
        gbc.gridx = 1;
        insulinTypeCombo = new JComboBox<>(new String[]{
                "Rapid-acting", "Short-acting", "Intermediate-acting", "Long-acting"
        });
        if (currentUser.getInsulinType() != null) {
            insulinTypeCombo.setSelectedItem(currentUser.getInsulinType());
        }
        userPanel.add(insulinTypeCombo, gbc);

        // Next row: Insulin Admin
        gbc.gridy++;
        gbc.gridx = 0;
        userPanel.add(new JLabel("Insulin Administration:"), gbc);
        gbc.gridx = 1;
        insulinAdminCombo = new JComboBox<>(new String[]{"Pen", "Pump", "Injection"});
        if (currentUser.getInsulinAdmin() != null) {
            insulinAdminCombo.setSelectedItem(currentUser.getInsulinAdmin());
        }
        userPanel.add(insulinAdminCombo, gbc);

        // Next row: Phone
        gbc.gridy++;
        gbc.gridx = 0;
        userPanel.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(15);
        phoneField.setText(safeValue(currentUser.getPhone(), "07498375960"));
        userPanel.add(phoneField, gbc);

        // Next row: Password
        gbc.gridy++;
        gbc.gridx = 0;
        userPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        if (currentUser.getPassword() != null) {
            passwordField.setText(currentUser.getPassword());
        } else {
            passwordField.setText("password123");  // placeholder
        }
        userPanel.add(passwordField, gbc);

        centerPanel.add(userPanel);

        centerPanel.add(Box.createVerticalStrut(20));

        // =============== DOCTOR INFO PANEL ===============
        JPanel doctorPanel = new JPanel(new GridBagLayout());
        doctorPanel.setOpaque(false);
        doctorPanel.setBorder(BorderFactory.createTitledBorder("Doctor’s Information"));

        gbc.gridx = 0;
        gbc.gridy = 0;

        // Doctor's Name
        doctorPanel.add(new JLabel("Doctor's Name:"), gbc);
        gbc.gridx = 1;
        doctorNameField = new JTextField(15);
        doctorNameField.setText(safeValue(currentUser.getDoctorName(), "Doctor Surname"));
        doctorPanel.add(doctorNameField, gbc);

        // Next row: Doctor Email
        gbc.gridy++;
        gbc.gridx = 0;
        doctorPanel.add(new JLabel("Doctor Email:"), gbc);
        gbc.gridx = 1;
        doctorEmailField = new JTextField(15);
        doctorEmailField.setText(safeValue(currentUser.getDoctorEmail(), "doctor@hospital.com"));
        doctorPanel.add(doctorEmailField, gbc);

        // Next row: Address
        gbc.gridy++;
        gbc.gridx = 0;
        doctorPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        doctorAddressField = new JTextField(15);
        doctorAddressField.setText(safeValue(currentUser.getDoctorAddress(), "City, Street, Flat, Postcode"));
        doctorPanel.add(doctorAddressField, gbc);

        // Next row: Emergency Phone
        gbc.gridy++;
        gbc.gridx = 0;
        doctorPanel.add(new JLabel("Emergency Phone:"), gbc);
        gbc.gridx = 1;
        doctorEmergencyField = new JTextField(15);
        doctorEmergencyField.setText(safeValue(currentUser.getDoctorEmergencyPhone(), "07287567281"));
        doctorPanel.add(doctorEmergencyField, gbc);

        centerPanel.add(doctorPanel);

        centerPanel.add(Box.createVerticalStrut(20));

        // =============== SAVE BUTTON ===============
        JButton saveBtn = new JButton("Save Changes");
        saveBtn.setBackground(new Color(237, 165, 170));
        saveBtn.setForeground(Color.BLACK);
        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSaveChanges();
            }
        });
        centerPanel.add(saveBtn);
        centerPanel.add(Box.createVerticalStrut(10));

        // Bottom Nav
        JPanel navBar = createBottomNavBar(
                "Profile", currentUser,
                "/Icons/home.png", "/Icons/logbook.png", "/Icons/profilefull.png"
        );
        mainPanel.add(navBar, BorderLayout.SOUTH);

        setVisible(true);
    }

    /**
     * Extracts all updated data from fields, sets them into currentUser,
     * then calls UserDAO.updateUser(...) to persist changes.
     */
    private void handleSaveChanges() {
        // Basic validations (optional)
        if (nameField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty() ||
                passwordField.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this,
                    "Name, Email, and Password cannot be empty!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Update user object
        currentUser.setName(nameField.getText().trim());
        currentUser.setEmail(emailField.getText().trim());
        currentUser.setDiabetesType((String) diabetesCombo.getSelectedItem());
        currentUser.setInsulinType((String) insulinTypeCombo.getSelectedItem());
        currentUser.setInsulinAdmin((String) insulinAdminCombo.getSelectedItem());
        currentUser.setPhone(phoneField.getText().trim());
        currentUser.setPassword(new String(passwordField.getPassword()));

        currentUser.setDoctorName(doctorNameField.getText().trim());
        currentUser.setDoctorEmail(doctorEmailField.getText().trim());
        currentUser.setDoctorAddress(doctorAddressField.getText().trim());
        currentUser.setDoctorEmergencyPhone(doctorEmergencyField.getText().trim());

        // Now persist to DB
        try {
            UserDAO dao = new UserDAO();
            dao.updateUser(currentUser);
            JOptionPane.showMessageDialog(this,
                    "Profile updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to update profile. Check logs.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Safely returns the user’s existing field or a default placeholder.
     */
    private String safeValue(String fieldValue, String defaultValue) {
        return (fieldValue != null && !fieldValue.trim().isEmpty())
                ? fieldValue
                : defaultValue;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User dummyUser = new User();
            dummyUser.setId(1);  // For testing updates
            dummyUser.setName("Mark");
            dummyUser.setEmail("mark@example.com");
            dummyUser.setDiabetesType("Type 1");
            dummyUser.setInsulinType("Rapid-acting");
            dummyUser.setInsulinAdmin("Pen");
            dummyUser.setPhone("07498375960");
            dummyUser.setPassword("password123");
            dummyUser.setDoctorName("Dr. Roberts");
            dummyUser.setDoctorEmail("doctor@hospital.com");
            dummyUser.setDoctorAddress("123 Clinic Rd, City, XYZ");
            dummyUser.setDoctorEmergencyPhone("07123456789");

            new Profile(dummyUser);
        });
    }
}