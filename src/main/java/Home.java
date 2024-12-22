import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Home extends JFrame {

    private JLabel mealLabel;
    private JPanel quickLogPanel;

    public Home() {
        // Basic JFrame setup
        setTitle("Home");
        setSize(400, 800); // Mimic a mobile screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main gradient background panel
        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        // Load Lobster font (adjust path if needed)
        Font lobsterFont = loadCustomFont("src/fonts/Lobster.ttf", 30f);

        // "SugarByte" title (centered)
        JLabel titleLabel = new JLabel("SugarByte", SwingConstants.CENTER);
        titleLabel.setFont((lobsterFont != null)
                ? lobsterFont
                : new Font("Serif", Font.BOLD, 30));
        titleLabel.setForeground(Color.BLACK);

        // Place title at the top with some spacing
        gbc.gridy = 0;
        gbc.insets = new Insets(15, 0, 10, 0);
        mainPanel.add(titleLabel, gbc);

        // Date label (e.g. "Thursday, 28 Nov")
        LocalDate today = LocalDate.now();
        String formattedDate = today.format(DateTimeFormatter.ofPattern("EEEE, d MMM"));
        JLabel dateLabel = new JLabel(formattedDate, SwingConstants.CENTER);
        dateLabel.setFont(new Font("Poppins", Font.BOLD, 16));
        dateLabel.setForeground(new Color(200, 40, 40)); // or any accent color
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(dateLabel, gbc);

        // Greeting label: "Hi, Name"
        JLabel greetingLabel = new JLabel("Hi, Name", SwingConstants.LEFT);
        greetingLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        greetingLabel.setForeground(Color.BLACK);

        // Reminder message in #888888
        JLabel reminderLabel = new JLabel("It’s meal time soon, don’t forget to log your values", SwingConstants.LEFT);
        reminderLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        reminderLabel.setForeground(new Color(0x88, 0x88, 0x88));

        // Put greeting & reminder in a small panel so they can stack nicely
        JPanel greetingPanel = new JPanel();
        greetingPanel.setLayout(new BoxLayout(greetingPanel, BoxLayout.Y_AXIS));
        greetingPanel.setOpaque(false);
        greetingPanel.add(greetingLabel);
        greetingPanel.add(reminderLabel);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 20, 20, 0);
        mainPanel.add(greetingPanel, gbc);

        // Quick Log section
        // We'll create a "Quick Log" panel that shows one meal based on current time
        quickLogPanel = createQuickLogPanel();
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(quickLogPanel, gbc);

        // "Access Your Logbook" button: "Logbook for (date)"
        String logbookLabel = "Logbook for " + today.format(DateTimeFormatter.ofPattern("d MMM"));
        RoundedButton logbookButton = new RoundedButton(logbookLabel, new Color(240, 240, 240));
        logbookButton.setForeground(Color.BLACK);
        logbookButton.setFont(new Font("Poppins", Font.BOLD, 14));
        logbookButton.setPreferredSize(new Dimension(220, 40));

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 30, 0);
        mainPanel.add(logbookButton, gbc);

        // Add the main panel to the frame
        add(mainPanel);
        setVisible(true);
    }

    /**
     * Creates the Quick Log panel. Determines which meal to display
     * based on current time, then shows a small table for "Pre" and
     * "Post" (Blood Glucose, Carbs Eaten).
     */
    private JPanel createQuickLogPanel() {
        // Which meal are we in currently?
        String currentMeal = getCurrentMeal();

        // Outer panel
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        // "Quick Log" + meal label
        JLabel quickLogTitle = new JLabel("Quick Log", SwingConstants.LEFT);
        quickLogTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        quickLogTitle.setForeground(new Color(200, 40, 40)); // accent color

        mealLabel = new JLabel(currentMeal, SwingConstants.LEFT);
        mealLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        mealLabel.setForeground(Color.BLACK);

        // Panel for the "Quick Log" title and meal name side by side
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        titleRow.setOpaque(false);
        titleRow.add(quickLogTitle);
        titleRow.add(mealLabel);

        panel.add(titleRow);

        // Rounded "table" for the quick log entries
        RoundedPanel logTablePanel = new RoundedPanel(30, 10);
        logTablePanel.setBackground(new Color(240, 240, 240));
        logTablePanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Headers
        JLabel bgHeader = new JLabel("Blood Glucose (mg/dL)");
        bgHeader.setFont(new Font("SansSerif", Font.BOLD, 14));
        JLabel carbsHeader = new JLabel("Carbs Eaten (g)");
        carbsHeader.setFont(new Font("SansSerif", Font.BOLD, 14));

        // Row 0: blank cell, BG header, Carbs header
        gbc.gridx = 0;
        gbc.gridy = 0;
        logTablePanel.add(new JLabel(""), gbc);

        gbc.gridx = 1;
        logTablePanel.add(bgHeader, gbc);

        gbc.gridx = 2;
        logTablePanel.add(carbsHeader, gbc);

        // Row 1: "Pre", text fields
        gbc.gridy = 1;
        gbc.gridx = 0;
        logTablePanel.add(new JLabel("Pre"), gbc);

        gbc.gridx = 1;
        JTextField bgPre = createCellField();
        logTablePanel.add(bgPre, gbc);

        gbc.gridx = 2;
        JTextField carbsPre = createCellField();
        logTablePanel.add(carbsPre, gbc);

        // Row 2: "Post", text fields
        gbc.gridy = 2;
        gbc.gridx = 0;
        logTablePanel.add(new JLabel("Post"), gbc);

        gbc.gridx = 1;
        JTextField bgPost = createCellField();
        logTablePanel.add(bgPost, gbc);

        gbc.gridx = 2;
        JTextField carbsPost = createCellField();
        logTablePanel.add(carbsPost, gbc);

        panel.add(logTablePanel);

        return panel;
    }

    /**
     * Returns "Breakfast", "Lunch", "Dinner", or "Bedtime"
     * depending on the current local time.
     *
     *  Breakfast: 06:00–11:59
     *  Lunch:     12:00–16:59
     *  Dinner:    17:00–20:59
     *  Bedtime:   21:00–05:59
     */
    private String getCurrentMeal() {
        LocalTime now = LocalTime.now();
        int hour = now.getHour();

        // Check the hour range
        // 06 - 11 => Breakfast
        // 12 - 16 => Lunch
        // 17 - 20 => Dinner
        // 21 - 23 or 00 - 05 => Bedtime
        if (hour >= 6 && hour < 12) {
            return "Breakfast";
        } else if (hour >= 12 && hour < 17) {
            return "Lunch";
        } else if (hour >= 17 && hour < 21) {
            return "Dinner";
        } else {
            return "Bedtime";
        }
    }

    /**
     * Creates a text field (cell) for Blood Glucose or Carbs input.
     */
    private JTextField createCellField() {
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(100, 30));
        textField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        textField.setHorizontalAlignment(JTextField.CENTER);
        return textField;
    }

    /**
     * A panel that draws a rounded rectangle with a slight shadow.
     */
    class RoundedPanel extends JPanel {
        private int arc;
        private int shadowSize;
        private Color shadowColor;

        public RoundedPanel(int arc, int shadowSize) {
            this.arc = arc;
            this.shadowSize = shadowSize;
            this.shadowColor = new Color(2, 2, 2, 3);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            // Draw the shadow
            g2.setColor(shadowColor);
            for (int i = 0; i < shadowSize; i++) {
                g2.fillRoundRect(
                        i, i + shadowSize / 2,
                        getWidth() - i * 2,
                        getHeight() - i * 2 - shadowSize / 2,
                        arc, arc
                );
            }

            // Draw the panel background
            g2.setColor(getBackground());
            g2.fillRoundRect(
                    shadowSize / 2,
                    shadowSize / 2,
                    getWidth() - shadowSize,
                    getHeight() - shadowSize,
                    arc,
                    arc
            );

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * A simple rounded button (similar to your previous code).
     */
    class RoundedButton extends JButton {
        private Color backgroundColor;

        public RoundedButton(String text, Color backgroundColor) {
            super(text);
            this.backgroundColor = backgroundColor;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            super.paintComponent(g);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            // No border
        }
    }

    /**
     * The same gradient panel approach used in your other classes.
     */
    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();

            Color startColor = new Color(255, 255, 255);
            Color endColor = new Color(240, 240, 240);
            GradientPaint gradient = new GradientPaint(
                    0, 0, startColor,
                    0, height, endColor
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, width, height);
        }
    }

    /**
     * Loads the custom Lobster font, similar to your previous examples.
     */
    private Font loadCustomFont(String path, float size) {
        try {
            return Font.createFont(Font.TRUETYPE_FONT, new File(path))
                    .deriveFont(size);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Test the Home page
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Home());
    }
}
