package ui;

import model.LogEntry;
import model.User;
import service.LogService;

import javax.swing.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.HashSet;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.event.KeyEvent;


/**
 * ComprehensiveLogbook includes columns for:
 *   - Blood Glucose
 *   - Carbs Eaten
 *   - Exercise Type
 *   - Insulin Dose
 *   - Hours Since Last Meal (for Pre rows)
 * and triggers alarms (via AlarmService) automatically.
 */
public class ComprehensiveLogbook extends BaseUI {

    protected User currentUser;
    protected String targetDate;

    // 7 rows: Breakfast Pre/Post, Lunch Pre/Post, Dinner Pre/Post, Bedtime
    private static final String[] ROW_LABELS = {
            "Breakfast Pre",
            "Breakfast Post",
            "Lunch Pre",
            "Lunch Post",
            "Dinner Pre",
            "Dinner Post",
            "Bedtime"
    };

    // Arrays to store input fields for each column in the comprehensive logbook.
    protected JTextField[] bloodSugarFields = new JTextField[7]; // Fields for blood sugar levels, one for each row.
    protected JTextField[] carbsFields = new JTextField[7]; // Fields for carb intake, one for each row.
    protected JTextArea[] exerciseFields = new JTextArea[7]; // Text areas for exercise details, allowing multi-line input.
    protected JTextField[] insulinDoseFields = new JTextField[7]; // Fields for insulin dose amounts, one for each row.
    protected JTextField[] hoursSinceMealFields = new JTextField[3]; // Fields for hours since the last meal (Pre rows only).

    /**
     * Constructor for creating a comprehensive logbook for a specific user and date.
     *
     * @param user The user for whom the logbook is created. Provides access to user-specific data.
     * @param date The date of the logbook. Logs correspond to this date.
     */
    public ComprehensiveLogbook(User user, String date) {
        super("Comprehensive Logbook for " + date); // Set the window title using the date.
        this.currentUser = user; // Store the current user's information.
        this.targetDate = date;  // Store the date for which the logbook is being displayed.

        buildUIComprehensive(); // Build and initialize the user interface components for the logbook.
        loadLogEntriesComprehensive(); // Load any existing log entries for the user and date.
        setVisible(true); // Display the logbook window to the user.
    }

