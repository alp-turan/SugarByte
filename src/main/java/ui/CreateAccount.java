package ui;

import database.UserDAO;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


public class CreateAccount extends BaseUI {

    private JTextField nameField;
    private JComboBox<String> diabetesCombo;
    private JComboBox<String> insulinTypeCombo;
    private JComboBox<String> insulinAdminCombo;
    private JTextField emailField;
    private JTextField phoneField;

    private JTextField doctorNameField;
    private JTextField doctorEmailField;
    private JTextField doctorAddressField;
    private JTextField doctorEmergencyField;

    private JComboBox<String> logbookTypeCombo;
    private JPasswordField passwordField;

    /**
     * Constructs the "Create Account" screen, allowing users to input their details
     * and create an account for the SugarByte application.
     */
    public CreateAccount() {
        super("Create Account"); // Sets the title of the JFrame to "Create Account"

        // Main panel with a gradient background
        JPanel mainPanel = createGradientPanel(Color.WHITE, new Color(240, 240, 240)); // Background transition from white to light gray
        mainPanel.setLayout(new GridBagLayout()); // Uses GridBagLayout for flexible component placement
        setContentPane(mainPanel); // Sets this panel as the main content area of the JFrame

        // Initialize layout constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Uniform padding around components
        gbc.fill = GridBagConstraints.HORIZONTAL; // Stretch components horizontally
        gbc.gridx = 0; // Start at the first column

        // ===== Title =====
        JLabel titleLabel = createTitleLabel("SugarByte", lobsterFont, Color.BLACK); // Title with custom font and black color
        gbc.gridy = 0; // Row position for the title
        gbc.gridwidth = 2; // Span across two columns
        gbc.insets = new Insets(0, 0, 40, 0); // Add extra bottom padding for spacing
        mainPanel.add(titleLabel, gbc); // Add the title to the main panel

        gbc.gridwidth = 1; // Reset column span
        gbc.insets = new Insets(3, 5, 3, 5); // Reset uniform padding for other components

        // ===== Back Button =====
        RoundedButton backBtn = new RoundedButton("Back", new Color(237, 165, 170)); // Button with rounded corners and pink background
        backBtn.setForeground(Color.BLACK); // Black text for the button
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 14)); // Bold, sans-serif font
        backBtn.setPreferredSize(new Dimension(50, 35)); // Smaller button size
        gbc.gridy = 1; // Place above input fields
        gbc.gridwidth = 2; // Span across two columns
        gbc.insets = new Insets(5, 0, 30, 200); // Position towards the left
        mainPanel.add(backBtn, gbc); // Add the back button to the main panel

        // Action listener for the back button
        backBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the current window
                new Login(); // Navigate back to the login screen
            }
        });

        gbc.gridwidth = 1; // Reset column span
        gbc.insets = new Insets(3, 5, 3, 5); // Reset uniform padding for other components

        // ===== Input Fields =====
        int row = 2; // Start input fields from the second row
        row = addLabelAndField(mainPanel, gbc, row, "Full Name:", nameField = new JTextField(15)); // Full name input
        row = addLabelAndField(mainPanel, gbc, row, "Type of Diabetes:", diabetesCombo = createDiabetesDropdown()); // Diabetes type dropdown
        row = addLabelAndField(mainPanel, gbc, row, "Type of Insulin:", insulinTypeCombo = createInsulinTypeDropdown()); // Insulin type dropdown
        row = addLabelAndField(mainPanel, gbc, row, "Insulin Admin:", insulinAdminCombo = createInsulinAdminDropdown()); // Insulin administration dropdown
        row = addLabelAndField(mainPanel, gbc, row, "Email:", emailField = new JTextField(15)); // Email input field
        row = addLabelAndField(mainPanel, gbc, row, "Phone:", phoneField = new JTextField(15)); // Phone number input field

        row = addLabelAndField(mainPanel, gbc, row, "Doctor's Name:", doctorNameField = new JTextField(15)); // Doctor's name input
        row = addLabelAndField(mainPanel, gbc, row, "Doctor Email:", doctorEmailField = new JTextField(15)); // Doctor's email input
        row = addLabelAndField(mainPanel, gbc, row, "Doctor Address:", doctorAddressField = new JTextField(15)); // Doctor's address input
        row = addLabelAndField(mainPanel, gbc, row, "Doctor Emergency:", doctorEmergencyField = new JTextField(15)); // Doctor's emergency contact input
        row = addLabelAndField(mainPanel, gbc, row, "Logbook Type:", logbookTypeCombo = createLogbookDropdown()); // Logbook type dropdown
        row = addLabelAndField(mainPanel, gbc, row, "Password:", passwordField = new JPasswordField(15)); // Password input field

        // ===== Create Account Button =====
        gbc.gridy = row; // Place the button below the input fields
        gbc.gridx = 0; // Align to the first column
        gbc.gridwidth = 2; // Span across two columns
        gbc.insets = new Insets(35, 65, 15, 65); // Add spacing around the button

        RoundedButton createBtn = new RoundedButton("Create Account", new Color(220, 53, 69)); // Button with red background
        createBtn.setForeground(Color.WHITE); // White text for the button
        createBtn.setFont(new Font("SansSerif", Font.BOLD, 14)); // Bold, sans-serif font
        createBtn.setPreferredSize(new Dimension(120, 40)); // Adjusted button size
        mainPanel.add(createBtn, gbc); // Add the create account button to the main panel

        // Action listener for the create account button
        createBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleCreate(); // Handle account creation
            }
        });

        // ===== Validation for Name Fields =====
        // Restrict name field to accept only letters and spaces
        // AI was used for the syntax of the if loop
        nameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (!Character.isLetter(e.getKeyChar()) && e.getKeyChar() != ' ') {
                    e.consume(); // Ignore non-letter characters
                }
            }
        });

        // Restrict doctor's name field to accept only letters and spaces
        // AI was used for the syntax of the if loop
        doctorNameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (!Character.isLetter(e.getKeyChar()) && e.getKeyChar() != ' ') {
                    e.consume(); // Ignore non-letter characters
                }
            }
        });


        // Add validation to ensure the phone number field accepts only numeric input
        phoneField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                // If the entered character is not a digit, ignore it
                if (!Character.isDigit(e.getKeyChar())) {
                    e.consume(); // Prevent non-numeric characters from being added
                }
            }
        });

        setVisible(true);// Make the "Create Account" window visible to the user

    }

