package ui;

import model.LogEntry;
import model.User;
import service.LogService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * IntensiveLogbook includes:
 *   - Blood Glucose
 *   - Carbs Eaten
 *   - Exercise Type
 *   - Insulin Dose
 *   - Food Diary
 *   - Other Events
 *   - Hours Since Last Meal (for Pre rows)
 * Also triggers alarms via LogService (like the Simple version).
 *
 * The scrollable feature is implemented using JScrollPane
 */
public class IntensiveLogbook extends BaseUI {

    protected User currentUser;
    protected String targetDate;

    // 7 standard rows
    private static final String[] ROW_LABELS = { // the labels of all the rows on the Intensive Logbook page
            "Breakfast Pre",
            "Breakfast Post",
            "Lunch Pre",
            "Lunch Post",
            "Dinner Pre",
            "Dinner Post",
            "Bedtime"
    };

    // Arrays for each column
    protected JTextField[] bloodSugarFields  = new JTextField[7]; // the value in JTextField indicates the number of rows the column has
    protected JTextField[] carbsFields       = new JTextField[7];
    protected JTextField[] insulinDoseFields = new JTextField[7];
    protected JTextArea[] exerciseFields = new JTextArea[7];
    protected JTextArea[] foodDiaryFields = new JTextArea[7];
    protected JTextArea[] otherEventsFields = new JTextArea[7];

    // Hours Since Last Meal for "Pre" rows
    protected JTextField[] hoursSinceMealFields = new JTextField[3];

    public IntensiveLogbook(User user, String date) {
        super("Intensive Logbook for " + date);
        this.currentUser = user;
        this.targetDate  = date;

        buildUIIntensive();
        loadLogEntriesIntensive();
        setVisible(true);
    }