    /**
     * Build the "Comprehensive" UI with columns:
     * Time of Day | Blood Glucose | Carbs Eaten | Exercise Type | Insulin Dose | Hours Since Last Meal
     * and place it in a JScrollPane for scrolling.
     */
    protected void buildUIComprehensive() {
        // Creating the main panel with a gradient background
        JPanel mainPanel = createGradientPanel(Color.WHITE, Color.WHITE); // Gradient background for visual appeal.
        mainPanel.setLayout(new BorderLayout()); // BorderLayout to organize the UI into regions.
        setContentPane(mainPanel); // Setting this panel as the content pane of the JFrame.

        // ===== TOP PANEL =====
        // Creating the top panel for title and date display
        JPanel topPanel = new JPanel(); // Container for the title and logbook type/date.
        topPanel.setOpaque(false); // Transparent background to match the gradient.
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS)); // Vertical layout for stacking components.
        topPanel.setBorder(BorderFactory.createEmptyBorder(65, 0, 20, 0)); // Padding around the panel.

        // Adding the application title
        JLabel titleLabel = createTitleLabel("SugarByte", lobsterFont, Color.BLACK); // Custom title label using the Lobster font.
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center-aligning the title within the panel.
        topPanel.add(titleLabel); // Adding the title label to the top panel.

        // Displaying the logbook type and formatted date
        String logbookType = getLogbookType(currentUser); // Retrieves the type of logbook based on user preferences.
        String formattedDate = formatDate(targetDate); // Formats the target date into a user-friendly string.

        JLabel dateLabel = new JLabel(logbookType + " logbook for " + formattedDate, SwingConstants.CENTER); // Label showing the logbook type and date.
        dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 16)); // Styling the label with a plain SansSerif font.
        dateLabel.setForeground(Color.BLACK); // Black text for readability.
        dateLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Padding around the label.
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center-aligning the label within the panel.
        topPanel.add(dateLabel); // Adding the date label to the top panel.

        mainPanel.add(topPanel, BorderLayout.NORTH); // Placing the top panel at the top of the main layout.

        // ===== CENTER PANEL (GridBagLayout) =====
        // Creating the center panel to hold log entry headers and fields
        JPanel centerPanel = new JPanel(new GridBagLayout()); // Using GridBagLayout for flexible grid-based alignment.
        centerPanel.setOpaque(false); // Transparent background to match the overall design.
        GridBagConstraints gbc = new GridBagConstraints(); // Object to specify layout constraints for the grid.
        gbc.insets = new Insets(5, 5, 0, 5); // Adding padding around grid components.
        gbc.fill = GridBagConstraints.HORIZONTAL; // Ensuring components fill horizontal space.

        // First row of headers
        gbc.gridy = 0; // First row in the grid.
        gbc.gridx = 0; // First column in the grid.
        JLabel timeHeaderLine1 = new JLabel("Time"); // Header label for the "Time" column.
        timeHeaderLine1.setFont(new Font("SansSerif", Font.BOLD, 12)); // Bold font for header emphasis.
        centerPanel.add(timeHeaderLine1, gbc); // Adding the header to the grid.

        gbc.gridx = 1; // Moving to the second column.
        JLabel bloodHeaderLine1 = new JLabel("Blood"); // Header label for the "Blood" column.
        bloodHeaderLine1.setFont(new Font("SansSerif", Font.BOLD, 12)); // Bold font for header emphasis.
        centerPanel.add(bloodHeaderLine1, gbc); // Adding the header to the grid.

        gbc.gridx = 2; // Moving to the third column.
        JLabel hoursHeaderLine1 = new JLabel("Hours since"); // Header label for the "Hours since" column.
        hoursHeaderLine1.setFont(new Font("SansSerif", Font.BOLD, 12)); // Bold font for header emphasis.
        centerPanel.add(hoursHeaderLine1, gbc); // Adding the header to the grid.

        gbc.gridx = 3; // Moving to the fourth column.
        JLabel carbsHeaderLine1 = new JLabel("Carbs"); // Header label for the "Carbs" column.
        carbsHeaderLine1.setFont(new Font("SansSerif", Font.BOLD, 12)); // Bold font for header emphasis.
        centerPanel.add(carbsHeaderLine1, gbc); // Adding the header to the grid.

        gbc.gridx = 4; // Moving to the fifth column.
        JLabel exerciseHeaderLine1 = new JLabel("Exercise"); // Header label for the "Exercise" column.
        exerciseHeaderLine1.setFont(new Font("SansSerif", Font.BOLD, 12)); // Bold font for header emphasis.
        centerPanel.add(exerciseHeaderLine1, gbc); // Adding the header to the grid.

        // Column 5: Insulin header for the first row of headers
        gbc.gridx = 5; // Sixth column in the grid
        JLabel insulinHeaderLine1 = new JLabel("Insulin"); // Header label for the "Insulin" column
        insulinHeaderLine1.setFont(new Font("SansSerif", Font.BOLD, 12)); // Bold font for emphasis
        centerPanel.add(insulinHeaderLine1, gbc); // Adding the header to the grid

        // ===== SECOND ROW OF HEADERS =====
        gbc.gridy = 1; // Second row in the grid

        // Column 0: Time of day
        gbc.gridx = 0; // First column in the second row
        JLabel timeHeaderLine2 = new JLabel("of day"); // Secondary header for the "Time" column
        timeHeaderLine2.setFont(new Font("SansSerif", Font.BOLD, 12)); // Bold font for emphasis
        centerPanel.add(timeHeaderLine2, gbc); // Adding the header to the grid

        // Column 1: Blood glucose
        gbc.gridx = 1; // Second column
        JLabel bloodHeaderLine2 = new JLabel("glucose (mmol/L)"); // Secondary header for "Blood glucose" column
        bloodHeaderLine2.setFont(new Font("SansSerif", Font.BOLD, 12)); // Bold font for emphasis
        centerPanel.add(bloodHeaderLine2, gbc); // Adding the header to the grid

        // Column 2: Hours since last meal
        gbc.gridx = 2; // Third column
        JLabel hoursHeaderLine2 = new JLabel("last meal (hr)"); // Secondary header for "Hours since last meal"
        hoursHeaderLine2.setFont(new Font("SansSerif", Font.BOLD, 12)); // Bold font for emphasis
        centerPanel.add(hoursHeaderLine2, gbc); // Adding the header to the grid

        // Column 3: Carbs
        gbc.gridx = 3; // Fourth column
        JLabel carbsHeaderLine2 = new JLabel("eaten (g)"); // Secondary header for "Carbs eaten"
        carbsHeaderLine2.setFont(new Font("SansSerif", Font.BOLD, 12)); // Bold font for emphasis
        centerPanel.add(carbsHeaderLine2, gbc); // Adding the header to the grid

        // Column 4: Exercise
        gbc.gridx = 4; // Fifth column
        JLabel exerciseHeaderLine2 = new JLabel("type"); // Secondary header for "Exercise type"
        exerciseHeaderLine2.setFont(new Font("SansSerif", Font.BOLD, 12)); // Bold font for emphasis
        centerPanel.add(exerciseHeaderLine2, gbc); // Adding the header to the grid

        // Column 5: Insulin dose
        gbc.gridx = 5; // Sixth column
        JLabel insulinHeaderLine2 = new JLabel("dose"); // Secondary header for "Insulin dose"
        insulinHeaderLine2.setFont(new Font("SansSerif", Font.BOLD, 12)); // Bold font for emphasis
        centerPanel.add(insulinHeaderLine2, gbc); // Adding the header to the grid

        // ===== DATA ROWS =====
        int preIndex = 0; // Counter to track "Pre" rows for hoursSinceMealFields
        for (int i = 0; i < ROW_LABELS.length; i++) {
            gbc.gridy = i + 2; // Start data rows from the third row in the grid

            // Column 0: Time-of-day label
            gbc.gridx = 0; // First column
            JLabel rowLabel = new JLabel(ROW_LABELS[i] + ":"); // Label for the time of day (e.g., "Breakfast Pre")
            rowLabel.setFont(new Font("SansSerif", Font.BOLD, 12)); // Bold font for emphasis
            centerPanel.add(rowLabel, gbc); // Adding the label to the grid

            // Column 1: Blood sugar
            gbc.gridx = 1; // Second column
            bloodSugarFields[i] = createNumberOnlyField(); // Creating a text field for blood sugar input
            centerPanel.add(bloodSugarFields[i], gbc); // Adding the text field to the grid

            // Column 2: Hours since meal (only for "Pre" rows)
            gbc.gridx = 2; // Third column
            if (ROW_LABELS[i].endsWith("Pre")) { // Checking if the row corresponds to a "Pre" entry
                hoursSinceMealFields[preIndex] = createNumberOnlyField(); // Creating a text field for hours since meal
                centerPanel.add(hoursSinceMealFields[preIndex], gbc); // Adding the text field to the grid
                preIndex++; // Incrementing the "Pre" row counter
            }

            // Column 3: Carbs
            gbc.gridx = 3; // Fourth column
            carbsFields[i] = createNumberOnlyField(); // Creating a text field for carbs input
            centerPanel.add(carbsFields[i], gbc); // Adding the text field to the grid

            // Column 4: Exercise
            gbc.gridx = 4; // Fifth column
            exerciseFields[i] = new JTextArea(2, 10); // Creating a multi-line text area for exercise input
            exerciseFields[i].setLineWrap(true); // Enabling line wrapping for the text area
            exerciseFields[i].setWrapStyleWord(true); // Wrapping at word boundaries
            JScrollPane exerciseScrollPane = new JScrollPane(exerciseFields[i]); // Adding a scroll pane to the text area
            exerciseScrollPane.setPreferredSize(new Dimension(100, 40)); // Setting a preferred size for the scroll pane
            centerPanel.add(exerciseScrollPane, gbc); // Adding the scroll pane to the grid

            // Column 5: Insulin
            gbc.gridx = 5; // Sixth column
            insulinDoseFields[i] = createNumberOnlyField(); // Creating a text field for insulin dose input
            centerPanel.add(insulinDoseFields[i], gbc); // Adding the text field to the grid
        }

