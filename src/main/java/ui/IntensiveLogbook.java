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
 * Now scrollable if needed via JScrollPane.
 */
public class IntensiveLogbook extends BaseUI {

    protected User currentUser;
    protected String targetDate;

    // 7 standard rows
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
    protected JTextField[] bloodSugarFields  = new JTextField[7];
    protected JTextField[] carbsFields       = new JTextField[7];
    protected JTextField[] exerciseFields    = new JTextField[7];
    protected JTextField[] insulinDoseFields = new JTextField[7];
    protected JTextField[] foodDiaryFields   = new JTextField[7];
    protected JTextField[] otherEventsFields = new JTextField[7];
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
     * Build the "Intensive" UI with columns:
     * Time of Day | Blood Glucose | Carbs Eaten | Exercise | Insulin | Food | Other | Hours Since Last Meal
     * and place in a JScrollPane.
     */
    protected void buildUIIntensive() {
        JPanel mainPanel = createGradientPanel(Color.WHITE, Color.WHITE);
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);

        // ==== TOP ====
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

        // ==== CENTER (GridBag) ====
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // First line of headers
        gbc.gridy = 0;
        gbc.gridx = 0;
        JLabel timeHeader1 = new JLabel("Time");
        timeHeader1.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(timeHeader1, gbc);

        gbc.gridx = 1;
        JLabel bloodHeader1 = new JLabel("Blood");
        bloodHeader1.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(bloodHeader1, gbc);

        gbc.gridx = 2;
        JLabel carbsHeader1 = new JLabel("Carbs");
        carbsHeader1.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(carbsHeader1, gbc);

        gbc.gridx = 3;
        JLabel exerciseHeader1 = new JLabel("Exercise");
        exerciseHeader1.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(exerciseHeader1, gbc);

        gbc.gridx = 4;
        JLabel insulinHeader1 = new JLabel("Insulin");
        insulinHeader1.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(insulinHeader1, gbc);

        gbc.gridx = 5;
        JLabel foodHeader1 = new JLabel("Food");
        foodHeader1.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(foodHeader1, gbc);

        gbc.gridx = 6;
        JLabel otherHeader1 = new JLabel("Other");
        otherHeader1.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(otherHeader1, gbc);

        gbc.gridx = 7;
        JLabel hoursHeader1 = new JLabel("Hours Since");
        hoursHeader1.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(hoursHeader1, gbc);

        // Second line of headers
        gbc.gridy = 1;
        gbc.gridx = 0;
        JLabel timeHeader2 = new JLabel("of Day");
        timeHeader2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(timeHeader2, gbc);

        gbc.gridx = 1;
        JLabel bloodHeader2 = new JLabel("Glucose");
        bloodHeader2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(bloodHeader2, gbc);

        gbc.gridx = 2;
        JLabel carbsHeader2 = new JLabel("Eaten");
        carbsHeader2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(carbsHeader2, gbc);

        gbc.gridx = 3;
        JLabel exerciseHeader2 = new JLabel("Type");
        exerciseHeader2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(exerciseHeader2, gbc);

        gbc.gridx = 4;
        JLabel insulinHeader2 = new JLabel("Dose");
        insulinHeader2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(insulinHeader2, gbc);

        gbc.gridx = 5;
        JLabel foodHeader2 = new JLabel("Diary");
        foodHeader2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(foodHeader2, gbc);

        gbc.gridx = 6;
        JLabel otherHeader2 = new JLabel("Events");
        otherHeader2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(otherHeader2, gbc);

        gbc.gridx = 7;
        JLabel hoursHeader2 = new JLabel("Last Meal");
        hoursHeader2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(hoursHeader2, gbc);

