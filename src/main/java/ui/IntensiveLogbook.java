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
        topPanel.setBorder(BorderFactory.createEmptyBorder(65, 0, 20, 0));

        JLabel titleLabel = createTitleLabel("SugarByte", lobsterFont, Color.BLACK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(titleLabel);

        String logbookType = getLogbookType(currentUser);
        String formattedDate = formatDate(targetDate);

        JLabel dateLabel = new JLabel(logbookType + " logbook for " + formattedDate, SwingConstants.CENTER);        dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        dateLabel.setForeground(Color.BLACK);
        dateLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(dateLabel);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // ==== CENTER (GridBag) ====
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 0, 5);
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
        JLabel hoursHeader1 = new JLabel("Hours since");
        hoursHeader1.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(hoursHeader1, gbc);

        // Second line of headers
        gbc.gridy = 1;
        gbc.gridx = 0;
        JLabel timeHeader2 = new JLabel("of day");
        timeHeader2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(timeHeader2, gbc);

        gbc.gridx = 1;
        JLabel bloodHeader2 = new JLabel("glucose (mmol/L)");
        bloodHeader2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(bloodHeader2, gbc);

        gbc.gridx = 2;
        JLabel carbsHeader2 = new JLabel("eaten (g)");
        carbsHeader2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(carbsHeader2, gbc);

        gbc.gridx = 3;
        JLabel exerciseHeader2 = new JLabel("type");
        exerciseHeader2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(exerciseHeader2, gbc);

        gbc.gridx = 4;
        JLabel insulinHeader2 = new JLabel("dose");
        insulinHeader2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(insulinHeader2, gbc);

        gbc.gridx = 5;
        JLabel foodHeader2 = new JLabel("diary");
        foodHeader2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(foodHeader2, gbc);

        gbc.gridx = 6;
        JLabel otherHeader2 = new JLabel("events");
        otherHeader2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(otherHeader2, gbc);

        gbc.gridx = 7;
        JLabel hoursHeader2 = new JLabel("last meal");
        hoursHeader2.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(hoursHeader2, gbc);

        gbc.gridy = 2;


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
            addNumericInputRestriction(bloodSugarFields[i]);
            centerPanel.add(bloodSugarFields[i], gbc);

            gbc.gridx = 2;
            carbsFields[i] = new JTextField(5);
            addNumericInputRestriction(carbsFields[i]);
            centerPanel.add(carbsFields[i], gbc);

            // Exercise Type (Multiline JTextArea)
            gbc.gridx = 3;
            exerciseFields[i] = new JTextArea(2, 10); // 2 rows, 10 columns
            exerciseFields[i].setLineWrap(true);     // Enable line wrapping
            exerciseFields[i].setWrapStyleWord(true);// Wrap at word boundaries
            JScrollPane exerciseScrollPane = new JScrollPane(exerciseFields[i]);
            exerciseScrollPane.setPreferredSize(new Dimension(100, 40));
            centerPanel.add(exerciseScrollPane, gbc);

            // Insulin Dose
            gbc.gridx = 4;
            insulinDoseFields[i] = new JTextField(5);
            addNumericInputRestriction(insulinDoseFields[i]);
            centerPanel.add(insulinDoseFields[i], gbc);

            // Food Diary (Multiline JTextArea)
            gbc.gridx = 5;
            foodDiaryFields[i] = new JTextArea(2, 10); // 2 rows, 10 columns
            foodDiaryFields[i].setLineWrap(true);
            foodDiaryFields[i].setWrapStyleWord(true);
            JScrollPane foodScrollPane = new JScrollPane(foodDiaryFields[i]);
            foodScrollPane.setPreferredSize(new Dimension(100, 40));
            centerPanel.add(foodScrollPane, gbc);

            // Other Events (Multiline JTextArea)
            gbc.gridx = 6;
            otherEventsFields[i] = new JTextArea(2, 10); // 2 rows, 10 columns
            otherEventsFields[i].setLineWrap(true);
            otherEventsFields[i].setWrapStyleWord(true);
            JScrollPane otherScrollPane = new JScrollPane(otherEventsFields[i]);
            otherScrollPane.setPreferredSize(new Dimension(100, 40));
            centerPanel.add(otherScrollPane, gbc);

            gbc.gridx = 7;
            if (ROW_LABELS[i].endsWith("Pre")) {
                hoursSinceMealFields[preIndex] = new JTextField(5);
                addNumericInputRestriction(hoursSinceMealFields[preIndex]);
                centerPanel.add(hoursSinceMealFields[preIndex], gbc);
                preIndex++;
            }
        }


        // Wrap centerPanel in a JScrollPane
        JScrollPane scrollPane = new JScrollPane(
                centerPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
// Create the scroll pane with the centerPanel

// Make the scroll pane and its viewport transparent
        scrollPane.setOpaque(false);  // Make the JScrollPane transparent
        scrollPane.getViewport().setOpaque(false);  // Make the viewport transparent
        scrollPane.setBackground(new Color(0, 0, 0, 0));  // Transparent background for the scroll pane
        scrollPane.getViewport().setBackground(new Color(0, 0, 0, 0));  // Transparent background for the viewport

// Remove borders from the scroll pane
        scrollPane.setBorder(null);  // Remove the border around the scroll pane

// Add the scroll pane to the main panel
        mainPanel.add(scrollPane, BorderLayout.CENTER);

// Set a custom layout for the JScrollPane
        scrollPane.setLayout(new ScrollPaneLayout() {
            @Override
            public void layoutContainer(Container parent) {
                super.layoutContainer(parent);

                // Move the horizontal scrollbar to the top
                JScrollPane pane = (JScrollPane) parent;
                if (pane.getHorizontalScrollBar() != null) {
                    Rectangle bounds = pane.getHorizontalScrollBar().getBounds();
                    bounds.y = 420; // Place at the top
                    pane.getHorizontalScrollBar().setBounds(bounds);

                    // Adjust viewport bounds to avoid overlap
                    JViewport viewport = pane.getViewport();
                    Rectangle viewportBounds = viewport.getBounds();
                    viewportBounds.y = bounds.height; // Shift the viewport down
                    viewportBounds.height -= bounds.height; // Reduce height
                    viewport.setBounds(viewportBounds);
                }
            }
        });
        scrollPane.setOpaque(false);
        scrollPane.setBackground(Color.WHITE); // Set the background color of the JScrollPane
        scrollPane.getViewport().setBackground(Color.WHITE); // Set the background color of the viewport
        scrollPane.setBorder(null);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20)); // Top, Left, Bottom, Right
        scrollPane.setViewportBorder(null); // Top, Left, Bottom, Right

        //centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3)); // Top, Left, Bottom, Right


        mainPanel.add(scrollPane, BorderLayout.CENTER);


        // ==== BOTTOM (Save + Nav) ====
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        RoundedButtonLogin saveAllBtn = new RoundedButtonLogin("Save all", new Color(237, 165, 170));
        saveAllBtn.setPreferredSize(new Dimension(96, 40));
        saveAllBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        saveAllBtn.setForeground(Color.BLACK);
        saveAllBtn.addActionListener(e -> handleSaveAllIntensive());
        bottomPanel.add(saveAllBtn);

        JPanel navBar = createBottomNavBar("Logbook", currentUser,
                "/Icons/home.png", "/Icons/logbookfull.png", "/Icons/graph.png", "/Icons/profile.png");

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(bottomPanel, BorderLayout.NORTH);
        wrapperPanel.add(navBar, BorderLayout.SOUTH);

        mainPanel.add(wrapperPanel, BorderLayout.SOUTH);
    }

    /**
     * Add numeric input restriction to a field.
     */
    private void addNumericInputRestriction(JTextField textField) {
        textField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '.' && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
            }
        });
    }

    /**
     * Add alphabetic input restriction to a field.
     */
    private void addAlphabeticInputRestriction(JTextField textField) {
        textField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isLetter(c) && c != ' ' && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
            }
        });
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

    private String getLogbookType(User user) {
        String logbookType = user.getLogbookType(); // Assuming `User` has a `getLogbookType()` method
        return logbookType != null ? logbookType : "Simple";
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
