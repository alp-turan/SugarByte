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

    // Arrays for each column
    protected JTextField[] bloodSugarFields   = new JTextField[7];
    protected JTextField[] carbsFields        = new JTextField[7];
    protected JTextField[] exerciseFields     = new JTextField[7];
    protected JTextField[] insulinDoseFields  = new JTextField[7];
    // Hours Since Last Meal is only relevant for "Pre" rows
    protected JTextField[] hoursSinceMealFields = new JTextField[3];

    public ComprehensiveLogbook(User user, String date) {
        super("Comprehensive Logbook for " + date);
        this.currentUser = user;
        this.targetDate  = date;

        buildUIComprehensive();
        loadLogEntriesComprehensive();
        setVisible(true);
    }

    /**
     * Build the "Comprehensive" UI with columns:
     * Time of Day | Blood Glucose | Carbs Eaten | Exercise Type | Insulin Dose | Hours Since Last Meal
     * and place it in a JScrollPane for scrolling.
     */
    protected void buildUIComprehensive() {
        // Main gradient background
        JPanel mainPanel = createGradientPanel(Color.WHITE, Color.WHITE);
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);

        // ===== TOP PANEL =====
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel titleLabel = createTitleLabel("SugarByte", lobsterFont, Color.BLACK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(titleLabel);

        JLabel dateLabel = new JLabel("Logbook for " + targetDate, SwingConstants.CENTER);
        dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        dateLabel.setForeground(Color.BLACK);
        dateLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(dateLabel);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // ===== CENTER PANEL (GridBagLayout) =====
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // First row of headers
        gbc.gridy = 0;
        gbc.gridx = 0;
        JLabel timeHeaderLine1 = new JLabel("Time");
        timeHeaderLine1.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(timeHeaderLine1, gbc);

        gbc.gridx = 1;
        JLabel bloodHeaderLine1 = new JLabel("Blood");
        bloodHeaderLine1.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(bloodHeaderLine1, gbc);

        gbc.gridx = 2;
        JLabel carbsHeaderLine1 = new JLabel("Carbs");
        carbsHeaderLine1.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(carbsHeaderLine1, gbc);

        gbc.gridx = 3;
        JLabel exerciseHeaderLine1 = new JLabel("Exercise");
        exerciseHeaderLine1.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(exerciseHeaderLine1, gbc);

        gbc.gridx = 4;
        JLabel insulinHeaderLine1 = new JLabel("Insulin");
        insulinHeaderLine1.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(insulinHeaderLine1, gbc);

        gbc.gridx = 5;
        JLabel hoursHeaderLine1 = new JLabel("Hours Since");
        hoursHeaderLine1.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(hoursHeaderLine1, gbc);

        // Second row of headers
        gbc.gridy = 1;
        gbc.gridx = 0;
        JLabel timeHeaderLine2 = new JLabel("of Day");
        timeHeaderLine2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(timeHeaderLine2, gbc);

        gbc.gridx = 1;
        JLabel bloodHeaderLine2 = new JLabel("Glucose");
        bloodHeaderLine2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(bloodHeaderLine2, gbc);

        gbc.gridx = 2;
        JLabel carbsHeaderLine2 = new JLabel("Eaten");
        carbsHeaderLine2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(carbsHeaderLine2, gbc);

        gbc.gridx = 3;
        JLabel exerciseHeaderLine2 = new JLabel("Type");
        exerciseHeaderLine2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(exerciseHeaderLine2, gbc);

        gbc.gridx = 4;
        JLabel insulinHeaderLine2 = new JLabel("Dose");
        insulinHeaderLine2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(insulinHeaderLine2, gbc);

        gbc.gridx = 5;
        JLabel hoursHeaderLine2 = new JLabel("Last Meal");
        hoursHeaderLine2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(hoursHeaderLine2, gbc);

        // Data rows
        int preIndex = 0; // track how many "Pre" rows we've encountered
        for (int i = 0; i < ROW_LABELS.length; i++) {
            gbc.gridy = i + 2; // start from row 2

            // Column 0: Time-of-day label
            gbc.gridx = 0;
            JLabel rowLabel = new JLabel(ROW_LABELS[i] + ":");
            rowLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            centerPanel.add(rowLabel, gbc);

            // Column 1: BloodSugar
            gbc.gridx = 1;
            bloodSugarFields[i] = new JTextField(5);
            centerPanel.add(bloodSugarFields[i], gbc);

            // Column 2: Carbs
            gbc.gridx = 2;
            carbsFields[i] = new JTextField(5);
            centerPanel.add(carbsFields[i], gbc);

            // Column 3: Exercise
            gbc.gridx = 3;
            exerciseFields[i] = new JTextField(5);
            centerPanel.add(exerciseFields[i], gbc);

            // Column 4: Insulin
            gbc.gridx = 4;
            insulinDoseFields[i] = new JTextField(5);
            centerPanel.add(insulinDoseFields[i], gbc);

            // Column 5: Hours Since Meal (only for Pre)
            gbc.gridx = 5;
            if (ROW_LABELS[i].endsWith("Pre")) {
                hoursSinceMealFields[preIndex] = new JTextField(5);
                centerPanel.add(hoursSinceMealFields[preIndex], gbc);
                preIndex++;
            } else {
                centerPanel.add(new JLabel("—"), gbc);
            }
        }

        // Put centerPanel in a JScrollPane for scrollable UI
        JScrollPane scrollPane = new JScrollPane(
                centerPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        scrollPane.setOpaque(false);
        // Note: the panel inside won't be transparent by default;
        // you can customize if you want a gradient behind the scrollpane or not

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // ===== BOTTOM PANEL (Save + Nav) =====
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        JButton saveAllBtn = new JButton("Save All");
        saveAllBtn.setBackground(new Color(237, 165, 170));
        saveAllBtn.setForeground(Color.BLACK);
        saveAllBtn.addActionListener(e -> handleSaveAllComprehensive());
        bottomPanel.add(saveAllBtn);

        JPanel navBar = createBottomNavBar("Logbook", currentUser,
                "/Icons/home.png", "/Icons/logbookfull.png", "/Icons/profile.png");

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(bottomPanel, BorderLayout.NORTH);
        wrapperPanel.add(navBar, BorderLayout.SOUTH);

        mainPanel.add(wrapperPanel, BorderLayout.SOUTH);
    }

    /**
     * Load data from DB (including hoursSinceMeal, etc.) for the "Comprehensive" style
     */
    protected void loadLogEntriesComprehensive() {
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
                exerciseFields[i].setText(e.getExerciseType() == null ? "" : e.getExerciseType());
                insulinDoseFields[i].setText(String.valueOf(e.getInsulinDose()));

                if (ROW_LABELS[i].endsWith("Pre")) {
                    hoursSinceMealFields[preIndex].setText(String.valueOf(e.getHoursSinceMeal()));
                    preIndex++;
                }
            }
        }
    }

    /**
     * Save all data from the comprehensive form; triggers the alarm automatically
     * via LogService.createEntry(...).
     */
    protected void handleSaveAllComprehensive() {
        int preIndex = 0; // to track "Pre" rows for hoursSinceMeal
        for (int i = 0; i < ROW_LABELS.length; i++) {
            double bg       = parseDoubleSafe(bloodSugarFields[i].getText());
            double carbs    = parseDoubleSafe(carbsFields[i].getText());
            String exercise = exerciseFields[i].getText();
            double insulin  = parseDoubleSafe(insulinDoseFields[i].getText());
            int hours = 0;

            if (ROW_LABELS[i].endsWith("Pre")) {
                hours = parseIntSafe(hoursSinceMealFields[preIndex].getText());
                preIndex++;
            }

            // Only create/update if there's some content
            if (bg > 0 || carbs > 0 || !exercise.isEmpty() || insulin > 0 || hours > 0) {
                LogEntry entry = new LogEntry();
                entry.setUserId(currentUser.getId());
                entry.setDate(targetDate);
                entry.setTimeOfDay(ROW_LABELS[i]);
                entry.setBloodSugar(bg);
                entry.setCarbsEaten(carbs);
                entry.setExerciseType(exercise);
                entry.setInsulinDose(insulin);
                entry.setHoursSinceMeal(hours);
                entry.setFoodDetails("Comprehensive Logbook Entry: " + ROW_LABELS[i]);

                // This calls AlarmService.checkAndSendAlarm() behind the scenes
                LogService.createEntry(entry, currentUser);
            }
        }

        JOptionPane.showMessageDialog(this,
                "All entered values have been saved (Comprehensive).",
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