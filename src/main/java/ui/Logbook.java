package ui;

import model.LogEntry;
import model.User;
import service.LogService;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple Logbook page with 7 rows (Breakfast Pre/Post,
 * Lunch Pre/Post, Dinner Pre/Post, Bedtime).
 * Now uses two-line headers for each column:
 *
 *   1) Time  / of Day
 *   2) Blood / Glucose
 *   3) Carbs / Eaten
 *   4) Hours Since / Last Meal
 *
 * The last column shows a textfield only for "Pre" rows.
 */
public class Logbook extends BaseUI {

    private final User currentUser;
    private final String targetDate;

    // Existing columns
    private JTextField[] bloodSugarFields = new JTextField[7];
    private JTextField[] carbsFields      = new JTextField[7];

    // Newly added column (only used for "Pre" rows)
    private JTextField[] hoursSinceMealFields = new JTextField[3]; // 3 "Pre" rows

    // Row labels for clarity
    private static final String[] ROW_LABELS = {
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
        this.targetDate  = date;

        buildUI();
        loadLogEntries(); // Load existing blood-sugar/carb data
        setVisible(true);
    }

    private void buildUI() {
        // Main gradient background from BaseUI
        JPanel mainPanel = createGradientPanel(Color.WHITE, Color.WHITE);
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);

        // Top: SugarByte + "Logbook for (date)"
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // SugarByte title
        JLabel titleLabel = createTitleLabel("SugarByte", lobsterFont, Color.BLACK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(titleLabel);

        // Logbook for (date)
        JLabel dateLabel = new JLabel("Logbook for " + targetDate, SwingConstants.CENTER);
        dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        dateLabel.setForeground(Color.BLACK);
        dateLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(dateLabel);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center panel: table with multiple rows
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // =====================
        // HEADER ROW - FIRST LINE
        // =====================
        // "Time"
        JLabel timeOfDayHeaderLine1 = new JLabel("Time");
        timeOfDayHeaderLine1.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(timeOfDayHeaderLine1, gbc);

        // "Blood"
        gbc.gridx = 1;
        JLabel bloodHeaderLine1 = new JLabel("Blood");
        bloodHeaderLine1.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(bloodHeaderLine1, gbc);

        // "Carbs"
        gbc.gridx = 2;
        JLabel carbsHeaderLine1 = new JLabel("Carbs");
        carbsHeaderLine1.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(carbsHeaderLine1, gbc);

        // "Hours Since"
        gbc.gridx = 3;
        JLabel hoursHeaderLine1 = new JLabel("Hours Since");
        hoursHeaderLine1.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(hoursHeaderLine1, gbc);

        // =====================
        // HEADER ROW - SECOND LINE
        // =====================
        // Move to next row
        gbc.gridy = 1;
        gbc.gridx = 0;
        JLabel timeOfDayHeaderLine2 = new JLabel("of Day");
        timeOfDayHeaderLine2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(timeOfDayHeaderLine2, gbc);

        gbc.gridx = 1;
        JLabel bloodHeaderLine2 = new JLabel("Glucose");
        bloodHeaderLine2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(bloodHeaderLine2, gbc);

        gbc.gridx = 2;
        JLabel carbsHeaderLine2 = new JLabel("Eaten");
        carbsHeaderLine2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(carbsHeaderLine2, gbc);

        gbc.gridx = 3;
        JLabel hoursHeaderLine2 = new JLabel("Last Meal");
        hoursHeaderLine2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(hoursHeaderLine2, gbc);
        // =====================
        // HEADER ROW - THIRD LINE (relevant for hours since last meal)
        // =====================
        // Move to next row
        gbc.gridy = 2;
        gbc.gridx = 1;
        JLabel bloodHeaderLine3 = new JLabel("(mmol/L)");
        bloodHeaderLine3.setFont(new Font("SansSerif", Font.BOLD, 10));
        centerPanel.add(bloodHeaderLine3, gbc);

        gbc.gridx = 2;
        JLabel carbsHeaderLine3 = new JLabel("(grams)");
        carbsHeaderLine3.setFont(new Font("SansSerif", Font.BOLD, 10));
        centerPanel.add(carbsHeaderLine3, gbc);

        gbc.gridx = 3;
        JLabel hoursHeaderLine3 = new JLabel("(to the closest hour)");
        hoursHeaderLine3.setFont(new Font("SansSerif", Font.BOLD, 10));
        centerPanel.add(hoursHeaderLine3, gbc);


        // ====================
        // DATA ROWS START AT gbc.gridy = 3
        // =====================
        int preRowIndex = 0; // Index for "Pre" rows
        for (int i = 0; i < ROW_LABELS.length; i++) {
            gbc.gridy = i + 3; // shift down by 2 rows (header lines used 0,1 & 2)

            // 1) Time-of-day label in col 0
            gbc.gridx = 0;
            JLabel rowLabel = new JLabel(ROW_LABELS[i] + ":");
            rowLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            centerPanel.add(rowLabel, gbc);

            // 2) Blood sugar field in col 1
            bloodSugarFields[i] = new JTextField(5);
            gbc.gridx = 1;
            centerPanel.add(bloodSugarFields[i], gbc);

            // 3) Carbs field in col 2
            carbsFields[i] = new JTextField(5);
            gbc.gridx = 2;
            centerPanel.add(carbsFields[i], gbc);

            // 4) Hours Since Last Meal field in col 3
            gbc.gridx = 3;
            if (ROW_LABELS[i].endsWith("Pre")) {
                hoursSinceMealFields[preRowIndex] = new JTextField(5);
                centerPanel.add(hoursSinceMealFields[preRowIndex], gbc);
                preRowIndex++; // Increment for next "Pre" row
            } else {
                centerPanel.add(new JLabel("—"), gbc);
            }
        }

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // "Save All" button at bottom
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        JButton saveAllBtn = new JButton("Save All");
        saveAllBtn.setBackground(new Color(237, 165, 170));
        saveAllBtn.setForeground(Color.BLACK);
        saveAllBtn.addActionListener(e -> handleSaveAll());
        bottomPanel.add(saveAllBtn);

        // Bottom Nav Bar
        JPanel navBar = createBottomNavBar("Logbook", currentUser,
                "/Icons/home.png", "/Icons/logbookfull.png", "/Icons/profile.png");

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(bottomPanel, BorderLayout.NORTH);
        wrapperPanel.add(navBar, BorderLayout.SOUTH);

        mainPanel.add(wrapperPanel, BorderLayout.SOUTH);
    }