/**
 * Adds a label and its corresponding input field to the specified panel.
 *
 * @param panel The panel to which the label and field will be added.
 * @param gbc The layout constraints used to position components within the panel.
 * @param row The current row in the GridBagLayout.
 * @param labelText The text for the label.
 * @param field The input field associated with the label.
 * @return The updated row index to position the next component.
 */
        private int addLabelAndField (JPanel panel, GridBagConstraints gbc,int row, String labelText, JComponent field){
            gbc.gridy = row; // Set the vertical position for the label
            gbc.gridx = 0; // Set the column position for the label
            panel.add(new JLabel(labelText), gbc); // Add the label to the panel

            gbc.gridx = 1; // Set the column position for the input field
            panel.add(field, gbc); // Add the field to the panel

            return row + 1; // Return the next row index
        }

/**
 * Creates a dropdown menu for selecting the type of diabetes.
 *
 * @return A JComboBox populated with options for diabetes types.
 */
        private JComboBox<String> createDiabetesDropdown () {
            String[] options = {"Type 1", "Type 2"}; // Available options for diabetes types
            return new JComboBox<>(options); // Create and return the dropdown menu
        }

/**
 * Creates a dropdown menu for selecting the type of insulin.
 *
 * @return A JComboBox populated with options for insulin types.
 */
        private JComboBox<String> createInsulinTypeDropdown () {
            String[] options = {"Rapid-acting", "Short-acting", "Intermediate-acting", "Long-acting"}; // Insulin type options
            return new JComboBox<>(options); // Create and return the dropdown menu
        }

/**
 * Creates a dropdown menu for selecting the method of insulin administration.
 *
 * @return A JComboBox populated with options for insulin administration methods.
 */
        private JComboBox<String> createInsulinAdminDropdown () {
            String[] options = {"Pen", "Pump", "Injection"}; // Insulin administration method options
            return new JComboBox<>(options); // Create and return the dropdown menu
        }

/**
 * Creates a dropdown menu for selecting the logbook type.
 *
 * @return A JComboBox populated with options for logbook types.
 */
        private JComboBox<String> createLogbookDropdown () {
            String[] options = {"Simple", "Comprehensive", "Intensive"}; // Logbook type options
            return new JComboBox<>(options); // Create and return the dropdown menu
        }

