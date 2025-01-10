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

    // In Profile class
    private void buildUI() {
        // Add padding around the main content
        ((JPanel)getContentPane()).setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        // Create a scrollable panel for the content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // Add spacing between title and content
        contentPanel.add(Box.createVerticalStrut(60));  // Space for title and logout

        // Add the panels with proper spacing
        contentPanel.add(createUserInfoPanel());
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createDoctorInfoPanel());
        contentPanel.add(Box.createVerticalStrut(20));

        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        add(scrollPane, BorderLayout.CENTER);
    }
    private JPanel createUserInfoPanel() {
        JPanel panel = new RoundedPanel(20, 5);  // Rounded corners with slight shadow
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 245));  // Light gray background
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("User's Information"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 15);  // Add space between label and field

        // Add full name field
        addFormField(panel, gbc, "Full Name:", nameField);
        gbc.gridy++;
        addFormField(panel, gbc, "Email:", emailField);
        gbc.gridy++;
        addFormField(panel, gbc, "Type of Diabetes:", diabetesCombo);
        gbc.gridy++;
        addFormField(panel, gbc, "Type of Insulin:", insulinTypeCombo);
        gbc.gridy++;
        addFormField(panel, gbc, "Insulin Administration:", insulinAdminCombo);
        gbc.gridy++;
        addFormField(panel, gbc, "Phone Number:", phoneField);
        gbc.gridy++;
        addFormField(panel, gbc, "Password:", passwordField);
        gbc.gridy++;
        addFormField(panel, gbc, "Logbook Type:", logbookTypeCombo);

        return panel;
    }

    private JPanel createDoctorInfoPanel() {
        JPanel panel = new RoundedPanel(20, 5);
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Doctor's Information"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 15);

        addFormField(panel, gbc, "Doctor's Name:", doctorNameField);
        gbc.gridy++;
        addFormField(panel, gbc, "Doctor Email:", doctorEmailField);
        gbc.gridy++;
        addFormField(panel, gbc, "Address:", doctorAddressField);
        gbc.gridy++;
        addFormField(panel, gbc, "Emergency Phone:", doctorEmergencyField);

        return panel;
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        panel.add(label, gbc);

        GridBagConstraints fieldGbc = (GridBagConstraints) gbc.clone();
        fieldGbc.gridx = 1;
        fieldGbc.fill = GridBagConstraints.HORIZONTAL;
        fieldGbc.weightx = 1.0;
        panel.add(field, fieldGbc);
    }

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
        addLogoutButton();
    }

    /**
     * Extracts all updated data from fields, sets them into currentUser,
     * then calls UserDAO.updateUser(...) to persist changes.
     */
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
}