    private void loadLogEntries() {
        List<LogEntry> entries = LogService.getEntriesForDate(currentUser.getId(), targetDate);

        Map<String, LogEntry> entryMap = new HashMap<>();
        for (LogEntry entry : entries) {
            entryMap.put(entry.getTimeOfDay(), entry);
        }

        int preRowIndex = 0; // Index for "Pre" rows
        for (int i = 0; i < ROW_LABELS.length; i++) {
            LogEntry entry = entryMap.get(ROW_LABELS[i]);
            if (entry != null) {
                bloodSugarFields[i].setText(String.valueOf(entry.getBloodSugar()));
                carbsFields[i].setText(String.valueOf(entry.getCarbsEaten()));

                if (ROW_LABELS[i].endsWith("Pre")) {
                    hoursSinceMealFields[preRowIndex].setText(String.valueOf((int) entry.getHoursSinceMeal()));
                    preRowIndex++;
                }
            }
        }
    }

    private void handleSaveAll() {
        int preRowIndex = 0; // Index for "Pre" rows
        for (int i = 0; i < ROW_LABELS.length; i++) {
            double bg = parseDoubleSafe(bloodSugarFields[i].getText());
            double carbs = parseDoubleSafe(carbsFields[i].getText());
            int hours = 0;

            if (ROW_LABELS[i].endsWith("Pre")) {
                hours = parseIntSafe(hoursSinceMealFields[preRowIndex].getText());
                preRowIndex++;
            }

            if (bg > 0 || carbs > 0) {
                LogEntry entry = new LogEntry();
                entry.setUserId(currentUser.getId());
                entry.setDate(targetDate);
                entry.setTimeOfDay(ROW_LABELS[i]);
                entry.setBloodSugar(bg);
                entry.setCarbsEaten(carbs);
                entry.setHoursSinceMeal(hours);
                entry.setFoodDetails("Logbook entry - " + ROW_LABELS[i]);

                LogService.createEntry(entry, currentUser);
            }
        }

        JOptionPane.showMessageDialog(this,
                "All entered values have been saved.",
                "Logbook Saved",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private double parseDoubleSafe(String text) {
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
}
