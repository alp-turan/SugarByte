package ui;

import database.UserDAO;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A merged CreateAccount class that combines:
 *  - Comboboxes for Diabetes, Insulin Type, Insulin Admin, Logbook Type
 *  - Doctor's full name field
 *  - Advanced email & password validations
 *  - Basic required-field checks (name, email, password, docName)
 */
public class CreateAccount extends BaseUI {

    private JTextField nameField;
    private JComboBox<String> diabetesCombo;
    private JComboBox<String> insulinTypeCombo;
    private JComboBox<String> insulinAdminCombo;
    private JTextField emailField;
    private JTextField phoneField;

    private JTextField doctorNameField;      // Additional doctor name
    private JTextField doctorEmailField;
    private JTextField doctorAddressField;
    private JTextField doctorEmergencyField;

    private JComboBox<String> logbookTypeCombo;  // e.g., "Simple", "Comprehensive", etc.
    private JPasswordField passwordField;

    public CreateAccount() {
        super("Create Account");

        // Main gradient panel
        JPanel mainPanel = createGradientPanel(Color.WHITE, new Color(240, 240, 240));
        mainPanel.setLayout(new GridBagLayout());
        setContentPane(mainPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // Title
        JLabel titleLabel = createTitleLabel("SugarByte", lobsterFont, Color.BLACK);
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 120, 0);
        mainPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(3, 5, 3, 5);

        int row = 1;
        row = addLabelAndField(mainPanel, gbc, row, "Full Name:",         nameField           = new JTextField(15));
        row = addLabelAndField(mainPanel, gbc, row, "Type of Diabetes:",  diabetesCombo       = createDiabetesDropdown());
        row = addLabelAndField(mainPanel, gbc, row, "Type of Insulin:",   insulinTypeCombo    = createInsulinTypeDropdown());
        row = addLabelAndField(mainPanel, gbc, row, "Insulin Admin:",     insulinAdminCombo   = createInsulinAdminDropdown());
        row = addLabelAndField(mainPanel, gbc, row, "Email:",             emailField          = new JTextField(15));
        row = addLabelAndField(mainPanel, gbc, row, "Phone:",             phoneField          = new JTextField(15));

        // Additional field from first version
        row = addLabelAndField(mainPanel, gbc, row, "Doctor's Full Name:", doctorNameField    = new JTextField(15));

        row = addLabelAndField(mainPanel, gbc, row, "Doctor Email:",      doctorEmailField    = new JTextField(15));
        row = addLabelAndField(mainPanel, gbc, row, "Doctor Address:",    doctorAddressField  = new JTextField(15));
        row = addLabelAndField(mainPanel, gbc, row, "Doctor Emergency:",  doctorEmergencyField= new JTextField(15));

        // Logbook type from first version
        row = addLabelAndField(mainPanel, gbc, row, "Logbook Type:",      logbookTypeCombo    = createLogbookDropdown());

        row = addLabelAndField(mainPanel, gbc, row, "Password:",          passwordField       = new JPasswordField(15));

        // Create Button
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 0, 15, 0);
        JButton createBtn = new JButton("Create Account");
        createBtn.setBackground(new Color(237, 165, 170));
        createBtn.setForeground(Color.BLACK);
        mainPanel.add(createBtn, gbc);

        createBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleCreate();
            }
        });

        setVisible(true);
        addBackButton();

    }

    /**
     * A helper method to add a (label, field) row to our GridBag layout.
     */
    private int addLabelAndField(JPanel panel, GridBagConstraints gbc, int row,
                                 String labelText, JComponent field) {
        gbc.gridy = row;
        gbc.gridx = 0;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
        return row + 1;
    }

    /**
     * Creates a combo box for diabetes types (Type 1, Type 2).
     */
    private JComboBox<String> createDiabetesDropdown() {
        String[] options = {"Type 1", "Type 2"};
        return new JComboBox<>(options);
    }

    /**
     * Creates a combo box for insulin type (Rapid-acting, short, etc.).
     */
    private JComboBox<String> createInsulinTypeDropdown() {
        String[] options = {"Rapid-acting", "Short-acting", "Intermediate-acting", "Long-acting"};
        return new JComboBox<>(options);
    }

    /**
     * Creates a combo box for insulin administration (Pen, Pump, Injection).
     */
    private JComboBox<String> createInsulinAdminDropdown() {
        String[] options = {"Pen", "Pump", "Injection"};
        return new JComboBox<>(options);
    }

    /**
     * Creates a combo box for logbook type (Simple, Comprehensive, Intensive).
     */
    private JComboBox<String> createLogbookDropdown() {
        String[] options = {"Simple", "Comprehensive", "Intensive"};
        return new JComboBox<>(options);
    }

    /**
     * The main logic for creating an account when the user hits "Create Account".
     */
    private void handleCreate() {
        String name         = nameField.getText().trim();
        String diabetes     = (String) diabetesCombo.getSelectedItem();
        String insulinType  = (String) insulinTypeCombo.getSelectedItem();
        String insulinAdmin = (String) insulinAdminCombo.getSelectedItem();
        String email        = emailField.getText().trim();
        String phone        = phoneField.getText().trim();

        // Additional fields
        String doctorName   = doctorNameField.getText().trim();
        String docEmail     = doctorEmailField.getText().trim();
        String docAddress   = doctorAddressField.getText().trim();
        String docEmerg     = doctorEmergencyField.getText().trim();
        String logbookType  = (String) logbookTypeCombo.getSelectedItem(); // "Simple", "Comprehensive", "Intensive"

        String pass         = new String(passwordField.getPassword());

        // Basic required checks
        if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || doctorName.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Name, Email, Password, and Doctor's Name are required!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Advanced email check
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid email address!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Advanced password check
        if (!isValidPassword(pass)) {
            JOptionPane.showMessageDialog(this,
                    "Password must be at least 8 characters long and include both letters and numbers.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Construct the user object
        User u = new User();
        u.setName(name);
        u.setDiabetesType(diabetes);
        u.setInsulinType(insulinType);
        u.setInsulinAdmin(insulinAdmin);
        u.setEmail(email);
        u.setPhone(phone);

        // Doctor info
        u.setDoctorName(doctorName);
        u.setDoctorEmail(docEmail);
        u.setDoctorAddress(docAddress);
        u.setDoctorEmergencyPhone(docEmerg);

        // *** Save logbook choice in DB ***
        u.setLogbookType(logbookType);

        u.setPassword(pass);

        // Insert into DB
        UserDAO dao = new UserDAO();
        User created = dao.createUser(u);
        if (created != null && created.getId() > 0) {
            JOptionPane.showMessageDialog(this,
                    "Account created successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
            dispose();
            new Home(created);
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to create account. Try a different email or check DB logs.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Simple email validation (regex-based).
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    /**
     * Ensures password has at least 8 chars, contains letters and digits.
     */
    private boolean isValidPassword(String password) {
        if (password.length() < 8) return false;
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            else if (Character.isDigit(c)) hasDigit = true;
            if (hasLetter && hasDigit) {
                return true;
            }
        }
        return false;
    }
}