    /**
     * Building the "Intensive" UI layout with columns for detailed data entry.
     * Placing the content inside a scrollable JScrollPane for accessibility.
     */
    protected void buildUIIntensive() {
        // Creating the main panel with a gradient background
        JPanel mainPanel = createGradientPanel(Color.WHITE, Color.WHITE);
        mainPanel.setLayout(new BorderLayout()); // Using BorderLayout for the main layout
        setContentPane(mainPanel); // Setting the main panel as the content pane for this JFrame

        // ==== TOP PANEL ====
        JPanel topPanel = new JPanel(); // Creating a top panel for the title and date
        topPanel.setOpaque(false); // Making the panel transparent to show the background gradient
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS)); // Stacking components vertically
        topPanel.setBorder(BorderFactory.createEmptyBorder(65, 0, 20, 0)); // Adding padding around the top panel

        // Creating and adding the title label
        JLabel titleLabel = createTitleLabel("SugarByte", lobsterFont, Color.BLACK); // Title with custom font
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Centering the label horizontally
        topPanel.add(titleLabel); // Adding the title label to the top panel

        // Formatting and displaying the logbook type and date
        String logbookType = getLogbookType(currentUser); // Retrieving the logbook type from the user object
        String formattedDate = formatDate(targetDate); // Formatting the target date for display
        JLabel dateLabel = new JLabel(logbookType + " logbook for " + formattedDate, SwingConstants.CENTER); // Creating the date label
        dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 16)); // Setting font for the date label
        dateLabel.setForeground(Color.BLACK); // Setting text color to black
        dateLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Adding padding around the date label
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Centering the label horizontally
        topPanel.add(dateLabel); // Adding the date label to the top panel

        // Adding the top panel to the main panel at the north position
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // ==== CENTER PANEL ====
        JPanel centerPanel = new JPanel(new GridBagLayout()); // Creating a center panel with GridBagLayout
        centerPanel.setOpaque(false); // Making the center panel transparent to show the background
        GridBagConstraints gbc = new GridBagConstraints(); // Setting up constraints for GridBagLayout
        gbc.insets = new Insets(5, 5, 0, 5); // Adding padding between components
        gbc.fill = GridBagConstraints.HORIZONTAL; // Allowing components to fill horizontally within their cells

        // Adding headers for the first line
        gbc.gridy = 0; // Setting the row index
        gbc.gridx = 0; // Setting the column index for the first header
        JLabel timeHeader1 = new JLabel("Time"); // Creating the "Time" column header
        timeHeader1.setFont(new Font("SansSerif", Font.BOLD, 12)); // Setting a bold font for emphasis
        centerPanel.add(timeHeader1, gbc); // Adding the header to the center panel

        // - Same method for all the following texts as that explained in comments above -
        gbc.gridx = 1;
        JLabel bloodHeader1 = new JLabel("Blood");
        bloodHeader1.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(bloodHeader1, gbc);

        gbc.gridx = 2;
        JLabel hoursHeader1 = new JLabel("Hours since");
        hoursHeader1.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(hoursHeader1, gbc);

        gbc.gridx = 3;
        JLabel carbsHeader1 = new JLabel("Carbs");
        carbsHeader1.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(carbsHeader1, gbc);

        gbc.gridx = 4;
        JLabel exerciseHeader1 = new JLabel("Exercise");
        exerciseHeader1.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(exerciseHeader1, gbc);

        gbc.gridx = 5;
        JLabel insulinHeader1 = new JLabel("Insulin");
        insulinHeader1.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(insulinHeader1, gbc);

        gbc.gridx = 6;
        JLabel foodHeader1 = new JLabel("Food");
        foodHeader1.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(foodHeader1, gbc);

        gbc.gridx = 7;
        JLabel otherHeader1 = new JLabel("Other");
        otherHeader1.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(otherHeader1, gbc);


        // Second line of headers - still the same method as that explained for the initial 'Time' header @ the 0th x value of the grid -
        gbc.gridy = 1; // shifts all of the following headers down by 1 y-value (so these words all lie below those assigned to gbc.gridy=0 above
        gbc.gridx = 0;
        JLabel timeHeader2 = new JLabel("of day");
        timeHeader2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(timeHeader2, gbc);

        gbc.gridx = 1;
        JLabel bloodHeader2 = new JLabel("glucose (mmol/L)");
        bloodHeader2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(bloodHeader2, gbc);

        gbc.gridx = 2;
        JLabel hoursHeader2 = new JLabel("last meal");
        hoursHeader2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(hoursHeader2, gbc);

        gbc.gridx = 3;
        JLabel carbsHeader2 = new JLabel("eaten (g)");
        carbsHeader2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(carbsHeader2, gbc);

        gbc.gridx = 4;
        JLabel exerciseHeader2 = new JLabel("type and duration");
        exerciseHeader2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(exerciseHeader2, gbc);

        gbc.gridx = 5;
        JLabel insulinHeader2 = new JLabel("dose");
        insulinHeader2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(insulinHeader2, gbc);

        gbc.gridx = 6;
        JLabel foodHeader2 = new JLabel("diary");
        foodHeader2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(foodHeader2, gbc);

        gbc.gridx = 7;
        JLabel otherHeader2 = new JLabel("events");
        otherHeader2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(otherHeader2, gbc);

        /**
         Setting up the boxes which correspond to each factor (eg blood glucose) for each time of day (eg "Pre Dinner") below
         * -- AI (ChatGPT) was used for the scrollable pane at the bottom of the screen --
         */

        // Creating the data rows for inputs
        int preIndex = 0; // tracks "Pre" rows for hours - based on method in 'Home' class
        for (int i = 0; i < ROW_LABELS.length; i++) { // adds an input box for each data row of time (Pre breakfast... etc)
            gbc.gridy = i + 2; // ensures the first input box is placed below the headings (as the headings occupy y=0 and y=1)

            // Time-of-day - not input boxes, just labels to represent the time of day
            gbc.gridx = 0;
            JLabel rowLabel = new JLabel(ROW_LABELS[i] + ":");
            rowLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            centerPanel.add(rowLabel, gbc);

            gbc.gridx = 1;
            bloodSugarFields[i] = new JTextField(5);
            addNumericInputRestriction(bloodSugarFields[i]);
            centerPanel.add(bloodSugarFields[i], gbc);


            gbc.gridx = 2;
            if (ROW_LABELS[i].endsWith("Pre")) {
                hoursSinceMealFields[preIndex] = new JTextField(5);
                addNumericInputRestriction(hoursSinceMealFields[preIndex]);
                centerPanel.add(hoursSinceMealFields[preIndex], gbc);
                preIndex++;
            }

            gbc.gridx = 3;
            carbsFields[i] = new JTextField(5);
            addNumericInputRestriction(carbsFields[i]);
            centerPanel.add(carbsFields[i], gbc);

            // Adding the Exercise Type field (a multiline JTextArea) to the grid
            gbc.gridx = 4; // Setting the column index to 4 for Exercise Type
            exerciseFields[i] = new JTextArea(2, 10); // Creating a JTextArea with 2 rows and 10 columns for exercise notes
            exerciseFields[i].setLineWrap(true);     // Enabling line wrapping to prevent horizontal scrolling
            exerciseFields[i].setWrapStyleWord(true); // Wrapping at word boundaries to improve readability
            JScrollPane exerciseScrollPane = new JScrollPane(exerciseFields[i]); // Wrapping the JTextArea in a JScrollPane for scrollability - taken from ChatGPT
            exerciseScrollPane.setPreferredSize(new Dimension(100, 40)); // Setting a fixed size for the scroll pane
            centerPanel.add(exerciseScrollPane, gbc); // Adding the scroll pane to the center panel at the specified grid position

// Adding the Insulin Dose field (a single-line JTextField)
            gbc.gridx = 5; // Setting the column index to 5 for Insulin Dose
            insulinDoseFields[i] = new JTextField(5); // Creating a JTextField with a width of 5 columns for numeric input
            addNumericInputRestriction(insulinDoseFields[i]); // Restricting input to numeric values using a custom method
            centerPanel.add(insulinDoseFields[i], gbc); // Adding the JTextField to the center panel at the specified grid position

// Adding the Food Diary field (a multiline JTextArea)
            gbc.gridx = 6; // Setting the column index to 6 for Food Diary
            foodDiaryFields[i] = new JTextArea(2, 10); // Creating a JTextArea with 2 rows and 10 columns for food diary entries
            foodDiaryFields[i].setLineWrap(true); // Enabling line wrapping for better usability
            foodDiaryFields[i].setWrapStyleWord(true); // Wrapping at word boundaries for clean text display
            JScrollPane foodScrollPane = new JScrollPane(foodDiaryFields[i]); // Wrapping the JTextArea in a JScrollPane
            foodScrollPane.setPreferredSize(new Dimension(100, 40)); // Setting a fixed size for the scroll pane
            centerPanel.add(foodScrollPane, gbc); // Adding the scroll pane to the center panel at the specified grid position

// Adding the Other Events field (a multiline JTextArea)
            gbc.gridx = 7; // Setting the column index to 7 for Other Events
            otherEventsFields[i] = new JTextArea(2, 10); // Creating a JTextArea with 2 rows and 10 columns for other events
            otherEventsFields[i].setLineWrap(true); // Enabling line wrapping for readability
            otherEventsFields[i].setWrapStyleWord(true); // Wrapping at word boundaries
            JScrollPane otherScrollPane = new JScrollPane(otherEventsFields[i]); // Wrapping the JTextArea in a JScrollPane
            otherScrollPane.setPreferredSize(new Dimension(100, 40)); // Setting a fixed size for the scroll pane
            centerPanel.add(otherScrollPane, gbc);// Adding the scroll pane to the center panel at the specified grid position

        }

