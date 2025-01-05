package ui;

import model.LogEntry;
import model.User;
import service.LogService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComprehensiveLogbook extends BaseUI {
    private final User currentUser;
    private final String targetDate;

    // Arrays to hold our input fields
    private final JTextField[] bloodSugarFields = new JTextField[7];
    private final JTextField[] carbsFields = new JTextField[7];
    private final JTextField[] exerciseTypeFields = new JTextField[7];
    private final JTextField[] exerciseDurationFields = new JTextField[7];

    // Define our time periods for readings
    private static final String[] ROW_LABELS = {
            "Breakfast Pre",
            "Breakfast Post",
            "Lunch Pre",
            "Lunch Post",
            "Dinner Pre",
            "Dinner Post",
            "Bedtime"
    };

    // Colors for better visual organization
    private static final Color SECTION_HEADER_COLOR = new Color(70, 70, 70);
    private static final Color LABEL_COLOR = new Color(100, 100, 100);
    private static final int STANDARD_PADDING = 15;

    public ComprehensiveLogbook(User user, String date) {
        super("Comprehensive Logbook for " + date);
        this.currentUser = user;
        this.targetDate = date;
        buildUI();
        loadLogEntries();
        setVisible(true);
    }

    private void buildUI() {
        // Create main panel with gradient background
        JPanel mainPanel = createGradientPanel(Color.WHITE, Color.WHITE);
        mainPanel.setLayout(new BorderLayout(STANDARD_PADDING, STANDARD_PADDING));
        mainPanel.setBorder(new EmptyBorder(STANDARD_PADDING, STANDARD_PADDING,
                STANDARD_PADDING, STANDARD_PADDING));
        setContentPane(mainPanel);

        // Create and add the header section
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Create and add the main content section
        JScrollPane scrollPane = new JScrollPane(createLogEntryPanel());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Create and add the bottom section with save button and navigation
        mainPanel.add(createBottomPanel(), BorderLayout.SOUTH);
    }
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        // Create panel for the save button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);

        // Create and style the save button
        JButton saveAllBtn = new JButton("Save All");
        saveAllBtn.setBackground(new Color(237, 165, 170));
        saveAllBtn.setForeground(Color.BLACK);
        saveAllBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        saveAllBtn.setPreferredSize(new Dimension(120, 40));
        saveAllBtn.addActionListener(e -> handleSaveAll());
        buttonPanel.add(saveAllBtn);

        // Create the navigation bar
        JPanel navBar = createBottomNavBar("Logbook", currentUser,
                "/Icons/home.png", "/Icons/logbookfull.png", "/Icons/profile.png");

        // Add both panels to the bottom panel
        bottomPanel.add(buttonPanel, BorderLayout.NORTH);
        bottomPanel.add(navBar, BorderLayout.SOUTH);

        return bottomPanel;
    }
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(new EmptyBorder(0, 0, STANDARD_PADDING, 0));

        // Add title
        JLabel titleLabel = createTitleLabel("Comprehensive Logbook", lobsterFont, Color.BLACK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add date subtitle
        JLabel dateLabel = new JLabel("Logbook for " + targetDate);
        dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        dateLabel.setForeground(LABEL_COLOR);
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(dateLabel);

        return headerPanel;
    }

    private JPanel createLogEntryPanel() {
        JPanel entryPanel = new JPanel();
        entryPanel.setOpaque(false);
        entryPanel.setLayout(new GridBagLayout());

        // Create constraints for our grid
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);  // Add more padding between elements
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Add column headers
        addColumnHeaders(entryPanel, gbc);

        // Add entry rows
        for (int i = 0; i < ROW_LABELS.length; i++) {
            addLogEntryRow(entryPanel, gbc, i);
        }

        // Wrap the panel to allow for proper scrolling
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(entryPanel, BorderLayout.NORTH);
        wrapperPanel.add(Box.createVerticalGlue(), BorderLayout.CENTER);

        return wrapperPanel;
    }

    private void addColumnHeaders(JPanel panel, GridBagConstraints gbc) {
        String[] headers = {"Time", "Blood Sugar", "Carbs", "Exercise Type", "Duration"};
        gbc.gridy = 0;
        gbc.gridx = 0;

        for (String header : headers) {
            JLabel headerLabel = new JLabel(header);
            headerLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            headerLabel.setForeground(SECTION_HEADER_COLOR);
            panel.add(headerLabel, gbc);
            gbc.gridx++;
        }
    }

    private void addLogEntryRow(JPanel panel, GridBagConstraints gbc, int rowIndex) {
        gbc.gridy = rowIndex + 1;  // +1 because row 0 is headers
        gbc.gridx = 0;

        // Time label
        JLabel timeLabel = new JLabel(ROW_LABELS[rowIndex]);
        timeLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        timeLabel.setForeground(LABEL_COLOR);
        panel.add(timeLabel, gbc);

        // Blood sugar field
        gbc.gridx++;
        bloodSugarFields[rowIndex] = createStyledTextField(5);
        panel.add(bloodSugarFields[rowIndex], gbc);

        // Carbs field
        gbc.gridx++;
        carbsFields[rowIndex] = createStyledTextField(5);
        panel.add(carbsFields[rowIndex], gbc);

        // Exercise type field
        gbc.gridx++;
        exerciseTypeFields[rowIndex] = createStyledTextField(15);
        panel.add(exerciseTypeFields[rowIndex], gbc);

        // Exercise duration field
        gbc.gridx++;
        exerciseDurationFields[rowIndex] = createStyledTextField(5);
        panel.add(exerciseDurationFields[rowIndex], gbc);
    }

    private JTextField createStyledTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return field;
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
