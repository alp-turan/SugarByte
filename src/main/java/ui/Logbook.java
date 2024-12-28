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
 * Lunch Pre/Post, Dinner Pre/Post, Bedtime),
 * each row having 2 fields: Blood Sugar, Carbs Eaten.
 *
 * This page allows users to log data, save, and load existing entries.
 */
public class Logbook extends BaseUI {

    private final User currentUser;
    private final String targetDate;

    // 7 Rows × 2 columns = 14 text fields
    private JTextField[] bloodSugarFields = new JTextField[7];
    private JTextField[] carbsFields = new JTextField[7];

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
        this.targetDate = date;

        buildUI();
        loadLogEntries(); // Load existing entries, if any
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

        // Center panel: table of 7 rows × 2 columns
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Build the 7 rows
        for (int i = 0; i < ROW_LABELS.length; i++) {
            // Label for the row
            JLabel rowLabel = new JLabel(ROW_LABELS[i] + ":");
            rowLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            rowLabel.setForeground(Color.BLACK);

            // Blood Sugar field
            bloodSugarFields[i] = new JTextField(5);

            // Carbs field
            carbsFields[i] = new JTextField(5);

            // Layout: Row label in col 0
            gbc.gridx = 0;
            gbc.gridy = i;
            centerPanel.add(rowLabel, gbc);

            // Blood sugar in col 1
            gbc.gridx = 1;
            centerPanel.add(bloodSugarFields[i], gbc);

            // Carbs in col 2
            gbc.gridx = 2;
            centerPanel.add(carbsFields[i], gbc);
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

        JPanel navBar = createBottomNavBar("Logbook", currentUser,
                "/Icons/home.png", "/Icons/logbookfull.png", "/Icons/profile.png");

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(bottomPanel, BorderLayout.NORTH);
        wrapperPanel.add(navBar, BorderLayout.SOUTH);

        mainPanel.add(wrapperPanel, BorderLayout.SOUTH);
    }

    /**
     * Loads previously saved entries for the specified date
     * and populates the fields.
     */
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
            }
        }
    }

    /**
     * Called when the user clicks "Save All".
     * Creates or updates LogEntry objects for each row.
     */
    private void handleSaveAll() {
        for (int i = 0; i < ROW_LABELS.length; i++) {
            double bg = parseDoubleSafe(bloodSugarFields[i].getText());
            double carbs = parseDoubleSafe(carbsFields[i].getText());

            if (bg > 0 || carbs > 0) {
                LogEntry entry = new LogEntry();
                entry.setUserId(currentUser.getId());
                entry.setDate(targetDate);
                entry.setTimeOfDay(ROW_LABELS[i]);
                entry.setBloodSugar(bg);
                entry.setCarbsEaten(carbs);
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
}