// Wrapping the centerPanel in a JScrollPane to enable scrolling if content exceeds the visible area
        JScrollPane scrollPane = new JScrollPane(
                centerPanel, // The panel to be scrolled
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, // Show vertical scrollbar only when needed
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED // Show horizontal scrollbar only when needed
        );

// Making the JScrollPane and its viewport transparent
        // ChatGPT helped us with the syntax to make the viewport transparent, but it's not >6 lines so not properly referenced
        scrollPane.setOpaque(false); // Allowing the main panel's background to show through
        scrollPane.getViewport().setOpaque(false); // Making the viewport (the area showing the centerPanel) transparent
        scrollPane.setBackground(new Color(0, 0, 0, 0)); // Setting a fully transparent background color
        scrollPane.getViewport().setBackground(new Color(0, 0, 0, 0)); // Setting the viewport's background to transparent

// Removing any borders from the JScrollPanel
        scrollPane.setBorder(null); // Removing the default border around the scroll pane

// Adding the scroll pane to the main panel
        mainPanel.add(scrollPane, BorderLayout.CENTER); // Placing the scroll pane in the center of the main panel

// Setting a custom layout for the JScrollPanel
        /* Reference 16 - taken from ChatGPT*/
        scrollPane.setLayout(new ScrollPaneLayout() {
            @Override
            public void layoutContainer(Container parent) {
                super.layoutContainer(parent); // Calling the default layout behavior from the parent class

                // Moving the horizontal scrollbar to the top of the scroll pane
                JScrollPane pane = (JScrollPane) parent;
                if (pane.getHorizontalScrollBar() != null) {
                    Rectangle bounds = pane.getHorizontalScrollBar().getBounds(); // Getting the scrollbar's current bounds
                    bounds.y = 420; // Moving it to the top by setting the Y-coordinate
                    pane.getHorizontalScrollBar().setBounds(bounds); // Applying the new bounds defined above

                    // Adjusting the viewport bounds to prevent overlap with the scrollbar
                    JViewport viewport = pane.getViewport();
                    Rectangle viewportBounds = viewport.getBounds(); // Getting the current viewport bounds
                    viewportBounds.y = bounds.height; // Shifting the viewport down to make space for the scrollbar
                    viewportBounds.height -= bounds.height; // Reducing the viewport's height to account for the scrollbar
                    viewport.setBounds(viewportBounds); // Applying the new bounds
                }
            }
        });
        /* end of reference 16*/