/**
 * Handles the process of creating a new user account.
 * Validates user input and stores the new account details in the database if valid.
 */
        private void handleCreate () {
            // Retrieve input from user fields
            String name = nameField.getText().trim();
            String diabetes = (String) diabetesCombo.getSelectedItem();
            String insulinType = (String) insulinTypeCombo.getSelectedItem();
            String insulinAdmin = (String) insulinAdminCombo.getSelectedItem();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();

            // Retrieve additional input for doctor's information and preferences
            String doctorName = doctorNameField.getText().trim();
            String docEmail = doctorEmailField.getText().trim();
            String docAddress = doctorAddressField.getText().trim();
            String docEmerg = doctorEmergencyField.getText().trim();
            String logbookType = (String) logbookTypeCombo.getSelectedItem(); // Logbook type selection
            String pass = new String(passwordField.getPassword()); // Password input

            // ===== Input Validation =====
            // Check if required fields are filled
            if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || doctorName.isEmpty() || docEmail.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Name, Email, Password, Doctor's Name, and Doctor's Email are required!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return; // Stop further processing if validation fails
            }

            // Validate the user's email address
            if (!isValidEmail(email)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid email address!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Validate the doctor's email address
            if (!isValidEmail(docEmail)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid doctor's email address!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Validate the password's strength
            if (!isValidPassword(pass)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Password must be at least 8 characters long and include both letters and numbers.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // ===== User Account Creation =====
            // Construct a new User object with the collected input
            User u = new User();
            u.setName(name); // Set the user's name
            u.setDiabetesType(diabetes); // Set the user's diabetes type
            u.setInsulinType(insulinType); // Set the user's insulin type
            u.setInsulinAdmin(insulinAdmin); // Set the method of insulin administration
            u.setEmail(email); // Set the user's email
            u.setPhone(phone); // Set the user's phone number
            u.setDoctorName(doctorName); // Set the doctor's name
            u.setDoctorEmail(docEmail); // Set the doctor's email
            u.setDoctorAddress(docAddress); // Set the doctor's address
            u.setDoctorEmergencyPhone(docEmerg); // Set the doctor's emergency phone number
            u.setLogbookType(logbookType); // Set the user's logbook type preference
            u.setPassword(pass); // Set the user's password

            // Insert the user into the database
            UserDAO dao = new UserDAO();
            User created = dao.createUser(u); // Create the user in the database

            if (created != null && created.getId() > 0) {
                // Notify the user of successful account creation
                JOptionPane.showMessageDialog(
                        this,
                        "Account created successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
                dispose(); // Close the current window
                new Home(created); // Navigate to the home screen
            } else {
                // Notify the user of account creation failure
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to create account. Try a different email or check DB logs.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }

/**
 * Validates an email address using a regular expression pattern.
 *
 * @param email The email address to validate.
 * @return True if the email matches the pattern, false otherwise.
 * -- AI (ChatGPT) was used for this block as we didn't know how to validate emails ourselves, but as it's less
 *  than 6 lines, it hasn't been referenced properly --
 */
        private boolean isValidEmail (String email){
            String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$"; // Regular expression for basic email validation
            return email.matches(emailRegex); // Check if the email matches the regex pattern
        }

/**
 * Validates a password for length and content.
 * A valid password must be at least 8 characters long and contain both letters and digits.
 *
 * @param password The password to validate.
 * @return True if the password meets the criteria, false otherwise.
 */
/* reference - inspiration for this entire block was taken from https://stackoverflow.com/questions/24924321/how-can-i-perform-validation-on-a-secure-password-regular-expressions-on-a-char */
        private boolean isValidPassword (String password){
            if (password.length() < 8) return false; // Ensure the password is at least 8 characters long
            boolean hasLetter = false; // Flag for presence of letters
            boolean hasDigit = false; // Flag for presence of digits

            // Iterate over each character in the password
            for (char c : password.toCharArray()) {
                if (Character.isLetter(c)) hasLetter = true; // Check for letters
                else if (Character.isDigit(c)) hasDigit = true; // Check for digits

                // If both letters and digits are present, the password is valid
                if (hasLetter && hasDigit) {
                    return true;
                }
            }
            return false; // Return false if either condition is not met
        }
        /* end of reference */
    }
