package ui;

import model.LogEntry;
import model.User;
import service.LogService;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntensiveLogbook extends BaseUI {

    protected User currentUser;
    protected String targetDate;

    // Example columns: BG, Carbs, Exercise, Insulin, FoodDiary, OtherEvents
    protected JTextField[] bloodSugarFields   = new JTextField[7];
    protected JTextField[] carbsFields        = new JTextField[7];
    protected JTextField[] exerciseFields     = new JTextField[7];
    protected JTextField[] insulinDoseFields  = new JTextField[7];
    protected JTextField[] foodDiaryFields    = new JTextField[7];
    protected JTextField[] otherEventsFields  = new JTextField[7];

    private static final String[] ROW_LABELS = {
            "Breakfast Pre",
            "Breakfast Post",
            "Lunch Pre",
            "Lunch Post",
            "Dinner Pre",
            "Dinner Post",
            "Bedtime"
    };

    public IntensiveLogbook(User user, String date) {
        super("Intensive Logbook for " + date);
        this.currentUser = user;
        this.targetDate  = date;

        buildUIIntensive();
        loadLogEntriesIntensive();
        setVisible(true);
    }

    /**
     * Build the "Intensive" UI
     */
    protected void buildUIIntensive() {
        JPanel mainPanel = createGradientPanel(Color.WHITE, Color.WHITE);
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);

        // Top
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel titleLabel = createTitleLabel("Intensive Logbook", lobsterFont, Color.BLACK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(titleLabel);

        JLabel dateLabel = new JLabel("Logbook for " + targetDate, SwingConstants.CENTER);
        dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        dateLabel.setForeground(Color.BLACK);
        dateLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(dateLabel);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // First line of headers
        gbc.gridx = 0;
        gbc.gridy = 0;
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
        JLabel foodHeaderLine1 = new JLabel("Food");
        foodHeaderLine1.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(foodHeaderLine1, gbc);

        gbc.gridx = 6;
        JLabel otherHeaderLine1 = new JLabel("Other");
        otherHeaderLine1.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(otherHeaderLine1, gbc);

        // Second line of headers
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
        JLabel foodHeaderLine2 = new JLabel("Diary");
        foodHeaderLine2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(foodHeaderLine2, gbc);

        gbc.gridx = 6;
        JLabel otherHeaderLine2 = new JLabel("Events");
        otherHeaderLine2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(otherHeaderLine2, gbc);

        // Data rows
        for (int i = 0; i < ROW_LABELS.length; i++) {
            gbc.gridy = i + 2;

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
        }

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom: Save All
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
     * Load data for "Intensive" logbook
     */
    protected void loadLogEntriesIntensive() {
        List<LogEntry> entries = LogService.getEntriesForDate(currentUser.getId(), targetDate);
        Map<String, LogEntry> entryMap = new HashMap<>();
        for (LogEntry entry : entries) {
            entryMap.put(entry.getTimeOfDay(), entry);
        }

        for (int i = 0; i < ROW_LABELS.length; i++) {
            LogEntry e = entryMap.get(ROW_LABELS[i]);
            if (e != null) {
                bloodSugarFields[i].setText(String.valueOf(e.getBloodSugar()));
                carbsFields[i].setText(String.valueOf(e.getCarbsEaten()));
                exerciseFields[i].setText(e.getExerciseType() == null ? "" : e.getExerciseType());
                insulinDoseFields[i].setText(String.valueOf(e.getInsulinDose()));
                foodDiaryFields[i].setText(e.getFoodDetails() == null ? "" : e.getFoodDetails());
                otherEventsFields[i].setText(e.getOtherMedications() == null ? "" : e.getOtherMedications());
            }
        }
    }

    /**
     * Save data from "Intensive" logbook
     */
    protected void handleSaveAllIntensive() {
        for (int i = 0; i < ROW_LABELS.length; i++) {
            double bg       = parseDoubleSafe(bloodSugarFields[i].getText());
            double carbs    = parseDoubleSafe(carbsFields[i].getText());
            String exercise = exerciseFields[i].getText();
            double insulin  = parseDoubleSafe(insulinDoseFields[i].getText());
            String food     = foodDiaryFields[i].getText();
            String other    = otherEventsFields[i].getText();

            // Only save if something is entered
            if (bg > 0 || carbs > 0 || !exercise.isEmpty() || insulin > 0
                    || !food.isEmpty() || !other.isEmpty()) {
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
}