// Setting additional transparency and border properties for the scroll pane
        scrollPane.setOpaque(false); // Making the scroll pane itself transparent
        scrollPane.setBackground(Color.WHITE); // Setting a white background color
        scrollPane.getViewport().setBackground(Color.WHITE); // Setting the viewport's background to white
        scrollPane.setBorder(null); // Removing any borders from the scroll pane
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20)); // Adding padding around the content

// Adding the scroll pane to the main panel again to ensure it's displayed
        mainPanel.add(scrollPane, BorderLayout.CENTER);

// ==== BOTTOM PANEL (Save + Navigation) ====
        JPanel bottomPanel = new JPanel(); // Creating a panel for the bottom buttons
        bottomPanel.setOpaque(false); // Making the bottom panel transparent
        RoundedButtonLogin saveAllBtn = new RoundedButtonLogin("Save all", new Color(237, 165, 170)); // Creating a custom button for saving all data
        saveAllBtn.setFont(new Font("SansSerif", Font.BOLD, 16)); // Setting the font for the button text
        saveAllBtn.setForeground(Color.BLACK); // Setting the text color to black
        saveAllBtn.addActionListener(e -> handleSaveAllIntensive()); // Adding an event listener to handle the save action
        bottomPanel.add(saveAllBtn); // Adding the save button to the bottom panel

        // Setting up the navigation bar, highlighting the "Logbook" icon to indicate the current screen.
// The navBar dynamically adapts based on the `currentUser` and paths provided for icons.
        JPanel navBar = createBottomNavBar(
                "Logbook",               // Identifying the active screen
                currentUser,             // User-specific data for navigation logic
                "/Icons/home.png",       // Path for the "Home" icon
                "/Icons/logbookfull.png",// Path for the "Logbook" icon (active state)
                "/Icons/graph.png",      // Path for the "Graph" icon
                "/Icons/profile.png"     // Path for the "Profile" icon
        );

