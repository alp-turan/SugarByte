package ui;

import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * The Calendar screen of the SugarByte application.
 * Displays calendar functionalities and includes a bottom navigation bar.
 */
public class Calendar extends BaseUI {

    private YearMonth currentYearMonth;  // Tracks the displayed month/year
    private User currentUser;             // Currently logged-in user

    /**
     * Constructor that accepts a User object.
     *
     * @param user The currently logged-in user.
     */
    public Calendar(User user) {
        super("Calendar"); // Call BaseUI constructor with the title
        this.currentUser = user;
        buildUI();
        setVisible(true);
    }

    /**
     * No-argument constructor.
     * Useful if you need to instantiate Calendar without a User.
     */
    public Calendar() {
        super("Calendar"); // Call BaseUI constructor with the title
        buildUI();
        setVisible(true);
    }

    /**
     * Builds the UI components of the Calendar screen.
     */
    private void buildUI() {
        // Initialize with the current month
        currentYearMonth = YearMonth.now();

        // Main panel with BorderLayout to organize components
        JPanel mainPanel = createGradientPanel(Color.WHITE, new Color(255, 255, 255)); // Using BaseUI's method
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);

        // =======================
        // Top and Center Content
        // =======================

        // Panel to hold top and center content using GridBagLayout
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false); // Transparent to show gradient
        GridBagConstraints gbc = new GridBagConstraints();

        // Title Label: "SugarByte"
        JLabel titleLabel = createTitleLabel("SugarByte", lobsterFont, Color.BLACK); // Using BaseUI's method
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 100, 0); // Top padding
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        contentPanel.add(titleLabel, gbc);

        // Month-Year label
        JLabel monthYearLabel = new JLabel(getMonthYearString(currentYearMonth), SwingConstants.CENTER);
        monthYearLabel.setFont(new Font("Poppins", Font.BOLD, 18));
        monthYearLabel.setForeground(Color.BLACK);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0); // Between title and month-year
        contentPanel.add(monthYearLabel, gbc);

        // Top navigation panel (Left/Right arrows and month-year)
        JPanel topNavPanel = createTopNavPanel(monthYearLabel);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 20, 0); // Between month-year and days
        contentPanel.add(topNavPanel, gbc);

        // Days panel (Day initials + Days grid)
        JPanel daysPanel = createDaysPanel();
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 20, 0); // Padding below days
        contentPanel.add(daysPanel, gbc);

        // Add contentPanel to the center of mainPanel
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // =====================
        // Bottom Navigation Bar
        // =====================

        // Create navigation bar panel using BaseUI's method
        JPanel navBar = createBottomNavBar("Calendar");
        mainPanel.add(navBar, BorderLayout.SOUTH);
    }

    /**
     * Creates the top navigation panel with left/right arrows and month-year label.
     *
     * @param monthYearLabel The JLabel displaying the current month and year.
     * @return A JPanel containing the navigation arrows and month-year label.
     */
    private JPanel createTopNavPanel(JLabel monthYearLabel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // Left arrow
        JButton leftArrow = createArrowButton("\u25C0"); // Unicode for a left triangle
        leftArrow.setFont(new Font("SansSerif", Font.BOLD, 18));
        leftArrow.setForeground(Color.BLACK);
        leftArrow.setToolTipText("Previous Month");

        // Right arrow
        JButton rightArrow = createArrowButton("\u25B6"); // Unicode for a right triangle
        rightArrow.setFont(new Font("SansSerif", Font.BOLD, 18));
        rightArrow.setForeground(Color.BLACK);
        rightArrow.setToolTipText("Next Month");

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

        // Add arrows and month-year label to the panel
        panel.add(leftArrow, BorderLayout.WEST);
        panel.add(monthYearLabel, BorderLayout.CENTER);
        panel.add(rightArrow, BorderLayout.EAST);

        return panel;
    }

    /**
     * Creates an arrow button with the specified symbol.
     *
     * @param symbol The arrow symbol.
     * @return A styled JButton representing an arrow.
     */
    private JButton createArrowButton(String symbol) {
        JButton button = new JButton(symbol);
        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false); // Transparent background
        return button;
    }

    /**
     * Creates the panel containing the day initials and the grid of days.
     */
    private JPanel createDaysPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // Day initials row
        JPanel dayInitialsPanel = new JPanel(new GridLayout(1, 7, 10, 0));
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
        YearMonth yearMonth = currentYearMonth;
        int lengthOfMonth = yearMonth.lengthOfMonth();

        // Get the first day of the month and its day of the week
        java.time.LocalDate firstOfMonth = yearMonth.atDay(1);
        int dayOfWeekValue = firstOfMonth.getDayOfWeek().getValue();
        // In ISO, Monday = 1, Sunday = 7

        // We need to figure out how many blank days to add before the first day
        int blankDaysBefore = (dayOfWeekValue == 7) ? 6 : dayOfWeekValue - 1;

        // Fill in blank days
        for (int i = 0; i < blankDaysBefore; i++) {
            daysGrid.add(new JLabel(""));
        }

        // Fill in actual days
        java.time.LocalDate today = java.time.LocalDate.now();
        for (int day = 1; day <= lengthOfMonth; day++) {
            java.time.LocalDate date = yearMonth.atDay(day);

            // Custom label or panel for each day
            JPanel dayPanel = new JPanel(new BorderLayout());
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
            dayPanel.add(dayCircle, BorderLayout.CENTER);

            daysGrid.add(dayPanel);
        }

        // Fill the remaining cells with empty labels to maintain grid structure
        int totalCells = 6 * 7;
        int filledCells = blankDaysBefore + lengthOfMonth;
        for (int i = filledCells; i < totalCells; i++) {
            daysGrid.add(new JLabel(""));
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
        // Remove existing content
        getContentPane().removeAll();

        // Recreate the main panel with updated month-year
        JPanel mainPanel = createGradientPanel(Color.WHITE, new Color(255, 255, 255)); // Using BaseUI's method
        mainPanel.setLayout(new BorderLayout());

        // Recreate the content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false); // Transparent to show gradient
        GridBagConstraints gbc = new GridBagConstraints();

        // Load custom Lobster font from resources
        Font lobsterFont = loadCustomFont("/Fonts/Lobster.ttf", 32f); // Ensure the path is correct

        // Title Label: "SugarByte"
        JLabel titleLabel = createTitleLabel("SugarByte", lobsterFont, Color.BLACK); // Using BaseUI's method
        gbc.gridy = 0;
        gbc.insets = new Insets(15, 0, 10, 0); // Top padding
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        contentPanel.add(titleLabel, gbc);

        // Month-Year label
        JLabel monthYearLabel = new JLabel(getMonthYearString(currentYearMonth), SwingConstants.CENTER);
        monthYearLabel.setFont(new Font("Poppins", Font.BOLD, 18));
        monthYearLabel.setForeground(Color.BLACK);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0); // Between title and month-year
        contentPanel.add(monthYearLabel, gbc);

        // Top navigation panel (Left/Right arrows and month-year)
        JPanel topNavPanel = createTopNavPanel(monthYearLabel);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 20, 0); // Between month-year and days
        contentPanel.add(topNavPanel, gbc);

        // Days panel (Day initials + Days grid)
        JPanel daysPanel = createDaysPanel();
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 20, 0); // Padding below days
        contentPanel.add(daysPanel, gbc);

        // Add contentPanel to the center of mainPanel
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // =====================
        // Bottom Navigation Bar
        // =====================

        // Create navigation bar panel using BaseUI's method
        JPanel navBar = createBottomNavBar("Calendar");
        mainPanel.add(navBar, BorderLayout.SOUTH);

        // Add the main panel back to the frame
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
     * Creates the bottom navigation bar with Home, Calendar, and Profile buttons.
     *
     * @param activeButton The name of the active button to highlight.
     * @return A JPanel representing the bottom navigation bar.
     */
    private JPanel createBottomNavBar(String activeButton) {
        JPanel navBar = new JPanel();
        navBar.setOpaque(false); // Transparent to show gradient
        navBar.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 10)); // Centered with gaps
        navBar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Padding around nav bar

        // Load icons using BaseUI's method
        ImageIcon homeIcon = loadScaledIcon("/Icons/home.png", 30, 30);
        ImageIcon calendarIcon = loadScaledIcon("/Icons/logbookfull.png", 30, 30);
        ImageIcon profileIcon = loadScaledIcon("/Icons/profile.png", 30, 30);

        // Optionally, highlight the active button by changing its icon or color
        if ("Home".equals(activeButton)) {
            homeIcon = loadScaledIcon("/Icons/home.png", 30, 30); // Ensure this icon exists
        } else if ("Calendar".equals(activeButton)) {
            calendarIcon = loadScaledIcon("/Icons/logbookfull.png", 30, 30); // Ensure this icon exists
        } else if ("Profile".equals(activeButton)) {
            profileIcon = loadScaledIcon("/Icons/profile.png", 30, 30); // Ensure this icon exists
        }

        // Create navigation buttons using BaseUI's method
        JButton homeButton = createIconButton(homeIcon, "Home");
        JButton calendarButton = createIconButton(calendarIcon, "Calendar");
        JButton profileButton = createIconButton(profileIcon, "Profile");

        // Add action listeners to navigation buttons
        homeButton.addActionListener(e -> {
            // Navigate to Home screen
            dispose(); // Close current window
            new Home(currentUser); // Open Home screen with User object
        });

        calendarButton.addActionListener(e -> {
            // Already on Calendar - optional: show a message or refresh
            JOptionPane.showMessageDialog(this, "Already on Calendar.", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        profileButton.addActionListener(e -> {
            // Navigate to Profile screen
            dispose(); // Close current window
            new Profile(currentUser); // Open Profile screen with User object
        });

        // Add buttons to navigation bar
        navBar.add(homeButton);
        navBar.add(calendarButton);
        navBar.add(profileButton);

        return navBar;
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
            setOpaque(false);
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
     * Main method to run the Calendar screen independently.
     * Useful for testing.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // For testing purposes, create a dummy User
            User dummyUser = new User(); // Ensure User has setters
            dummyUser.setName("Mark");
            new Calendar(dummyUser);
        });
    }
}
