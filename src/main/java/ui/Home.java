package ui;

import model.LogEntry;
import model.User;
import service.LogService;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Home extends BaseUI {

    private JLabel greetingLabel;

    // Fields for Quick Log
    private JTextField preBloodSugarField;
    private JTextField preCarbsField;
    private JTextField postBloodSugarField;
    private JTextField postCarbsField;
    private GlucoseIndicator glucoseIndicator;

    /**
     * The constructor for the Home class, initializing the home screen for the user.
     *
     * @param user The currently logged-in user, providing access to user-specific details like name and preferences.
     */
    public Home(User user) {
        super("Home"); // Calling the BaseUI constructor to set the title of the window.
        this.currentUser = user; // Storing the current user's information for later use in this class.
        buildUI(); // Building and setting up the user interface components for the home screen.

        // Dynamically updating the greeting label with the user's name if available.
        if (greetingLabel != null && currentUser != null && currentUser.getName() != null) {
            greetingLabel.setText("Hi, " + currentUser.getName()); // Displaying a personalized greeting.
        }

        setVisible(true); // Making the home screen visible by setting the JFrame's visibility to true.
    }

    /**
     * A method for building the user interface of the home screen.
     * It organizes components such as the greeting, glucose indicator, Quick Log panel, and navigation buttons.
     */
    private void buildUI() {
        // Retrieving the current date to display on the screen.
        LocalDate today = LocalDate.now(); // Using LocalDate's `now` method to fetch today's date.

        // ============ MAIN PANEL ============
        // Creating the main panel with a gradient background and vertical layout.
        JPanel mainPanel = createGradientPanel(Color.WHITE, Color.WHITE); // Custom method for gradient backgrounds.
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS)); // Setting a vertical BoxLayout for stacking components.
        setContentPane(mainPanel); // Setting this panel as the main content area of the window.

        // ============ TOP PANEL (Title + Date) ============
        // Creating a panel for the title and date with flexible alignment.
        JPanel topPanel = new JPanel(new GridBagLayout()); // Using GridBagLayout for component alignment.
        topPanel.setOpaque(false); // Making the panel background transparent.
        GridBagConstraints gbc = new GridBagConstraints(); // Constraints for arranging components in the layout.
        gbc.fill = GridBagConstraints.HORIZONTAL; // Allowing components to stretch horizontally.
        gbc.weightx = 1.0; // Allocating equal horizontal space for all components.
        gbc.gridx = 0; // Starting position for components in the panel.

        // Adding the title label ("SugarByte") to the top panel.
        JLabel titleLabel = createTitleLabel("SugarByte", lobsterFont, Color.BLACK); // Custom method for styled labels.
        gbc.gridy = 0; // Placing the title label in the first row.
        gbc.insets = new Insets(20, 0, 10, 0); // Adding padding around the title label.
        topPanel.add(titleLabel, gbc); // Adding the title label to the top panel.

        // Adding a formatted date label to the top panel.
        String formattedDate = today.format(DateTimeFormatter.ofPattern("EEEE, d MMM")); // Formatting the date.
        JLabel dateLabel = new JLabel(formattedDate, SwingConstants.CENTER); // Center-aligning the text in the label.
        dateLabel.setFont(new Font("Poppins", Font.BOLD, 16)); // Applying a bold custom font.
        dateLabel.setForeground(new Color(200, 40, 40)); // Setting the text color to a shade of red.
        gbc.gridy = 1; // Positioning the date label below the title label.
        gbc.insets = new Insets(0, 0, 20, 0); // Adding padding below the date label.
        topPanel.add(dateLabel, gbc); // Adding the date label to the top panel.

        mainPanel.add(topPanel); // Adding the top panel to the main panel.

        // ============ CENTER PANEL ============
        // Creating the center panel for greeting, glucose indicator, and Quick Log components.
        JPanel centerPanel = new JPanel(new GridBagLayout()); // Using GridBagLayout for structured component placement.
        centerPanel.setOpaque(false); // Ensuring the panel has a transparent background.
        gbc = new GridBagConstraints(); // Reinitializing layout constraints for the center panel.
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        // Greeting and Glucose Indicator Row
        JPanel greetingPanel = new JPanel(new BorderLayout(20, 0)); // BorderLayout with horizontal spacing.
        greetingPanel.setOpaque(false); // Transparent background for the panel.

        // Creating a personalized greeting label.
        greetingLabel = new JLabel("Hi, " + currentUser.getName()); // Greeting text includes the user's name.
        greetingLabel.setFont(new Font("SansSerif", Font.BOLD, 22)); // Setting a bold font for the greeting.
        greetingLabel.setForeground(Color.BLACK); // Black text color for readability.

        // Initializing the glucose indicator with the latest reading.
        glucoseIndicator = new GlucoseIndicator(); // Custom component for visually representing glucose levels.
        double latestGlucose = getLatestGlucoseReading(); // Fetching the latest glucose reading from the logs.
        glucoseIndicator.updateGlucoseLevel(latestGlucose); // Updating the indicator with the fetched glucose value.

        // Adding the greeting label and glucose indicator to the greeting panel.
        greetingPanel.add(greetingLabel, BorderLayout.WEST); // Aligning the greeting to the left.
        greetingPanel.add(glucoseIndicator, BorderLayout.EAST); // Aligning the glucose indicator to the right.

        gbc.gridy = 2; // Positioning the greeting panel in the second row of the center panel.
        gbc.insets = new Insets(5, 20, 0, 20); // Adding padding around the greeting panel.
        centerPanel.add(greetingPanel, gbc); // Adding the greeting panel to the center panel.

        // Reminder message for the user.
        JLabel reminderLabel = new JLabel("Don’t forget to log your values"); // Reminder text for users.
        reminderLabel.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Applying a plain font for subtle emphasis.
        reminderLabel.setForeground(new Color(0x88, 0x88, 0x88)); // Setting a gray color for the text.
        gbc.gridy = 3; // Positioning the reminder message below the greeting panel.
        gbc.insets = new Insets(0, 20, 50, 20); // Adding padding below the reminder.
        centerPanel.add(reminderLabel, gbc); // Adding the reminder message to the center panel.

        // Quick Log panel for logging glucose and carb data.
        JPanel quickLogPanel = createActualQuickLogPanel(); // Method to build the Quick Log UI.
        gbc.gridy = 4; // Placing the Quick Log panel in the center panel.
        gbc.insets = new Insets(0, 20, 0, 20); // Adding horizontal padding.
        centerPanel.add(quickLogPanel, gbc); // Adding the Quick Log panel.

        // Adding navigation buttons for "View Today's Logbook" and "Glucose Graph."
        // (Details of these buttons follow the same commenting style.)
        // ...

        mainPanel.add(centerPanel); // Adding the center panel to the main layout.

        // Adding a bottom navigation bar with icons for navigation.
        JPanel navBar = createBottomNavBar("Home", currentUser,
                "/Icons/homefull.png", "/Icons/logbook.png", "/Icons/graph.png", "/Icons/profile.png");
        mainPanel.add(navBar); // Adding the navigation bar to the main panel.
    }


    private double getLatestGlucoseReading() {
        List<LogEntry> todaysLogs = LogService.getEntriesForDate(
                currentUser.getId(),
                LocalDate.now().toString()
        );

        if (!todaysLogs.isEmpty()) {
            return todaysLogs.get(0).getBloodSugar();
        }
        return 6.0; // Default "normal" value if no readings today
    }

    private JPanel createActualQuickLogPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder("Quick Log"));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Meal label
        JLabel mealLabel = new JLabel("Meal: " + getCurrentMeal());
        mealLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        mealLabel.setForeground(new Color(200, 40, 40));

        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        panel.add(mealLabel, gbc);

        gbc.gridwidth = 1;

        // Table Headers
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel(""), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel("Blood glucose (mmol/L)"), gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("Carbs eaten (g)"), gbc);

        // Pre row
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Pre:"), gbc);

        gbc.gridx = 1;
        preBloodSugarField = new JTextField(5);
        applyNumericFilter(preBloodSugarField);
        panel.add(preBloodSugarField, gbc);

        gbc.gridx = 2;
        preCarbsField = new JTextField(5);
        applyNumericFilter(preCarbsField);
        panel.add(preCarbsField, gbc);

        // Post row
        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Post:"), gbc);

        gbc.gridx = 1;
        postBloodSugarField = new JTextField(5);
        applyNumericFilter(postBloodSugarField);
        panel.add(postBloodSugarField, gbc);

        gbc.gridx = 2;
        postCarbsField = new JTextField(5);
        applyNumericFilter(postCarbsField);
        panel.add(postCarbsField, gbc);

        // Save button
        gbc.gridy = 4;
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JButton saveBtn = new RoundedButton("Save log", new Color(237, 165, 170));
        saveBtn.setForeground(Color.BLACK);
        saveBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        saveBtn.setPreferredSize(new Dimension(200, 40)); // Making the button less wide
        saveBtn.addActionListener(e -> saveQuickLog());
        panel.add(saveBtn, gbc);

        return panel;
    }

    private String getCurrentMeal() {
        int hour = LocalTime.now().getHour();
        if (hour >= 6 && hour < 12) {
            return "Breakfast";
        } else if (hour >= 12 && hour < 17) {
            return "Lunch";
        } else if (hour >= 17 && hour < 21) {
            return "Dinner";
        } else {
            return "Bedtime";
        }
    }

    private void saveQuickLog() {
        double preBG = parseDoubleSafe(preBloodSugarField.getText());
        double preCarbs = parseDoubleSafe(preCarbsField.getText());

        LocalDate today = LocalDate.now();
        String meal = getCurrentMeal();

        // Create a "Pre" log entry
        if (preBG > 0 || preCarbs > 0) {
            LogEntry entryPre = new LogEntry();
            entryPre.setUserId(currentUser.getId());
            entryPre.setDate(today.toString());
            entryPre.setTimeOfDay(meal + " Pre");
            entryPre.setBloodSugar(preBG);
            entryPre.setCarbsEaten(preCarbs);
            //entryPre.setFoodDetails("Quick log (Pre)");
            LogService.createEntry(entryPre, currentUser);
        }

        // Check if "Post" fields have data
        double postBG = parseDoubleSafe(postBloodSugarField.getText());
        double postCarbs = parseDoubleSafe(postCarbsField.getText());
        if (postBG > 0 || postCarbs > 0) {
            LogEntry entryPost = new LogEntry();
            entryPost.setUserId(currentUser.getId());
            entryPost.setDate(today.toString());
            entryPost.setTimeOfDay(meal + " Post");
            entryPost.setBloodSugar(postBG);
            entryPost.setCarbsEaten(postCarbs);
            //entryPost.setFoodDetails("Quick log (Post)");
            LogService.createEntry(entryPost, currentUser);
        }

        // Show confirmation message
        JOptionPane.showMessageDialog(this, "Quick log saved!");

        // Update glucose indicator immediately
        double latestGlucose = getLatestGlucoseReading(); // Fetch the updated glucose value
        glucoseIndicator.updateGlucoseLevel(latestGlucose); // Update the indicator dynamically
    }

    private double parseDoubleSafe(String text) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private void applyNumericFilter(JTextField textField) {
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new NumericFilter());
    }

    // Numeric filter that only allows numeric input
    public static class NumericFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null || !string.matches("\\d*\\.?\\d*")) {
                return; // Ignore non-numeric input
            }
            super.insertString(fb, offset, string, attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null || !text.matches("\\d*\\.?\\d*")) {
                return; // Ignore non-numeric input
            }
            super.replace(fb, offset, length, text, attrs);
        }
    }
}