// Encasing the bottom panel and nav bar into a single wrapper panel for structural alignment.
// Using `BorderLayout` allows flexibility to place components in specific positions like NORTH and SOUTH.
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setOpaque(false); // Ensuring a transparent background to blend seamlessly with the design.
        wrapperPanel.add(bottomPanel, BorderLayout.NORTH); // Placing the save button panel at the top of the wrapper.
        wrapperPanel.add(navBar, BorderLayout.SOUTH); // Placing the navigation bar at the bottom of the wrapper.

// Adding the wrapper panel to the main panel, ensuring it occupies the bottom section of the layout.
// This addition finalizes the UI structure for the bottom portion of the interface.
        mainPanel.add(wrapperPanel, BorderLayout.SOUTH);
    }

/**
 * Restricting a text field to accept only numeric input.
 * This enhances user experience by preventing invalid entries directly at the input stage.
 */
/* Reference 17- different aspects & lines of this code were collected together from https://stackoverflow.com/questions/1313390/is-there-any-way-to-accept-only-numeric-values-in-a-jtextfield*/
        private void addNumericInputRestriction (JTextField textField){
            // Attaching a KeyListener to monitor and process every key typed in the field.
            textField.addKeyListener(new KeyAdapter() {
                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar(); // Capturing the character typed by the user.
                    // Rejecting any input that is not a digit, a decimal point, or the backspace key.
                    if (!Character.isDigit(c) && c != '.' && c != KeyEvent.VK_BACK_SPACE) {
                        e.consume(); // Consuming the event prevents invalid characters from appearing.
                    }
                }
            });
        }
        /* end of reference 17*/

/**
 * Applying a restriction for alphabetic input in a text field.
 * This is particularly useful for fields where numerical or special characters are irrelevant.
 * -- AI (ChatGPT) was used for the if loop --
 */
        private void addAlphabeticInputRestriction (JTextField textField){
            // Utilizing a KeyListener to filter out unwanted characters at the typing stage.
            textField.addKeyListener(new KeyAdapter() {
                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar(); // Identifying the character entered by the user.
                    // Permitting only alphabetic characters, spaces, and backspace.
                    if (!Character.isLetter(c) && c != ' ' && c != KeyEvent.VK_BACK_SPACE) {
                        e.consume(); // Ignoring invalid keystrokes by consuming the event.
                    }
                }
            });
        }