// Create a JScrollPane to make the centerPanel scrollable
        /* reference - taken from ChatGPT as we did not how to make a scrollable panel*/
        JScrollPane scrollPane = new JScrollPane(
                centerPanel, // The panel to be scrolled
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, // Add vertical scroll bar as needed
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED // Add horizontal scroll bar as needed
        );

// Set transparency for the scroll pane and its viewport
        scrollPane.setOpaque(false); // Make the entire scroll pane transparent
        scrollPane.getViewport().setOpaque(false); // Make the viewport (content area) transparent
        scrollPane.setBackground(new Color(0, 0, 0, 0)); // Set a fully transparent background for the scroll pane
        scrollPane.getViewport().setBackground(new Color(0, 0, 0, 0)); // Transparent background for the viewport

// Remove the default border of the scroll pane
        scrollPane.setBorder(null); // Remove all borders from the scroll pane
        /* end of reference*/

// Add the scroll pane to the main panel
        mainPanel.add(scrollPane, BorderLayout.CENTER); // Place the scroll pane in the center of the main panel

// Customize the layout of the JScrollPane
        scrollPane.setLayout(new ScrollPaneLayout() {
            @Override
            public void layoutContainer(Container parent) {
                super.layoutContainer(parent);

                // Adjust the horizontal scrollbar to be at the top
                JScrollPane pane = (JScrollPane) parent;
                if (pane.getHorizontalScrollBar() != null) { // Check if the scrollbar exists
                    Rectangle bounds = pane.getHorizontalScrollBar().getBounds();
                    bounds.y = 420; // Position the scrollbar at the top
                    pane.getHorizontalScrollBar().setBounds(bounds); // Apply the new bounds

                    // Adjust the viewport to avoid overlapping with the scrollbar
                    JViewport viewport = pane.getViewport();
                    Rectangle viewportBounds = viewport.getBounds();
                    viewportBounds.y = bounds.height; // Shift the viewport down to accommodate the scrollbar
                    viewportBounds.height -= bounds.height; // Reduce the viewport height accordingly
                    viewport.setBounds(viewportBounds); // Apply the adjusted bounds to the viewport
                }
            }
        });