        // Data rows
        int preIndex = 0; // track "Pre" rows for hours
        for (int i = 0; i < ROW_LABELS.length; i++) {
            gbc.gridy = i + 2;

            // Time-of-day
            gbc.gridx = 0;
            JLabel rowLabel = new JLabel(ROW_LABELS[i] + ":");
            rowLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            centerPanel.add(rowLabel, gbc);

            gbc.gridx = 1;
            bloodSugarFields[i] = new JTextField(5);
            centerPanel.add(bloodSugarFields[i], gbc);

            gbc.gridx = 2;
            carbsFields[i] = new JTextField(5);
            centerPanel.add(carbsFields[i], gbc);

            gbc.gridx = 3;
            exerciseFields[i] = new JTextField(5);
            centerPanel.add(exerciseFields[i], gbc);

            gbc.gridx = 4;
            insulinDoseFields[i] = new JTextField(5);
            centerPanel.add(insulinDoseFields[i], gbc);

            gbc.gridx = 5;
            foodDiaryFields[i] = new JTextField(5);
            centerPanel.add(foodDiaryFields[i], gbc);

            gbc.gridx = 6;
            otherEventsFields[i] = new JTextField(5);
            centerPanel.add(otherEventsFields[i], gbc);

            gbc.gridx = 7;
            if (ROW_LABELS[i].endsWith("Pre")) {
                hoursSinceMealFields[preIndex] = new JTextField(5);
                centerPanel.add(hoursSinceMealFields[preIndex], gbc);
                preIndex++;
            } else {
                centerPanel.add(new JLabel("—"), gbc);
            }
        }

        // Wrap centerPanel in a JScrollPane
        JScrollPane scrollPane = new JScrollPane(
                centerPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        scrollPane.setOpaque(false);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // ==== BOTTOM (Save + Nav) ====
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        JButton saveAllBtn = new JButton("Save All");
        saveAllBtn.setBackground(new Color(237, 165, 170));
        saveAllBtn.setForeground(Color.BLACK);
        saveAllBtn.addActionListener(e -> handleSaveAllIntensive());
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
     * Load data for "Intensive" logbook, including hoursSinceMeal.
     */
    protected void loadLogEntriesIntensive() {
        List<LogEntry> entries = LogService.getEntriesForDate(currentUser.getId(), targetDate);
        Map<String, LogEntry> entryMap = new HashMap<>();
        for (LogEntry e : entries) {
            entryMap.put(e.getTimeOfDay(), e);
        }

        int preIndex = 0;
        for (int i = 0; i < ROW_LABELS.length; i++) {
            LogEntry e = entryMap.get(ROW_LABELS[i]);
            if (e != null) {
                bloodSugarFields[i].setText(String.valueOf(e.getBloodSugar()));
                carbsFields[i].setText(String.valueOf(e.getCarbsEaten()));
                exerciseFields[i].setText(e.getExerciseType() == null ? "" : e.getExerciseType());
                insulinDoseFields[i].setText(String.valueOf(e.getInsulinDose()));
                foodDiaryFields[i].setText(e.getFoodDetails() == null ? "" : e.getFoodDetails());
                otherEventsFields[i].setText(e.getOtherMedications() == null ? "" : e.getOtherMedications());

                if (ROW_LABELS[i].endsWith("Pre")) {
                    hoursSinceMealFields[preIndex].setText(String.valueOf(e.getHoursSinceMeal()));
                    preIndex++;
                }
            }
        }
    }

    /**
     * Save data from the "Intensive" logbook; triggers Alarm via LogService.
     */
    protected void handleSaveAllIntensive() {
        int preIndex = 0;
        for (int i = 0; i < ROW_LABELS.length; i++) {
            double bg       = parseDoubleSafe(bloodSugarFields[i].getText());
            double carbs    = parseDoubleSafe(carbsFields[i].getText());
            String exercise = exerciseFields[i].getText();
            double insulin  = parseDoubleSafe(insulinDoseFields[i].getText());
            String food     = foodDiaryFields[i].getText();
            String other    = otherEventsFields[i].getText();

            int hours = 0;
            if (ROW_LABELS[i].endsWith("Pre")) {
                hours = parseIntSafe(hoursSinceMealFields[preIndex].getText());
                preIndex++;
            }

            // Only save if something is entered
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