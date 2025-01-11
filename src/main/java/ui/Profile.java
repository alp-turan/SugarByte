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
    private JTextField doctorNameField;
    private JTextField doctorEmailField;
    private JTextField doctorAddressField;
    private JTextField doctorEmergencyField;

    // Field for logbook type
    private JComboBox<String> logbookTypeCombo;

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

        // Next row: Email
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
            passwordField.setText("password123");
        }
        userPanel.add(passwordField, gbc);

        // Next row: Logbook Type
        gbc.gridy++;
        gbc.gridx = 0;
        userPanel.add(new JLabel("Logbook Type:"), gbc);
        gbc.gridx = 1;
        logbookTypeCombo = new JComboBox<>(new String[]{"Simple", "Comprehensive", "Intensive"});
        if (currentUser.getLogbookType() != null) {
            logbookTypeCombo.setSelectedItem(currentUser.getLogbookType());
        }
        userPanel.add(logbookTypeCombo, gbc);

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

        // =============== BUTTON PANEL (Save and Logout buttons) ===============
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));  // Center both buttons
        buttonsPanel.setOpaque(false);

// Save Button with RoundedButton (using the same constructor as "Generate graph" button)
        RoundedButton saveBtn = new RoundedButton("Save Changes", new Color(237, 165, 170));  // Light Pink Color
        saveBtn.setForeground(Color.BLACK);  // Set text color to black
        saveBtn.setFont(new Font("SansSerif", Font.BOLD, 14));  // Set the font style and size

// Set preferred size for the button
        saveBtn.setPreferredSize(new Dimension(140, 35)); // Consistent size for the button

// Add ActionListener to handle save logic
        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSaveChanges();  // Call the method to save the changes
            }
        });

// Logout Button (using RoundedButton)
        RoundedButton logoutButton = new RoundedButton("Logout", new Color(220, 53, 69)) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(100, 35); // Consistent size
            }
        };

        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("SansSerif", Font.BOLD, 14));

        logoutButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logoutButton.setBackground(new Color(200, 35, 51)); // Darker red on hover
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                logoutButton.setBackground(new Color(220, 53, 69)); // Return to original color
            }
        });

        logoutButton.addActionListener(e -> handleLogout());

// Add buttons to the panel with increased spacing
        buttonsPanel.add(saveBtn);
        buttonsPanel.add(Box.createHorizontalStrut(20));  // Adds more space between buttons
        buttonsPanel.add(logoutButton);

// Add the button panel to the center section of the layout
        centerPanel.add(buttonsPanel);
        centerPanel.add(Box.createVerticalStrut(10));


        // Bottom Nav
        JPanel navBar = createBottomNavBar(
                "Profile", currentUser,
                "/Icons/home.png", "/Icons/logbook.png", "/Icons/graph.png", "/Icons/profilefull.png"
        );
        mainPanel.add(navBar, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Handle save changes
    private void handleSaveChanges() {
        if (nameField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty() ||
                passwordField.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this,
                    "Name, Email, and Password cannot be empty!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

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

        currentUser.setLogbookType((String) logbookTypeCombo.getSelectedItem());

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

    private String safeValue(String value, String defaultValue) {
        return value != null && !value.isEmpty() ? value : defaultValue;
    }
    private void handleLogout() {
        // Create a custom confirmation dialog with styled buttons
        int result = createCustomConfirmDialog(
                "Logout Confirmation",
                "Are you sure you want to log out?",
                new Color(220, 53, 69)  // Match logout button color
        );

        if (result == JOptionPane.YES_OPTION) {
            dispose();
            new Login();
        }
    }
}