/**
 * Loading log entries for the "Intensive" logbook view, including data like hours since last meal.
 * This method retrieves data from the database and populates the corresponding UI fields.
 */
        protected void loadLogEntriesIntensive () {
            // Fetching all log entries for the current user and the target date.
            List<LogEntry> entries = LogService.getEntriesForDate(currentUser.getId(), targetDate);

            // Creating a map to associate each log entry with its respective time of day.
            Map<String, LogEntry> entryMap = new HashMap<>();
            for (LogEntry e : entries) {
                entryMap.put(e.getTimeOfDay(), e); // Mapping entries using the time of day as the key.
            }

            int preIndex = 0; // Tracking "Pre" row indices for handling hoursSinceMeal fields.
            for (int i = 0; i < ROW_LABELS.length; i++) {
                LogEntry e = entryMap.get(ROW_LABELS[i]); // Retrieving the log entry for the current row label.
                if (e != null) {
                    // Filling in the corresponding UI fields with data from the log entry.
                    bloodSugarFields[i].setText(String.valueOf(e.getBloodSugar())); // Populating blood sugar data.
                    carbsFields[i].setText(String.valueOf(e.getCarbsEaten())); // Adding carbohydrate consumption.
                    exerciseFields[i].setText(e.getExerciseType() == null ? "" : e.getExerciseType()); // Setting exercise details.
                    insulinDoseFields[i].setText(String.valueOf(e.getInsulinDose())); // Displaying insulin dose.
                    foodDiaryFields[i].setText(e.getFoodDetails() == null ? "" : e.getFoodDetails()); // Filling in the food diary field.
                    otherEventsFields[i].setText(e.getOtherMedications() == null ? "" : e.getOtherMedications()); // Adding other events or medications.

                    // Handling the "Pre" rows that include hours since the last meal.
                    if (ROW_LABELS[i].endsWith("Pre")) {
                        hoursSinceMealFields[preIndex].setText(String.valueOf(e.getHoursSinceMeal()));
                        preIndex++; // Incrementing the index to move to the next "Pre" row.
                    }
                }
            }
        }




        private String getLogbookType(User user) {
        String logbookType = user.getLogbookType(); // Assuming `User` has a `getLogbookType()` method
        return logbookType != null ? logbookType : "Intensive";
    }

    protected String formatDate(String date) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd"); // Adjust based on your input format
            SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, MMM d"); // Desired format
            return outputFormat.format(inputFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return date; // Fallback to the original date if parsing fails
        }
    }

    /**
     * Save data from the "Intensive" logbook; triggers Alarm via LogService.
     */
    protected void handleSaveAllIntensive() {
        int preIndex = 0;
        for (int i = 0; i < ROW_LABELS.length; i++) {
            double bg = parseDoubleSafe(bloodSugarFields[i].getText());
            double carbs = parseDoubleSafe(carbsFields[i].getText());
            String exercise = exerciseFields[i].getText();
            double insulin = parseDoubleSafe(insulinDoseFields[i].getText());
            String food = foodDiaryFields[i].getText();
            String other = otherEventsFields[i].getText();

            int hours = 0;
            if (ROW_LABELS[i].endsWith("Pre")) {
                hours = parseIntSafe(hoursSinceMealFields[preIndex].getText());
                preIndex++; //goes through every pre row label
            }

            // Only save if something is entered and the input is valis
            if (bg > 0 || carbs > 0 || !exercise.isEmpty() || insulin > 0
                    || !food.isEmpty() || !other.isEmpty() || hours > 0) {
                LogEntry entry = new LogEntry();
                entry.setUserId(currentUser.getId());
                entry.setDate(targetDate);
                entry.setTimeOfDay(ROW_LABELS[i]);
                entry.setBloodSugar(bg);
                entry.setCarbsEaten(carbs);
                entry.setExerciseType(exercise);
                entry.setInsulinDose(insulin);
                entry.setFoodDetails(food);
                entry.setOtherMedications(other);
                entry.setHoursSinceMeal(hours);

                // This automatically triggers the alarm if out of range
                LogService.createEntry(entry, currentUser);
            }
        }

        JOptionPane.showMessageDialog(this,
                "All entered values have been saved (Intensive).",
                "Logbook Saved",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Safely parsing a string to a double, handling invalid input gracefully.
     *
     * @param text The string to be parsed into a double.
     *             This value can represent numbers entered by the user.
     * @return The parsed double value if the string contains valid numerical content;
     *         otherwise, returns 0.0 if the input is invalid or unparsable.
     */
    private double parseDoubleSafe(String text) {
        try {
            // Attempting to convert the string into a double using the built-in Double.parseDouble method.
            // If the input is a valid numerical string (e.g., "3.14"), this will return its double value.
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            // Handling cases where the input string is not a valid double (e.g., "abc" or null).
            // Returning 0.0 ensures the application doesn't crash and provides a default value for invalid input.
            return 0.0;
        }
    }

    /**
     * Safely parsing a string to an integer, ensuring the application doesn't crash on invalid input.
     *
     * @param text The string to be parsed into an integer.
     *             Typically, this string represents whole numbers entered by the user.
     * @return The parsed integer value if the input is valid;
     *         otherwise, returns 0 if the input is invalid or unparsable.
     */
    private int parseIntSafe(String text) {
        try {
            // Converting the string to an integer using the Integer.parseInt method.
            // For valid inputs (e.g., "42"), this will return the integer representation.
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            // Managing invalid inputs gracefully, such as non-numeric strings or null values.
            // Returning 0 ensures functionality is maintained without throwing exceptions.
            return 0;
        }
    }

}