// Additional scroll pane configuration
        scrollPane.setOpaque(false); // Ensure transparency is maintained
        scrollPane.setBackground(Color.WHITE); // Set the background color to white for better visibility
        scrollPane.getViewport().setBackground(Color.WHITE); // Match the viewport background to the scroll pane
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20)); // Add padding inside the scroll pane
        scrollPane.setViewportBorder(null); // Remove the viewport border

// Add the scroll pane to the main panel again (ensures placement is correct)
        mainPanel.add(scrollPane, BorderLayout.CENTER);

// ===== BOTTOM PANEL (Save and Navigation Buttons) =====
        JPanel bottomPanel = new JPanel(); // Panel to hold the save button
        bottomPanel.add(Box.createVerticalStrut(60)); // Add vertical spacing above the button
        bottomPanel.setOpaque(false); // Make the bottom panel transparent

// Create a "Save all" button
        RoundedButtonLogin saveAllBtn = new RoundedButtonLogin("Save all", new Color(237, 165, 170)); // Styled button with a custom color
        saveAllBtn.setFont(new Font("SansSerif", Font.BOLD, 16)); // Apply bold font for emphasis
        saveAllBtn.setForeground(Color.BLACK); // Set the text color to black
        saveAllBtn.addActionListener(e -> handleSaveAllComprehensive()); // Attach the save action to the button
        bottomPanel.add(saveAllBtn); // Add the button to the bottom panel

