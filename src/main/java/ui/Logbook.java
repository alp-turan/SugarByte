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
        super("Logbook for " + date);
        this.currentUser = user;
        this.targetDate = date;

        buildUI();
        loadLogEntries();
        setVisible(true);
    }

    /**
     * Build the "simple" UI
     */
    protected void buildUI() {
        // Main gradient background
        JPanel mainPanel = createGradientPanel(Color.WHITE, Color.WHITE);
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);

        // Top panel: Title + date
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(70, 0, 20, 0));

        JLabel titleLabel = createTitleLabel("SugarByte", lobsterFont, Color.BLACK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(titleLabel);

        String logbookType = getLogbookType(currentUser);
        String formattedDate = formatDate(targetDate);

        JLabel dateLabel = new JLabel(logbookType + " logbook for " + formattedDate, SwingConstants.CENTER);
        dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        dateLabel.setForeground(Color.BLACK);
        dateLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(dateLabel);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center panel: table
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 7, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Two-line header
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel timeOfDayHeaderLine1 = new JLabel("Time");
        timeOfDayHeaderLine1.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(timeOfDayHeaderLine1, gbc);

        gbc.gridx = 1;
        JLabel bloodHeaderLine1 = new JLabel("Blood");
        bloodHeaderLine1.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(bloodHeaderLine1, gbc);

        gbc.gridx = 2;
        JLabel carbsHeaderLine1 = new JLabel("Carbs");
        carbsHeaderLine1.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(carbsHeaderLine1, gbc);

        gbc.gridx = 3;
        JLabel hoursHeaderLine1 = new JLabel("Hours since");
        hoursHeaderLine1.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(hoursHeaderLine1, gbc);

        // Second line
        gbc.gridy = 1;
        gbc.gridx = 0;
        JLabel timeOfDayHeaderLine2 = new JLabel("of day");
        timeOfDayHeaderLine2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(timeOfDayHeaderLine2, gbc);

        gbc.gridx = 1;
        JLabel bloodHeaderLine2 = new JLabel("glucose (mmol/L)");
        bloodHeaderLine2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(bloodHeaderLine2, gbc);

        gbc.gridx = 2;
        JLabel carbsHeaderLine2 = new JLabel("eaten (g)");
        carbsHeaderLine2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(carbsHeaderLine2, gbc);

        gbc.gridx = 3;
        JLabel hoursHeaderLine2 = new JLabel("last meal");
        hoursHeaderLine2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(hoursHeaderLine2, gbc);

        // Data rows
        int preIndex = 0;
        for (int i = 0; i < ROW_LABELS.length; i++) {
            gbc.gridy = i + 2;

            // Column 0
            gbc.gridx = 0;
            JLabel rowLabel = new JLabel(ROW_LABELS[i] + ":");
            rowLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            centerPanel.add(rowLabel, gbc);

            // Column 1 - BloodSugar
            gbc.gridx = 1;
            bloodSugarFields[i] = new JTextField(5);
            addNumericInputRestriction(bloodSugarFields[i]); // Apply restriction here
            centerPanel.add(bloodSugarFields[i], gbc);

            // Column 2 - Carbs
            gbc.gridx = 2;
            carbsFields[i] = new JTextField(5);
            addNumericInputRestriction(carbsFields[i]); // Apply restriction here
            centerPanel.add(carbsFields[i], gbc);

            // Column 3 - Hours for Pre
            gbc.gridx = 3;
            if (ROW_LABELS[i].endsWith("Pre")) {
                hoursSinceMealFields[preIndex] = new JTextField(5);
                addNumericInputRestriction(hoursSinceMealFields[preIndex]); // Apply restriction here
                centerPanel.add(hoursSinceMealFields[preIndex], gbc);
                preIndex++;
            }
        }

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel with "Save All" Button (moved above nav bar)
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(Box.createVerticalStrut(60)); // Adjust the value to control vertical space above the button

        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));  // Center the button

        RoundedButtonLogin saveAllBtn = new RoundedButtonLogin("Save all", new Color(237, 165, 170));
        saveAllBtn.setForeground(Color.BLACK);
        saveAllBtn.setFont(new Font("SansSerif", Font.BOLD, 14));  // Adjust text size and font
        saveAllBtn.addActionListener(e -> handleSaveAll());

        bottomPanel.add(saveAllBtn);  // Add button to bottom panel

        // Combine bottomPanel and navBar
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BorderLayout());
        southPanel.setOpaque(false);
        southPanel.add(bottomPanel, BorderLayout.CENTER);
        southPanel.add(createBottomNavBar("Logbook", currentUser,
                        "/Icons/home.png", "/Icons/logbookfull.png", "/Icons/graph.png", "/Icons/profile.png"),
                BorderLayout.SOUTH);

        // Add combined panel to mainPanel
        mainPanel.add(southPanel, BorderLayout.SOUTH);
    }

    /**
     * Load data for "simple" logbook.
     */
    protected void loadLogEntries() {
        List<LogEntry> entries = LogService.getEntriesForDate(currentUser.getId(), targetDate);
        Map<String, LogEntry> entryMap = new HashMap<>();
        for (LogEntry entry : entries) {
            entryMap.put(entry.getTimeOfDay(), entry);
        }

        int preIndex = 0;
        for (int i = 0; i < ROW_LABELS.length; i++) {
            LogEntry e = entryMap.get(ROW_LABELS[i]);
            if (e != null) {
                bloodSugarFields[i].setText(String.valueOf(e.getBloodSugar()));
                carbsFields[i].setText(String.valueOf(e.getCarbsEaten()));
                if (ROW_LABELS[i].endsWith("Pre")) {
                    hoursSinceMealFields[preIndex].setText(String.valueOf(e.getHoursSinceMeal()));
                    preIndex++;
                }
            }
        }
    }

    /**
     * Save data for "simple" logbook.
     */
    protected void handleSaveAll() {
        int preIndex = 0;
        for (int i = 0; i < ROW_LABELS.length; i++) {
            double bg = parseDoubleSafe(bloodSugarFields[i].getText());
            double carbs = parseDoubleSafe(carbsFields[i].getText());
            int hours = 0;

            if (ROW_LABELS[i].endsWith("Pre")) {
                hours = parseIntSafe(hoursSinceMealFields[preIndex].getText());
                preIndex++;
            }

            // Only save if there's some content
            if (bg > 0 || carbs > 0 || hours > 0) {
                LogEntry entry = new LogEntry();
                entry.setUserId(currentUser.getId());
                entry.setDate(targetDate);
                entry.setTimeOfDay(ROW_LABELS[i]);
                entry.setBloodSugar(bg);
                entry.setCarbsEaten(carbs);
                entry.setHoursSinceMeal(hours);

                LogService.createEntry(entry, currentUser);
            }
        }

        JOptionPane.showMessageDialog(this,
                "All entered values have been saved.",
                "Logbook Saved",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private String getLogbookType(User user) {
        String logbookType = user.getLogbookType(); // Assuming `User` has a `getLogbookType()` method
        return logbookType != null ? logbookType : "Simple";
    }

    /**
     * Format the date for display.
     */
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

    protected double parseDoubleSafe(String text) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private int parseIntSafe(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Add numeric input restriction to a field and ensure no negative numbers are allowed.
     */
    /*Reference 18: https://stackoverflow.com/questions/33942862/keylistener-wish-to-input-only-number-but-it-let-me-input-a-letter */
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
                    e.consume();  // If the text is empty or already contains a negative sign, don't allow further negative sign
                }
            }
        });
    }
    /*end of reference 18*/
}