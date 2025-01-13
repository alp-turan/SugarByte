package ui;

import model.LogEntry;
import model.User;
import service.LogService;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * "Simple" Logbook class
 * 7 rows for (Breakfast Pre/Post, Lunch Pre/Post, Dinner Pre/Post, Bedtime).
 * Has columns for Blood Glucose, Carbs, and HoursSinceMeal (for Pre rows).
 */
public class Logbook extends BaseUI {

    protected final User currentUser;
    protected final String targetDate;

    // Columns: BG, Carbs, HoursSinceMeal (for Pre)
    protected JTextField[] bloodSugarFields = new JTextField[7];
    protected JTextField[] carbsFields = new JTextField[7];
    protected JTextField[] hoursSinceMealFields = new JTextField[3]; // only for 3 "Pre" rows

    protected static final String[] ROW_LABELS = {
            "Breakfast Pre",
            "Breakfast Post",
            "Lunch Pre",
            "Lunch Post",
            "Dinner Pre",
            "Dinner Post",
            "Bedtime"
    };

    public Logbook(User user, String date) {
        // Title assigned dynamically based on the provided date
        super("Logbook for " + date);
        // User data stored for later access
        this.currentUser = user;
        // Target date for this logbook instance
        this.targetDate = date;

        // Building the user interface and populating data
        buildUI();
        loadLogEntries();
        // Window visibility enabled
        setVisible(true);
    }

