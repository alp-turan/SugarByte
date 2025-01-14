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

        // ============ LAST WEEK'S TREND BUTTON ============
        // "View Today's Logbook" button
        RoundedButton logbookButton = new RoundedButton("View today's logbook", new Color(240, 240, 240));
        logbookButton.setForeground(Color.BLACK);
        logbookButton.setFont(new Font("Poppins", Font.BOLD, 14));
        logbookButton.setPreferredSize(new Dimension(200, 40));

        logbookButton.addActionListener(e -> {
            dispose();
            String logbookType = currentUser.getLogbookType(); // "Simple", "Comprehensive", or "Intensive"

            switch (logbookType) {
                case "Simple":
                    new Logbook(currentUser, today.toString());
                    break;
                case "Comprehensive":
                    new ComprehensiveLogbook(currentUser, today.toString());
                    break;
                case "Intensive":
                    new IntensiveLogbook(currentUser, today.toString());
                    break;
                default:
                    JOptionPane.showMessageDialog(
                            this,
                            "Unknown logbook type: " + logbookType,
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    break;
            }
        });

        gbc.gridy = 5; // Adjusted position for "View Today's Logbook"
        gbc.insets = new Insets(20, 20, 10, 20);
        centerPanel.add(logbookButton, gbc);

        // ============ GLUCOSE GRAPH BUTTON ============
        JButton trendButton = new RoundedButton("Glucose graph", new Color(240, 240, 240));
        trendButton.setForeground(Color.BLACK);
        trendButton.setFont(new Font("Poppins", Font.BOLD, 14));
        trendButton.setPreferredSize(new Dimension(250, 40));

        trendButton.addActionListener(e -> {
            dispose(); // Close Home
            new GlucoseGraph(currentUser); // Open the new graph page
        });

        gbc.gridy = 6; // Position it after the "View Today's Logbook" button
        gbc.insets = new Insets(10, 20, 10, 20);
        centerPanel.add(trendButton, gbc);

        mainPanel.add(centerPanel);

        // ============ BOTTOM NAV BAR ============
        JPanel navBar = createBottomNavBar("Home", currentUser,
                "/Icons/homefull.png", "/Icons/logbook.png", "/Icons/graph.png", "/Icons/profile.png");
        mainPanel.add(navBar);
    }

    /**
     * Fetching the latest blood glucose reading for the current user from today's logs.
     *
     * @return The most recent blood glucose value for today. Returns a default of 6.0 if no logs exist.
     */
    private double getLatestGlucoseReading() {
        // Retrieving today's log entries for the current user using LogService.
        List<LogEntry> todaysLogs = LogService.getEntriesForDate(
                currentUser.getId(), // Fetching the user's unique ID to identify their logs.
                LocalDate.now().toString() // Fetching the current date as a string using LocalDate's `now` and `toString`.
        );

        // Checking if there are any log entries for today's date.
        if (!todaysLogs.isEmpty()) {
            // Returning the blood glucose level from the first log entry in the list.
            return todaysLogs.get(0).getBloodSugar();
        }

        // Returning a default blood glucose value of 6.0 when no logs exist for today.
        return 6.0;
    }

    /**
     * Creating the Quick Log panel, allowing the user to input pre/post glucose levels and carbs.
     *
     * @return A JPanel containing input fields for quick glucose logging.
     */
    private JPanel createActualQuickLogPanel() {
        JPanel panel = new JPanel(); // Initializing a JPanel to hold the components.
        panel.setOpaque(false); // Making the panel transparent for design consistency.
        panel.setBorder(BorderFactory.createTitledBorder("Quick Log")); // Adding a titled border to label the panel.
        panel.setLayout(new GridBagLayout()); // Using GridBagLayout for flexible component placement.

        // Determine time of day (e.g., Breakfast, Lunch, Bedtime).
        String timeOfDay = getCurrentMeal(); // Assuming getCurrentMeal() returns the current time of day.

        // Configuring constraints for the layout manager.
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Adding consistent padding between components.
        gbc.fill = GridBagConstraints.HORIZONTAL; // Ensuring components stretch horizontally.

        // Adding a label for the current meal (e.g., "Breakfast").
        JLabel mealLabel = new JLabel("Meal: " + timeOfDay); // Displaying the current meal name.
        mealLabel.setFont(new Font("SansSerif", Font.BOLD, 16)); // Applying a bold font to the label.
        mealLabel.setForeground(new Color(200, 40, 40)); // Setting the text color to red for emphasis.

        gbc.gridy = 0; // Placing the meal label in the first row.
        gbc.gridx = 0; // Starting at the first column.
        gbc.gridwidth = 3; // Spanning three columns for alignment.
        panel.add(mealLabel, gbc); // Adding the meal label to the panel.

        gbc.gridwidth = 1; // Resetting the column span for subsequent components.

        // Adding table headers for input fields.
        gbc.gridy = 1; // Moving to the second row.
        gbc.gridx = 0;
        panel.add(new JLabel(""), gbc); // Empty label for alignment.
        gbc.gridx = 1;
        panel.add(new JLabel("Blood glucose (mmol/L)"), gbc); // Header for glucose input fields.
        gbc.gridx = 2;
        panel.add(new JLabel("Carbs eaten (g)"), gbc); // Header for carb input fields.

        // Adding input fields for "Pre" glucose and carbs.
        gbc.gridy = 2; // Moving to the third row.
        gbc.gridx = 0;
        JLabel preLabel = new JLabel("Pre:");
        preLabel.setForeground("Bedtime".equalsIgnoreCase(timeOfDay) ? Color.WHITE : Color.BLACK); // White if Bedtime.
        panel.add(preLabel, gbc);

        gbc.gridx = 1;
        preBloodSugarField = new JTextField(5); // Input field for pre-meal glucose.
        applyNumericFilter(preBloodSugarField); // Applying a numeric filter to restrict input.
        panel.add(preBloodSugarField, gbc);

        gbc.gridx = 2;
        preCarbsField = new JTextField(5); // Input field for pre-meal carbs.
        applyNumericFilter(preCarbsField); // Applying a numeric filter to restrict input.
        panel.add(preCarbsField, gbc);

        // Adding input fields for "Post" glucose and carbs.
        gbc.gridy = 3; // Moving to the fourth row.
        gbc.gridx = 0;
        JLabel postLabel = new JLabel("Post:");
        postLabel.setForeground("Bedtime".equalsIgnoreCase(timeOfDay) ? Color.WHITE : Color.BLACK); // White if Bedtime.
        panel.add(postLabel, gbc);

        gbc.gridx = 1;
        postBloodSugarField = new JTextField(5); // Input field for post-meal glucose.
        applyNumericFilter(postBloodSugarField); // Applying a numeric filter to restrict input.
        if ("Bedtime".equalsIgnoreCase(timeOfDay)) {
            postBloodSugarField.setForeground(Color.WHITE); // Make text color white for Bedtime.
            postBloodSugarField.setCaretColor(Color.WHITE); // Ensure cursor is visible.
            postBloodSugarField.setBorder(BorderFactory.createLineBorder(Color.WHITE)); // Set border color to white.
        }
        panel.add(postBloodSugarField, gbc);

        gbc.gridx = 2;
        postCarbsField = new JTextField(5); // Input field for post meal carbs.
        applyNumericFilter(postCarbsField); // Applying a numeric filter to restrict input.
        if ("Bedtime".equalsIgnoreCase(timeOfDay)) {
            postCarbsField.setForeground(Color.WHITE); // Make text color white for Bedtime.
            postCarbsField.setCaretColor(Color.WHITE); // Ensure cursor is visible.
            postCarbsField.setBorder(BorderFactory.createLineBorder(Color.WHITE)); // Set border color to white.
        }
        panel.add(postCarbsField, gbc);

        // Adding a save button for logging the inputs.
        gbc.gridy = 4; // Moving to the next row.
        gbc.gridx = 1;
        gbc.gridwidth = 2; // Spanning two columns for alignment.
        JButton saveBtn = new RoundedButton("Save log", new Color(237, 165, 170)); // Styled button for saving logs.
        saveBtn.setForeground(Color.BLACK); // Setting the text color of the button.
        saveBtn.setFont(new Font("SansSerif", Font.BOLD, 14)); // Applying a bold font to the button.
        saveBtn.setPreferredSize(new Dimension(200, 40)); // Setting a fixed size for the button.
        saveBtn.addActionListener(e -> saveQuickLog()); // Adding an action listener to save the log.
        panel.add(saveBtn, gbc); // Adding the save button to the panel.

        return panel; // Returning the completed Quick Log panel.
    }

    /**
     * Determining the current meal based on the system's current hour.
     *
     * @return A string representing the meal name (e.g., "Breakfast", "Lunch").
     */
    private String getCurrentMeal() {
        int hour = LocalTime.now().getHour(); // Fetching the current hour using LocalTime's `now` method.

        // Returning the appropriate meal based on the hour.
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

    /**
     * Saving the user's quick log for glucose levels and carbs, creating log entries for both pre and post-meal data.
     */
    private void saveQuickLog() {
        // Fetching input values for pre-meal glucose and carbs.
        double preBG = parseDoubleSafe(preBloodSugarField.getText()); // Parsing pre-meal glucose input.
        double preCarbs = parseDoubleSafe(preCarbsField.getText()); // Parsing pre-meal carbs input.

        LocalDate today = LocalDate.now(); // Fetching today's date.
        String meal = getCurrentMeal(); // Determining the current meal.

        // Creating and saving a log entry for "Pre" meal data if inputs are provided.
        if (preBG > 0 || preCarbs > 0) {
            LogEntry entryPre = new LogEntry();
            entryPre.setUserId(currentUser.getId()); // Associating the log with the current user.
            entryPre.setDate(today.toString()); // Setting the log date.
            entryPre.setTimeOfDay(meal + " Pre"); // Specifying the log as pre-meal.
            entryPre.setBloodSugar(preBG); // Storing the pre-meal glucose value.
            entryPre.setCarbsEaten(preCarbs); // Storing the pre-meal carbs value.
            LogService.createEntry(entryPre, currentUser); // Saving the log entry using LogService.
        }

        // Creating and saving a log entry for "Post" meal data if inputs are provided.
        double postBG = parseDoubleSafe(postBloodSugarField.getText()); // Parsing post-meal glucose input.
        double postCarbs = parseDoubleSafe(postCarbsField.getText()); // Parsing post-meal carbs input.
        if (postBG > 0 || postCarbs > 0) {
            LogEntry entryPost = new LogEntry();
            entryPost.setUserId(currentUser.getId()); // Associating the log with the current user.
            entryPost.setDate(today.toString()); // Setting the log date.
            entryPost.setTimeOfDay(meal + " Post"); // Specifying the log as post-meal.
            entryPost.setBloodSugar(postBG); // Storing the post-meal glucose value.
            entryPost.setCarbsEaten(postCarbs); // Storing the post-meal carbs value.
            LogService.createEntry(entryPost, currentUser); // Saving the log entry using LogService.
        }

        // Creating and saving a log entry for "Bedtime" if inputs are provided.
        double bedtimeBG = parseDoubleSafe(preBloodSugarField.getText()); // Parsing bedtime glucose input.
        double bedtimeCarbs = parseDoubleSafe(preCarbsField.getText()); // Parsing bedtime carbs input.
        if (bedtimeBG > 0 || bedtimeCarbs > 0) {
            LogEntry entryBedtime = new LogEntry();
            entryBedtime.setUserId(currentUser.getId()); // Associating the log with the current user.
            entryBedtime.setDate(today.toString()); // Setting the log date.
            entryBedtime.setTimeOfDay("Bedtime"); // Specifying the log as "Bedtime".
            entryBedtime.setBloodSugar(bedtimeBG); // Storing the bedtime glucose value.
            entryBedtime.setCarbsEaten(bedtimeCarbs); // Storing the bedtime carbs value.
            LogService.createEntry(entryBedtime, currentUser); // Saving the log entry using LogService.
        }

        // Showing confirmation dialog to indicate successful logging.
        JOptionPane.showMessageDialog(this, "Quick log saved!");

        // Updating the glucose indicator with the latest glucose value after saving.
        double latestGlucose = getLatestGlucoseReading();
        glucoseIndicator.updateGlucoseLevel(latestGlucose); // Refreshing the glucose indicator dynamically.
    }


    /**
     * Safely parsing a string to a double value.
     *
     * @param text The input text to parse, typically from a text field.
     * @return The parsed double value, or 0.0 if parsing fails due to invalid input.
     */
    private double parseDoubleSafe(String text) {
        try {
            // Attempting to parse the input text as a double using Double.parseDouble.
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            // Returning 0.0 if the input is not a valid double.
            return 0.0;
        }
    }

    /**
     * Applying a numeric filter to a JTextField to ensure only numeric input is allowed.
     *
     * @param textField The JTextField to which the numeric filter is applied.
     */
    /* reference 15 - this entire section was taken from ChatGPT*/
    private void applyNumericFilter(JTextField textField) {
        // Setting a custom DocumentFilter on the text field's document to enforce numeric input.
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new NumericFilter());
    }

    /**
     * A custom DocumentFilter that allows only numeric input in text fields.
     * Supports both integer and decimal values.
     */
    public static class NumericFilter extends DocumentFilter {

        /**
         * Handling the insertion of text into the document.
         *
         * @param fb     The filter bypass for document modification.
         * @param offset The position at which the text is inserted.
         * @param string The text to be inserted.
         * @param attr   The attributes for the inserted text.
         * @throws BadLocationException Thrown if the insertion violates document constraints.
         */
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            // Validating the input string against a regex that matches numeric values (integers or decimals).
            if (string == null || !string.matches("\\d*\\.?\\d*")) {
                return; // Ignoring non-numeric input.
            }
            // Allowing the insertion of valid numeric input.
            super.insertString(fb, offset, string, attr);
        }

        /**
         * Handling the replacement of text in the document.
         *
         * @param fb     The filter bypass for document modification.
         * @param offset The starting position of the replacement.
         * @param length The length of the text to replace.
         * @param text   The replacement text.
         * @param attrs  The attributes for the replacement text.
         * @throws BadLocationException Thrown if the replacement violates document constraints.
         */
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            // Validating the replacement text against a regex that matches numeric values (integers or decimals).
            if (text == null || !text.matches("\\d*\\.?\\d*")) {
                return; // Ignoring non-numeric input.
            }
            // Allowing the replacement of valid numeric input.
            super.replace(fb, offset, length, text, attrs);
        }
    }
    /* end of reference 15*/

}

