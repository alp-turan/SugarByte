package ui;

import model.LogEntry;
import model.User;
import service.LogService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntensiveLogbook extends BaseUI {
    private final User currentUser;
    private final String targetDate;

    // Arrays to hold all input fields
    private final JTextField[] bloodSugarFields = new JTextField[7];
    private final JTextField[] carbsFields = new JTextField[7];
    private final JTextField[] exerciseTypeFields = new JTextField[7];
    private final JTextField[] exerciseDurationFields = new JTextField[7];
    private final JTextField[] insulinDoseFields = new JTextField[7];
    private final JTextField[] otherMedicationsFields = new JTextField[7];

    // Define time periods for readings
    private static final String[] ROW_LABELS = {
            "Breakfast Pre",
            "Breakfast Post",
            "Lunch Pre",
            "Lunch Post",
            "Dinner Pre",
            "Dinner Post",
            "Bedtime"
    };

    // Visual styling constants
    private static final Color SECTION_HEADER_COLOR = new Color(70, 70, 70);
    private static final Color LABEL_COLOR = new Color(100, 100, 100);
    private static final Color GROUP_BACKGROUND = new Color(245, 245, 245);
    private static final int STANDARD_PADDING = 15;
    private static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 14);
    private static final Font FIELD_FONT = new Font("SansSerif", Font.PLAIN, 14);

    public IntensiveLogbook(User user, String date) {
        super("Intensive Logbook for " + date);
        this.currentUser = user;
        this.targetDate = date;
        buildUI();
        loadLogEntries();
        setVisible(true);
    }

    private void buildUI() {
        JPanel mainPanel = createGradientPanel(Color.WHITE, Color.WHITE);
        mainPanel.setLayout(new BorderLayout(STANDARD_PADDING, STANDARD_PADDING));
        mainPanel.setBorder(new EmptyBorder(STANDARD_PADDING, STANDARD_PADDING,
                STANDARD_PADDING, STANDARD_PADDING));
        setContentPane(mainPanel);

        // Add the header section
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Create scrollable content area
        JScrollPane scrollPane = new JScrollPane(createLogEntryPanel());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Add the bottom section with save button and navigation
        mainPanel.add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(new EmptyBorder(0, 0, STANDARD_PADDING, 0));

        JLabel titleLabel = createTitleLabel("Intensive Logbook", lobsterFont, Color.BLACK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel dateLabel = new JLabel("Logbook for " + targetDate);
        dateLabel.setFont(FIELD_FONT);
        dateLabel.setForeground(LABEL_COLOR);
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(dateLabel);

        return headerPanel;
    }

    private JPanel createLogEntryPanel() {
        JPanel mainEntryPanel = new JPanel();
        mainEntryPanel.setLayout(new BoxLayout(mainEntryPanel, BoxLayout.Y_AXIS));
        mainEntryPanel.setOpaque(false);

        // Create and add each section
        mainEntryPanel.add(createGlucoseAndCarbsSection());
        mainEntryPanel.add(Box.createVerticalStrut(STANDARD_PADDING));
        mainEntryPanel.add(createExerciseSection());
        mainEntryPanel.add(Box.createVerticalStrut(STANDARD_PADDING));
        mainEntryPanel.add(createMedicationSection());

        return mainEntryPanel;
    }

    private JPanel createGlucoseAndCarbsSection() {
        JPanel panel = createGroupPanel("Glucose and Carbs");
        GridBagConstraints gbc = createStandardGridBagConstraints();

        // Add column headers
        addSectionHeaders(panel, gbc, new String[]{"Time", "Blood Sugar (mg/dL)", "Carbs (g)"});

        // Add input rows
        for (int i = 0; i < ROW_LABELS.length; i++) {
            gbc.gridy = i + 1;

            // Time label
            gbc.gridx = 0;
            panel.add(createStyledLabel(ROW_LABELS[i]), gbc);

            // Blood sugar field
            gbc.gridx = 1;
            bloodSugarFields[i] = createStyledTextField(5);
            panel.add(bloodSugarFields[i], gbc);

            // Carbs field
            gbc.gridx = 2;
            carbsFields[i] = createStyledTextField(5);
            panel.add(carbsFields[i], gbc);
        }

        return panel;
    }

    private JPanel createExerciseSection() {
        JPanel panel = createGroupPanel("Exercise");
        GridBagConstraints gbc = createStandardGridBagConstraints();

        // Add column headers
        addSectionHeaders(panel, gbc, new String[]{"Time", "Type", "Duration (min)"});

        // Add input rows
        for (int i = 0; i < ROW_LABELS.length; i++) {
            gbc.gridy = i + 1;

            // Time label
            gbc.gridx = 0;
            panel.add(createStyledLabel(ROW_LABELS[i]), gbc);

            // Exercise type field
            gbc.gridx = 1;
            exerciseTypeFields[i] = createStyledTextField(15);
            panel.add(exerciseTypeFields[i], gbc);

            // Duration field
            gbc.gridx = 2;
            exerciseDurationFields[i] = createStyledTextField(5);
            panel.add(exerciseDurationFields[i], gbc);
        }

        return panel;
    }

    private JPanel createMedicationSection() {
        JPanel panel = createGroupPanel("Medications");
        GridBagConstraints gbc = createStandardGridBagConstraints();

        // Add column headers
        addSectionHeaders(panel, gbc, new String[]{"Time", "Insulin Dose (units)", "Other Medications"});

        // Add input rows
        for (int i = 0; i < ROW_LABELS.length; i++) {
            gbc.gridy = i + 1;

            // Time label
            gbc.gridx = 0;
            panel.add(createStyledLabel(ROW_LABELS[i]), gbc);

            // Insulin dose field
            gbc.gridx = 1;
            insulinDoseFields[i] = createStyledTextField(5);
            panel.add(insulinDoseFields[i], gbc);

            // Other medications field
            gbc.gridx = 2;
            otherMedicationsFields[i] = createStyledTextField(20);
            panel.add(otherMedicationsFields[i], gbc);
        }

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        // Create save button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);

        JButton saveAllBtn = new RoundedButton("Save All", new Color(237, 165, 170));
        saveAllBtn.setPreferredSize(new Dimension(120, 40));
        saveAllBtn.setFont(HEADER_FONT);
        saveAllBtn.addActionListener(e -> handleSaveAll());
        buttonPanel.add(saveAllBtn);

        // Create navigation bar
        JPanel navBar = createBottomNavBar("Logbook", currentUser,
                "/Icons/home.png", "/Icons/logbookfull.png", "/Icons/profile.png");

        bottomPanel.add(buttonPanel, BorderLayout.NORTH);
        bottomPanel.add(navBar, BorderLayout.SOUTH);

        return bottomPanel;
    }

    // Helper methods for creating UI components
    private JPanel createGroupPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200)),
                        title,
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        HEADER_FONT
                ),
                new EmptyBorder(STANDARD_PADDING, STANDARD_PADDING,
                        STANDARD_PADDING, STANDARD_PADDING)
        ));
        panel.setBackground(GROUP_BACKGROUND);
        return panel;
    }

    private void addSectionHeaders(JPanel panel, GridBagConstraints gbc, String[] headers) {
        gbc.gridy = 0;
        for (int i = 0; i < headers.length; i++) {
            gbc.gridx = i;
            JLabel headerLabel = new JLabel(headers[i]);
            headerLabel.setFont(HEADER_FONT);
            headerLabel.setForeground(SECTION_HEADER_COLOR);
            panel.add(headerLabel, gbc);
        }
    }

    private JTextField createStyledTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(FIELD_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return field;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FIELD_FONT);
        label.setForeground(LABEL_COLOR);
        return label;
    }

    private GridBagConstraints createStandardGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    // Data handling methods
    private void loadLogEntries() {
        List<LogEntry> entries = LogService.getEntriesForDate(currentUser.getId(), targetDate);

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
                insulinDoseFields[i].setText(String.valueOf(entry.getInsulinDose()));
                otherMedicationsFields[i].setText(entry.getOtherMedications());
            }
        }
    }

    private void handleSaveAll() {
        for (int i = 0; i < ROW_LABELS.length; i++) {
            if (hasDataToSave(i)) {
                LogEntry entry = createLogEntryFromRow(i);
                LogService.createEntry(entry, currentUser);
            }
        }

        JOptionPane.showMessageDialog(this,
                "All entries have been saved successfully.",
                "Save Successful",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean hasDataToSave(int rowIndex) {
        return !bloodSugarFields[rowIndex].getText().trim().isEmpty() ||
                !carbsFields[rowIndex].getText().trim().isEmpty() ||
                !exerciseTypeFields[rowIndex].getText().trim().isEmpty() ||
                !exerciseDurationFields[rowIndex].getText().trim().isEmpty() ||
                !insulinDoseFields[rowIndex].getText().trim().isEmpty() ||
                !otherMedicationsFields[rowIndex].getText().trim().isEmpty();
    }

    private LogEntry createLogEntryFromRow(int rowIndex) {
        LogEntry entry = new LogEntry();
        entry.setUserId(currentUser.getId());
        entry.setDate(targetDate);
        entry.setTimeOfDay(ROW_LABELS[rowIndex]);

        // Set values with validation
        entry.setBloodSugar(parseDoubleSafe(bloodSugarFields[rowIndex].getText()));
        entry.setCarbsEaten(parseDoubleSafe(carbsFields[rowIndex].getText()));
        entry.setExerciseType(exerciseTypeFields[rowIndex].getText().trim());
        entry.setExerciseDuration(parseIntSafe(exerciseDurationFields[rowIndex].getText()));
        entry.setInsulinDose(parseDoubleSafe(insulinDoseFields[rowIndex].getText()));
        entry.setOtherMedications(otherMedicationsFields[rowIndex].getText().trim());

        return entry;
    }

    private double parseDoubleSafe(String text) {
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private int parseIntSafe(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}