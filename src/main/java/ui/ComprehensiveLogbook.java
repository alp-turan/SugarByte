package ui;

import model.LogEntry;
import model.User;
import service.LogService;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComprehensiveLogbook extends BaseUI {
    private final User currentUser;
    private final String targetDate;

    // Fields for blood sugar, carbs, and exercise details
    private JTextField[] bloodSugarFields = new JTextField[7];
    private JTextField[] carbsFields = new JTextField[7];
    private JTextField[] exerciseTypeFields = new JTextField[7];
    private JTextField[] exerciseDurationFields = new JTextField[7];

    private static final String[] ROW_LABELS = {
            "Breakfast Pre",
            "Breakfast Post",
            "Lunch Pre",
            "Lunch Post",
            "Dinner Pre",
            "Dinner Post",
            "Bedtime"
    };

    public ComprehensiveLogbook(User user, String date) {
        super("Comprehensive Logbook for " + date);
        this.currentUser = user;
        this.targetDate = date;

        buildUI();
        loadLogEntries();
        setVisible(true);
    }

    private void buildUI() {
        JPanel mainPanel = createGradientPanel(Color.WHITE, Color.WHITE);
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);

        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel titleLabel = createTitleLabel("Comprehensive Logbook", lobsterFont, Color.BLACK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(titleLabel);

        JLabel dateLabel = new JLabel("Logbook for " + targetDate, SwingConstants.CENTER);
        dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        dateLabel.setForeground(Color.BLACK);
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(dateLabel);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        for (int i = 0; i < ROW_LABELS.length; i++) {
            JLabel rowLabel = new JLabel(ROW_LABELS[i] + ":");
            rowLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            rowLabel.setForeground(Color.BLACK);

            bloodSugarFields[i] = new JTextField(5);
            carbsFields[i] = new JTextField(5);
            exerciseTypeFields[i] = new JTextField(10);
            exerciseDurationFields[i] = new JTextField(5);

            gbc.gridx = 0;
            gbc.gridy = i;
            centerPanel.add(rowLabel, gbc);

            gbc.gridx = 1;
            centerPanel.add(bloodSugarFields[i], gbc);

            gbc.gridx = 2;
            centerPanel.add(carbsFields[i], gbc);

            gbc.gridx = 3;
            centerPanel.add(new JLabel("Exercise Type:"), gbc);
            gbc.gridx = 4;
            centerPanel.add(exerciseTypeFields[i], gbc);

            gbc.gridx = 5;
            centerPanel.add(new JLabel("Duration (min):"), gbc);
            gbc.gridx = 6;
            centerPanel.add(exerciseDurationFields[i], gbc);
        }

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        JButton saveAllBtn = new JButton("Save All");
        saveAllBtn.setBackground(new Color(237, 165, 170));
        saveAllBtn.setForeground(Color.BLACK);
        saveAllBtn.addActionListener(e -> handleSaveAll());
        bottomPanel.add(saveAllBtn);

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
        System.out.println("Loading entries for user " + currentUser.getId() + " on date " + targetDate + ": " + entries.size());

        Map<String, LogEntry> entryMap = new HashMap<>();

        for (LogEntry entry : entries) {
            entryMap.put(entry.getTimeOfDay(), entry);
        }

        for (int i = 0; i < ROW_LABELS.length; i++) {
            LogEntry entry = entryMap.get(ROW_LABELS[i]);
            if (entry != null) {
                bloodSugarFields[i].setText(String.valueOf(entry.getBloodSugar()));
                carbsFields[i].setText(String.valueOf(entry.getCarbsEaten()));
                exerciseTypeFields[i].setText(entry.getExerciseType());
                exerciseDurationFields[i].setText(String.valueOf(entry.getExerciseDuration()));
            }
        }
    }

    private void handleSaveAll() {
        for (int i = 0; i < ROW_LABELS.length; i++) {
            double bg = parseDoubleSafe(bloodSugarFields[i].getText());
            double carbs = parseDoubleSafe(carbsFields[i].getText());
            String exerciseType = exerciseTypeFields[i].getText();
            int exerciseDuration = parseIntSafe(exerciseDurationFields[i].getText());

            if (bg > 0 || carbs > 0 || !exerciseType.isEmpty() || exerciseDuration > 0) {
                LogEntry entry = new LogEntry();
                entry.setUserId(currentUser.getId());
                entry.setDate(targetDate);
                entry.setTimeOfDay(ROW_LABELS[i]);
                entry.setBloodSugar(bg);
                entry.setCarbsEaten(carbs);
                entry.setExerciseType(exerciseType);
                entry.setExerciseDuration(exerciseDuration);
                entry.setFoodDetails("Comprehensive Logbook - " + ROW_LABELS[i]);

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
