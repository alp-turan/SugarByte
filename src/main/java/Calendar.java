import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

public class Calendar extends JFrame {

    private YearMonth currentYearMonth;  // Tracks the displayed month/year

    public Calendar() {
        // Frame setup
        setTitle("Calendar");
        setSize(400, 800); // Adjust to mimic mobile screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main gradient background panel
        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());

        // Initialize with the current month
        currentYearMonth = YearMonth.now();

        // Top panel (Title + Month-Year + Arrows)
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center panel (Days)
        JPanel daysPanel = createDaysPanel();
        mainPanel.add(daysPanel, BorderLayout.CENTER);

        // Add the main panel
        add(mainPanel);
        setVisible(true);
    }

    /**
     * Creates the top panel which has:
     * - "SugarByte" title
     * - Month-Year label
     * - Left/Right arrow buttons for navigation
     */
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        // Load Lobster font (adjust path as needed)
        Font lobsterFont = loadCustomFont("src/fonts/Lobster.ttf", 32f);

        // "SugarByte" title
        JLabel titleLabel = new JLabel("SugarByte");
        titleLabel.setFont((lobsterFont != null)
                ? lobsterFont
                : new Font("Serif", Font.BOLD, 32));
        titleLabel.setForeground(Color.BLACK);

        // Month-Year label
        JLabel monthYearLabel = new JLabel(getMonthYearString(currentYearMonth));
        monthYearLabel.setFont(new Font("Poppins", Font.BOLD, 18));
        monthYearLabel.setForeground(Color.BLACK);

        // Left arrow
        JButton leftArrow = new JButton("\u25C0"); // Unicode for a left triangle
        leftArrow.setFont(new Font("SansSerif", Font.BOLD, 18));
        leftArrow.setFocusPainted(false);
        leftArrow.setBorderPainted(false);
        leftArrow.setContentAreaFilled(false);
        leftArrow.setForeground(Color.BLACK);

        // Right arrow
        JButton rightArrow = new JButton("\u25B6"); // Unicode for a right triangle
        rightArrow.setFont(new Font("SansSerif", Font.BOLD, 18));
        rightArrow.setFocusPainted(false);
        rightArrow.setBorderPainted(false);
        rightArrow.setContentAreaFilled(false);
        rightArrow.setForeground(Color.BLACK);

        // Add action listeners for month navigation
        leftArrow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentYearMonth = currentYearMonth.minusMonths(1);
                refreshCalendar();
            }
        });
        rightArrow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentYearMonth = currentYearMonth.plusMonths(1);
                refreshCalendar();
            }
        });

        // Layout the top panel
        gbc.insets = new Insets(5, 5, 5, 5);

        // Title label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        panel.add(titleLabel, gbc);

        // Reset gridwidth for arrows and month-year
        gbc.gridwidth = 1;
        gbc.gridy = 1;

        // Left arrow
        gbc.gridx = 0;
        panel.add(leftArrow, gbc);

        // Month-Year label
        gbc.gridx = 1;
        panel.add(monthYearLabel, gbc);

        // Right arrow
        gbc.gridx = 2;
        panel.add(rightArrow, gbc);

        return panel;
    }

    /**
     * Creates the panel containing the day initials and the grid of days.
     */
    private JPanel createDaysPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setOpaque(false);

        // Day initials row
        JPanel dayInitialsPanel = new JPanel(new GridLayout(1, 7));
        dayInitialsPanel.setOpaque(false);
        String[] dayInitials = {"M", "T", "W", "T", "F", "S", "S"};
        for (String d : dayInitials) {
            JLabel label = new JLabel(d, SwingConstants.CENTER);
            label.setFont(new Font("SansSerif", Font.BOLD, 14));
            label.setForeground(new Color(0xD8, 0x38, 0x42)); // #D83842
            dayInitialsPanel.add(label);
        }

        // Days grid
        JPanel daysGrid = new JPanel(new GridLayout(6, 7, 10, 10));
        daysGrid.setOpaque(false);

        // We’ll build out the days for the current month
        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int dayOfWeekValue = firstOfMonth.getDayOfWeek().getValue();
        // In ISO, Monday = 1, Sunday = 7

        // We need to figure out how many days are in this month
        int lengthOfMonth = currentYearMonth.lengthOfMonth();

        // We’ll pad blank days before the 1st if needed
        // (ISO starts Monday=1, so if the first day is Monday, we skip 0)
        int blankDaysBefore = (dayOfWeekValue == 7) ? 6 : dayOfWeekValue - 1;

        // Fill in blank days
        for (int i = 0; i < blankDaysBefore; i++) {
            daysGrid.add(new JLabel(""));
        }

        // Fill in actual days
        LocalDate today = LocalDate.now();
        for (int day = 1; day <= lengthOfMonth; day++) {
            LocalDate date = currentYearMonth.atDay(day);

            // Custom label or panel for each day
            JPanel dayPanel = new JPanel();
            dayPanel.setLayout(new BorderLayout());
            dayPanel.setOpaque(false);

            // Circle label for the day number
            DayCircle dayCircle = new DayCircle(day);
            dayCircle.setHorizontalAlignment(SwingConstants.CENTER);
            dayCircle.setFont(new Font("SansSerif", Font.BOLD, 14));

            // If this day is the current date:
            if (date.equals(today)) {
                // Highlight in #D83842 with white text
                dayCircle.setCircleColor(new Color(0xD8, 0x38, 0x42));
                dayCircle.setForeground(Color.WHITE);
            } else {
                // Normal day style
                dayCircle.setCircleColor(new Color(0xE1, 0xE1, 0xE1)); // light gray
                dayCircle.setForeground(Color.BLACK);
            }
            dayPanel.add(dayCircle);

            daysGrid.add(dayPanel);
        }

        // Add day initials + days grid to the container
        panel.add(dayInitialsPanel, BorderLayout.NORTH);
        panel.add(daysGrid, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Refreshes the calendar when the month changes (called by arrow listeners).
     * Essentially rebuilds the day panel.
     */
    private void refreshCalendar() {
        // Replace the center panel with a new days panel for the new month
        getContentPane().removeAll();

        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());

        // Recreate the top panel (with updated month-year)
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Recreate the days panel
        JPanel daysPanel = createDaysPanel();
        mainPanel.add(daysPanel, BorderLayout.CENTER);

        add(mainPanel);
        revalidate();
        repaint();
    }

    /**
     * Helper method to format the current YearMonth into e.g. "November 2024"
     */
    private String getMonthYearString(YearMonth yearMonth) {
        String month = yearMonth.getMonth()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        int year = yearMonth.getYear();
        return month + " " + year;
    }

    /**
     * A small custom JLabel that draws a circular background behind the text.
     * This is how we achieve the “circle day” effect.
     */
    class DayCircle extends JLabel {
        private Color circleColor;

        public DayCircle(int dayNumber) {
            super(String.valueOf(dayNumber));
            this.circleColor = Color.LIGHT_GRAY;
            setPreferredSize(new Dimension(40, 40)); // Adjust size as needed
        }

        public void setCircleColor(Color circleColor) {
            this.circleColor = circleColor;
        }

        @Override
        protected void paintComponent(Graphics g) {
            // Draw the circle behind the text
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int diameter = Math.min(getWidth(), getHeight());
            int x = (getWidth() - diameter) / 2;
            int y = (getHeight() - diameter) / 2;

            g2d.setColor(circleColor);
            g2d.fillOval(x, y, diameter, diameter);

            g2d.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * The same gradient panel approach used previously.
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
     * Loads the custom Lobster font from file, similarly to your other pages.
     */
    private Font loadCustomFont(String path, float size) {
        try {
            return Font.createFont(Font.TRUETYPE_FONT, new File(path)).deriveFont(size);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Test the Calendar page
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Calendar());
    }
}