// Create a navigation bar for the bottom section
        JPanel navBar = createBottomNavBar(
                "Logbook", // Current screen identifier
                currentUser, // Pass the current user for navigation context
                "/Icons/home.png", // Home icon
                "/Icons/logbookfull.png", // Logbook icon (current screen)
                "/Icons/graph.png", // Graph icon
                "/Icons/profile.png" // Profile icon
        );

// Create a wrapper panel to combine the save button and the navigation bar
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setOpaque(false); // Make the wrapper panel transparent
        wrapperPanel.add(bottomPanel, BorderLayout.NORTH); // Place the save button at the top of the wrapper
        wrapperPanel.add(navBar, BorderLayout.SOUTH); // Place the navigation bar at the bottom

// Add the wrapper panel to the main panel
        mainPanel.add(wrapperPanel, BorderLayout.SOUTH);// Place the wrapper panel at the bottom of the main panel

    }

// ===== Utility Methods =====

    // Create a JTextField that accepts only numeric input
    private JTextField createNumberOnlyField() {
        JTextField textField = new JTextField(5); // Create a text field with a fixed width
        textField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                // Allow only digits, '.' (for decimals), backspace, and delete
                if (!(Character.isDigit(c) || c == '.' || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                    e.consume(); // Reject invalid input
                }
            }
        });
        return textField; // Return the configured text field
    }

    // Get the type of logbook for the current user
    private String getLogbookType(User user) {
        String logbookType = user.getLogbookType(); // Retrieve the logbook type from the user object
        return logbookType != null ? logbookType : "Comprehensive"; // Default to "Comprehensive" if no type is set
    }

    /**
     * Formats a given date string for display in a human-readable format.
     *
     * @param date The date string in the input format (e.g., "yyyy-MM-dd").
     * @return A formatted date string (e.g., "Monday, Jan 1").
     */
    protected String formatDate(String date) {
        try {
            // Define the input date format
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            // Define the desired output format
            SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, MMM d");
            // Parse the input date and format it to the output format
            return outputFormat.format(inputFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return date; // Return the original date if parsing fails
        }
    }

    /**
     * Creates a JTextField that allows only alphabetic input.
     *
     * @return A JTextField configured to accept only letters.
     */
    private JTextField createAlphaOnlyField() {
        JTextField textField = new JTextField(5); // Create a text field with a width of 5 columns
        textField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                // Allow only letters, backspace, and delete keys
                if (!(Character.isLetter(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                    e.consume(); // Reject non-alphabetic input
                }
            }
        });
        return textField; // Return the configured text field
    }

    /**
     * Loads log entries for the "Comprehensive" logbook style and populates the form fields.
     */
    protected void loadLogEntriesComprehensive() {
        // Fetch log entries for the current user on the target date
        List<LogEntry> entries = LogService.getEntriesForDate(currentUser.getId(), targetDate);
        // Map to store log entries by their time of day
        Map<String, LogEntry> entryMap = new HashMap<>();
        for (LogEntry entry : entries) {
            entryMap.put(entry.getTimeOfDay(), entry); // Map each entry by its time of day
        }

        int preIndex = 0; // Counter for "Pre" rows (used for hoursSinceMeal fields)
        for (int i = 0; i < ROW_LABELS.length; i++) {
            // Retrieve the log entry for the current time of day
            LogEntry e = entryMap.get(ROW_LABELS[i]);
            if (e != null) { // If an entry exists, populate the corresponding fields
                bloodSugarFields[i].setText(String.valueOf(e.getBloodSugar())); // Populate blood sugar field
                carbsFields[i].setText(String.valueOf(e.getCarbsEaten())); // Populate carbs field
                exerciseFields[i].setText(e.getExerciseType() == null ? "" : e.getExerciseType()); // Populate exercise field
                insulinDoseFields[i].setText(String.valueOf(e.getInsulinDose())); // Populate insulin field

                // For "Pre" rows, populate hoursSinceMeal field
                if (ROW_LABELS[i].endsWith("Pre")) {
                    hoursSinceMealFields[preIndex].setText(String.valueOf(e.getHoursSinceMeal()));
                    preIndex++; // Increment the counter for "Pre" rows
                }
            }
        }
    }

    /**
     * Saves all data from the comprehensive logbook form and triggers alarms if needed.
     */
    protected void handleSaveAllComprehensive() {
        int preIndex = 0; // Counter for "Pre" rows (used for hoursSinceMeal fields)
        Set<String> processedEntries = new HashSet<>(); // Track processed entries to avoid duplicate notifications

        for (int i = 0; i < ROW_LABELS.length; i++) {
            // Parse and retrieve data from the form fields
            double bg = parseDoubleSafe(bloodSugarFields[i].getText()); // Blood glucose value
            double carbs = parseDoubleSafe(carbsFields[i].getText()); // Carbs eaten
            String exercise = exerciseFields[i].getText(); // Exercise type
            double insulin = parseDoubleSafe(insulinDoseFields[i].getText()); // Insulin dose
            int hours = 0; // Default hours since meal

            // For "Pre" rows, retrieve hoursSinceMeal
            if (ROW_LABELS[i].endsWith("Pre")) {
                hours = parseIntSafe(hoursSinceMealFields[preIndex].getText());
                preIndex++; // Increment the counter for "Pre" rows
            }

            // Check if the entry contains any meaningful data
            if (bg > 0 || carbs > 0 || !exercise.isEmpty() || insulin > 0 || hours > 0) {
                // Create a new log entry object
                LogEntry entry = new LogEntry();
                entry.setUserId(currentUser.getId()); // Set the user ID
                entry.setDate(targetDate); // Set the date
                entry.setTimeOfDay(ROW_LABELS[i]); // Set the time of day
                entry.setBloodSugar(bg); // Set blood glucose value
                entry.setCarbsEaten(carbs); // Set carbs eaten
                entry.setExerciseType(exercise); // Set exercise type
                entry.setInsulinDose(insulin); // Set insulin dose
                entry.setHoursSinceMeal(hours); // Set hours since meal

                // Generate a unique identifier for the log entry
                String entryIdentifier = targetDate + "-" + ROW_LABELS[i];

                // Avoid duplicate notifications for already processed entries
                if (processedEntries.contains(entryIdentifier)) {
                    System.out.println("Notification already sent for entry: " + entryIdentifier + ", skipping.");
                } else {
                    // Create or update the log entry and trigger alarms if necessary
                    LogService.createEntry(entry, currentUser);

                    // Mark the entry as processed
                    processedEntries.add(entryIdentifier);
                }
            }
        }
    }

    /**
     * Safely parses a string to a double.
     *
     * @param text The string to parse.
     * @return The parsed double, or 0.0 if parsing fails.
     */
    private double parseDoubleSafe(String text) {
        try {
            return Double.parseDouble(text); // Attempt to parse the string as a double
        } catch (NumberFormatException e) {
            return 0.0; // Return 0.0 if parsing fails
        }
    }

    /**
     * Safely parses a string to an integer.
     *
     * @param text The string to parse.
     * @return The parsed integer, or 0 if parsing fails.
     */
    private int parseIntSafe(String text) {
        try {
            return Integer.parseInt(text); // Attempt to parse the string as an integer
        } catch (NumberFormatException e) {
            return 0; // Return 0 if parsing fails
        }
    }
}