    /**
     * The "simple" logbook user interface construction.
     */
    protected void buildUI() {
        // A gradient background forming the main panel
        JPanel mainPanel = createGradientPanel(Color.WHITE, Color.WHITE);
        mainPanel.setLayout(new BorderLayout());// Layout dividing the panel into regions
        setContentPane(mainPanel); // Assigning the panel to the frame

        // Header section for title and date
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false); // Transparency for blending with the gradient
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS)); // Vertical stacking of components
        topPanel.setBorder(BorderFactory.createEmptyBorder(70, 0, 20, 0)); // Padding around the header

        JLabel titleLabel = createTitleLabel("SugarByte", lobsterFont, Color.BLACK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center-aligned text
        topPanel.add(titleLabel);  // Adding title to the header panel

        String logbookType = getLogbookType(currentUser); // Fetching the logbook type from user data
        String formattedDate = formatDate(targetDate); // Formatted display of the target date

        JLabel dateLabel = new JLabel(logbookType + " logbook for " + formattedDate, SwingConstants.CENTER);
        dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 16)); // Standardized font size for consistency
        dateLabel.setForeground(Color.BLACK); // Black font for readability
        dateLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Margins above and below the text
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Ensuring alignment matches the title
        topPanel.add(dateLabel); // Date label added to the header

        mainPanel.add(topPanel, BorderLayout.NORTH); // Positioning the header at the top

        // Table-like center panel for the logbook data
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false); // Inheriting the transparency
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 7, 5); // Padding between table cells
        gbc.fill = GridBagConstraints.HORIZONTAL; // Allowing horizontal expansion

        // Header row definitions
        gbc.gridx = 0; // First column
        gbc.gridy = 0; // First row
        JLabel timeOfDayHeaderLine1 = new JLabel("Time");
        timeOfDayHeaderLine1.setFont(new Font("SansSerif", Font.BOLD, 12));  // Bold for emphasis
        centerPanel.add(timeOfDayHeaderLine1, gbc); // Adding this JLabel to the center panel

        gbc.gridx = 1; // Second column for blood glucose
        JLabel bloodHeaderLine1 = new JLabel("Blood");
        bloodHeaderLine1.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(bloodHeaderLine1, gbc);

        gbc.gridx = 2; // Third column for carbohydrate intake
        JLabel carbsHeaderLine1 = new JLabel("Carbs");
        carbsHeaderLine1.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(carbsHeaderLine1, gbc);

        gbc.gridx = 3; // Fourth column for hours since the last meal
        JLabel hoursHeaderLine1 = new JLabel("Hours since");
        hoursHeaderLine1.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(hoursHeaderLine1, gbc);

        // Second line of headers for additional details
        gbc.gridy = 1; // Moving to the next row
        gbc.gridx = 0; // Reset to the first column
        JLabel timeOfDayHeaderLine2 = new JLabel("of day");
        timeOfDayHeaderLine2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(timeOfDayHeaderLine2, gbc);

        gbc.gridx = 1; // Blood glucose full label
        JLabel bloodHeaderLine2 = new JLabel("glucose (mmol/L)");
        bloodHeaderLine2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(bloodHeaderLine2, gbc);

        gbc.gridx = 2; // Carbohydrate intake full label
        JLabel carbsHeaderLine2 = new JLabel("eaten (g)");
        carbsHeaderLine2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(carbsHeaderLine2, gbc);

        gbc.gridx = 3; // Hours since the meal full label
        JLabel hoursHeaderLine2 = new JLabel("last meal");
        hoursHeaderLine2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(hoursHeaderLine2, gbc);

        // Data rows with labels and input fields
        int preIndex = 0;
        for (int i = 0; i < ROW_LABELS.length; i++) {
            gbc.gridy = i + 2; // Row positioning based on the label index

            gbc.gridx = 0; // Label for time of day
            JLabel rowLabel = new JLabel(ROW_LABELS[i] + ":");
            rowLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            centerPanel.add(rowLabel, gbc);

            // Column 1 - BloodSugar
            // the process for making the input fields for each row is more or less the same for every feature/column so it hasn't
            // been commented in detail other than the first block for Blood sugar            gbc.gridx = 1;
            bloodSugarFields[i] = new JTextField(5);
            addNumericInputRestriction(bloodSugarFields[i]); // Apply numerical restriction - - this line was taken from ChatGPT as we were unsure of how to set a filter.
            centerPanel.add(bloodSugarFields[i], gbc);

            // Column 2 - Carbs
            gbc.gridx = 2;
            carbsFields[i] = new JTextField(5);
            addNumericInputRestriction(carbsFields[i]); // Apply numerical restriction here
            centerPanel.add(carbsFields[i], gbc);

            // Column 3 - Hours for Pre
            gbc.gridx = 3;
            if (ROW_LABELS[i].endsWith("Pre")) {
                hoursSinceMealFields[preIndex] = new JTextField(5);
                addNumericInputRestriction(hoursSinceMealFields[preIndex]); // Apply numerical restriction here
                centerPanel.add(hoursSinceMealFields[preIndex], gbc);
                preIndex++;
            }
        }

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel with "Save All" Button (moved above nav bar)
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(Box.createVerticalStrut(60)); // Adjust the value to control vertical space above the button - // Adjusting the value to control vertical space above the button - taken from ChatGPT.

        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));  // Centering the button

        // Creating a button for saving all log entries, setting text and styling
        RoundedButtonLogin saveAllBtn = new RoundedButtonLogin("Save all", new Color(237, 165, 170));
        // Setting the text color of the button to black for better readability
        saveAllBtn.setForeground(Color.BLACK);
        // Customizing the font by applying a bold style and a size of 14 points
        saveAllBtn.setFont(new Font("SansSerif", Font.BOLD, 14));  // Adjust text size and font
        // Linking the button's action listener to the method handling the save functionality
        saveAllBtn.addActionListener(e -> handleSaveAll());
        // Placing the save button within the bottom panel
        bottomPanel.add(saveAllBtn);

        // Combining the bottom panel and navigation bar
        JPanel southPanel = new JPanel();
        // Using BorderLayout to manage the placement of components in the south panel
        southPanel.setLayout(new BorderLayout());
        // Keeping the south panel transparent to blend seamlessly with the main panel
        southPanel.setOpaque(false);
        // Positioning the bottom panel at the center of the south panel
        southPanel.add(bottomPanel, BorderLayout.CENTER);
        // Adding a custom navigation bar at the bottom of the south panel
        southPanel.add(createBottomNavBar("Logbook", currentUser,
                        "/Icons/home.png", "/Icons/logbookfull.png", "/Icons/graph.png", "/Icons/profile.png"),
                BorderLayout.SOUTH);

        // Integrating the combined south panel into the main panel
        mainPanel.add(southPanel, BorderLayout.SOUTH);
    }

    /**
     * Loading entries into the "simple" logbook.
     */
    protected void loadLogEntries() {
        // Retrieving a list of log entries for the current user and target date
        List<LogEntry> entries = LogService.getEntriesForDate(currentUser.getId(), targetDate);
        // Storing entries in a map for easy lookup by time of day
        Map<String, LogEntry> entryMap = new HashMap<>();
        // Iterating over each log entry and populating the map
        for (LogEntry entry : entries) {
            entryMap.put(entry.getTimeOfDay(), entry); // Using the time of day as a key for the map
        }

        // Initializing an index to track "Pre" rows
        int preIndex = 0;
        // Iterating over each row label to populate fields with data
        for (int i = 0; i < ROW_LABELS.length; i++) {
            LogEntry e = entryMap.get(ROW_LABELS[i]); // Fetching the log entry for the corresponding time of day
            if (e != null) {
                // Updating the blood sugar field with the value from the log entry
                bloodSugarFields[i].setText(String.valueOf(e.getBloodSugar()));
                // Updating the carbs field with the value from the log entry
                carbsFields[i].setText(String.valueOf(e.getCarbsEaten()));
                // Handling "Pre" rows by updating hours since the last meal
                if (ROW_LABELS[i].endsWith("Pre")) {
                    hoursSinceMealFields[preIndex].setText(String.valueOf(e.getHoursSinceMeal()));
                    preIndex++;
                }
            }
        }
    }

    /**
     * Saving all entered data from the "simple" logbook.
     */
    protected void handleSaveAll() {
        // Index for handling "Pre" rows specifically
        int preIndex = 0;
        // Looping through each row to gather and save data
        for (int i = 0; i < ROW_LABELS.length; i++) {
            // Parsing the blood sugar field text into a double value
            double bg = parseDoubleSafe(bloodSugarFields[i].getText());
            // Parsing the carbs field text into a double value
            double carbs = parseDoubleSafe(carbsFields[i].getText());
            int hours = 0;

            // Parsing hours since the last meal for "Pre" rows
            if (ROW_LABELS[i].endsWith("Pre")) {
                hours = parseIntSafe(hoursSinceMealFields[preIndex].getText());
                preIndex++;
            }

            // Proceeding with saving only if at least one field contains valid data
            if (bg > 0 || carbs > 0 || hours > 0) {
                LogEntry entry = new LogEntry();
                entry.setUserId(currentUser.getId()); // Associating the entry with the current user
                entry.setDate(targetDate); // Setting the date for the entry
                entry.setTimeOfDay(ROW_LABELS[i]); // Specifying the time of day for the entry
                entry.setBloodSugar(bg); // Adding blood sugar value
                entry.setCarbsEaten(carbs); // Adding carbs value
                entry.setHoursSinceMeal(hours); // Adding hours since the last meal

                // Calling the service method to save the log entry and trigger alarms if needed
                LogService.createEntry(entry, currentUser);
            }
        }

        // Informing the user that the logbook has been successfully saved
        JOptionPane.showMessageDialog(this,
                "All entered values have been saved.",
                "Logbook Saved",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Fetching the logbook type for the user or defaulting to "Simple."
     */
    private String getLogbookType(User user) {
        // Retrieving the logbook type from the user object
        String logbookType = user.getLogbookType();
        // Returning the logbook type if available, otherwise defaulting to "Simple"
        return logbookType != null ? logbookType : "Simple";
    }

    /**
     * Formatting a date string for display purposes.
     */
    protected String formatDate(String date) {
        try {
            // Defining input and output date formats
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, MMM d");
            // Parsing the input date and formatting it into the desired output format
            return outputFormat.format(inputFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace(); // Printing the exception stack trace if parsing fails
            return date; // Returning the original date string as a fallback
        }
    }

    /**
     * Parsing a string into a double safely, returning 0.0 if parsing fails.
     */
    protected double parseDoubleSafe(String text) {
        try {
            return Double.parseDouble(text); // Attempting to convert the string to a double
        } catch (NumberFormatException e) {
            return 0.0; // Defaulting to 0.0 if parsing fails
        }
    }

    /**
     * Parsing a string into an integer safely, returning 0 if parsing fails.
     */
    private int parseIntSafe(String text) {
        try {
            return Integer.parseInt(text); // Attempting to convert the string to an integer
        } catch (NumberFormatException e) {
            return 0; // Defaulting to 0 if parsing fails
        }
    }

    /**
     * Restricting field input to positive numbers only.
     */
    /* reference - this class was taken from ChatGPT*/
    private void addNumericInputRestriction(JTextField textField) {
        textField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();

                // Prevent non-digit characters, except for decimal point and backspace
                if (!Character.isDigit(c) && c != '.' && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }

                // Prevent entering negative numbers
                if (c == '-' && (textField.getText().isEmpty() || textField.getText().contains("-"))) {
                    e.consume();  // If the text is empty or already contains a negative sign, don't allow further negative sign.
                }
            }
        });
    }
    /* end of reference*/
}