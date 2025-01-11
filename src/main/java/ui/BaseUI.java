package ui;

import database.UserDAO;
import model.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Properties;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

public class BaseUI extends JFrame {
    public YearMonth currentYearMonth = YearMonth.now();
    public User currentUser;
    public JTextField usernameField;
    public JPasswordField passwordField;
    private JCheckBox rememberMeCheckBox;
    private String icon1;

    public BaseUI(String title) {
        setTitle(title);
        setSize(400, 800); // Mimic a mobile screen size
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    protected void addBackButton() {
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setForeground(Color.BLUE);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleBack();
            }
        });

        // Use GridBagConstraints to add the Back button
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0; // Adjust this if you want to change the position of the button
        gbc.anchor = GridBagConstraints.WEST;

        // Add the button to the layout
        add(backButton, gbc);
    }

    // Method to handle back action
    private void handleBack() {
        // Close current window and go back to the previous screen
        dispose(); // Close the current screen
        // Example: You can show the previous screen or the main screen
        new Login(); // Or another class like Profile or Calendar based on your flow
    }


    protected void addLogoutButton() {
        // Create a styled button using our RoundedButton class for consistency
        RoundedButton logoutButton = new RoundedButton("Logout", new Color(220, 53, 69)) {
            // Override the preferred size to maintain consistent button dimensions
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(100, 35);
            }
        };

        // Style the button
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("SansSerif", Font.BOLD, 14));

        // Add hover effect
        logoutButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logoutButton.setBackground(new Color(200, 35, 51)); // Darker red on hover
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                logoutButton.setBackground(new Color(220, 53, 69)); // Return to original color
            }
        });

        // Create a container panel for proper positioning
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        // Create right-aligned panel for logout button with proper padding
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setOpaque(false);
        logoutPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 15)); // Add padding
        logoutPanel.add(logoutButton);

        // Add logout panel to the header
        headerPanel.add(logoutPanel, BorderLayout.EAST);

        // Add action listener for logout functionality
        logoutButton.addActionListener(e -> handleLogout());

        // Add the header panel to the frame
        getContentPane().add(headerPanel, BorderLayout.NORTH);
    }

    private void handleLogout() {
        // Create a custom confirmation dialog with styled buttons
        int result = createCustomConfirmDialog(
                "Logout Confirmation",
                "Are you sure you want to log out?",
                new Color(220, 53, 69)  // Match logout button color
        );

        if (result == JOptionPane.YES_OPTION) {
            dispose();
            new Login();
        }
    }

    // Helper method to create consistently styled confirmation dialogs
    private int createCustomConfirmDialog(String title, String message, Color accentColor) {
        // Create custom buttons with consistent styling
        RoundedButton yesButton = new RoundedButton("Yes", accentColor);
        RoundedButton noButton = new RoundedButton("No", Color.GRAY);
        yesButton.setForeground(Color.WHITE);
        noButton.setForeground(Color.WHITE);

        // Create the dialog with custom options
        Object[] options = {yesButton, noButton};
        return JOptionPane.showOptionDialog(
                this,
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                noButton  // Default to 'No'
        );
    }

    // Method to load custom fonts
    protected Font loadCustomFont(float size) {
        try (InputStream is = getClass().getResourceAsStream("/Fonts/Lobster.ttf")) {
            if (is == null) {
                System.err.println("Font not found: /Fonts/Lobster.ttf");
                return new Font("Serif", Font.PLAIN, (int) size);
            }
            return Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(size);
        } catch (Exception e) {
            e.printStackTrace();
            return new Font("Serif", Font.PLAIN, (int) size);
        }
    }

    Font lobsterFont = loadCustomFont(46);

    protected JLabel createTitleLabel(String text, Font font, Color color) {
        JLabel titleLabel = new JLabel(text);
        titleLabel.setFont(font != null ? font : new Font("Serif", Font.BOLD, 36));
        titleLabel.setForeground(color);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        return titleLabel;
    }

    protected JPanel createGradientPanel(Color startColor, Color endColor) {
        return new GradientPanel(startColor, endColor);
    }

    static class GradientPanel extends JPanel {
        private final Color startColor;
        private final Color endColor;

        public GradientPanel(Color startColor, Color endColor) {
            this.startColor = startColor;
            this.endColor = endColor;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();
            GradientPaint gradient = new GradientPaint(0, 0, startColor, 0, height, endColor);
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, width, height);
        }
    }

    public static class RoundedPanel extends JPanel {
        private int arc;      // Corner radius
        private int shadowSize; // Shadow size
        private Color shadowColor;

        public RoundedPanel(int arc, int shadowSize) {
            this.arc = arc;
            this.shadowSize = shadowSize;
            this.shadowColor = new Color(2, 2, 2, 3); // Semi-transparent shadow
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            // Enable anti-aliasing for smooth edges
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw the shadow
            g2.setColor(shadowColor);
            for (int i = 0; i < shadowSize; i++) {
                g2.fillRoundRect(i, i + shadowSize / 2, getWidth() - i * 2, getHeight() - i * 2 - shadowSize / 2, arc, arc);
            }

            // Draw the panel
            g2.setColor(getBackground());
            g2.fillRoundRect(shadowSize / 2, shadowSize / 2, getWidth() - shadowSize, getHeight() - shadowSize, arc, arc);

            g2.dispose();
        }
    }

    protected ImageIcon loadScaledIcon(String path) {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) {
                System.err.println("Icon not found: " + path);
                return null;
            }
            BufferedImage sourceImage = ImageIO.read(is);

            BufferedImage resized = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = resized.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.drawImage(sourceImage, 0, 0, 30, 30, null);
            g2.dispose();

            return new ImageIcon(resized);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public JButton createIconButton(ImageIcon icon, String altText) {
        JButton button = new JButton(icon);
        button.setToolTipText(altText);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        return button;
    }

    public JPanel createBottomNavBar(String currentScreen, User currentUser, String icon1, String icon2, String icon3, String icon4) {
        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        navBar.setOpaque(false);
        navBar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Load icons
        ImageIcon homeIcon = loadScaledIcon(icon1);
        ImageIcon calendarIcon = loadScaledIcon(icon2);
        ImageIcon graphIcon = loadScaledIcon(icon3);
        ImageIcon profileIcon = loadScaledIcon(icon4);  // Load Graph icon

        // Create buttons
        JButton homeButton = createIconButton(homeIcon, "Home");
        JButton calendarButton = createIconButton(calendarIcon, "Calendar");
        JButton graphButton = createIconButton(graphIcon, "Graph");  // Create Graph button
        JButton profileButton = createIconButton(profileIcon, "Profile");


        // Add ActionListeners for navigation
        homeButton.addActionListener(e -> navigateTo("Home", currentScreen, currentUser));
        calendarButton.addActionListener(e -> navigateTo("Calendar", currentScreen, currentUser));
        graphButton.addActionListener(e -> navigateTo("GlucoseGraph", currentScreen, currentUser));  // Navigate to GlucoseGraph
        profileButton.addActionListener(e -> navigateTo("Profile", currentScreen, currentUser));

        // Add buttons to the navigation bar
        navBar.add(homeButton);
        navBar.add(calendarButton);
        navBar.add(graphButton);  // Add Graph button
        navBar.add(profileButton);


        return navBar;
    }


    private void navigateTo(String targetScreen, String currentScreen, User currentUser) {
        if (!targetScreen.equals(currentScreen)) {
            dispose();
            switch (targetScreen) {
                case "Home":
                    new Home(currentUser);
                    break;
                case "Calendar":
                    new Calendar(currentUser);
                    break;
                case "GlucoseGraph":
                    new GlucoseGraph(currentUser);  // Navigate to GlucoseGraph
                    break;
                case "Profile":
                    new Profile(currentUser);
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Unknown target screen.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Already on the " + targetScreen + " page.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    protected void refreshCalendar() {
        getContentPane().removeAll();

        JPanel mainPanel = createGradientPanel(Color.WHITE, Color.WHITE);
        mainPanel.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel titleLabel = createTitleLabel("SugarByte", lobsterFont, Color.BLACK);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 30, 0); // Bottom padding before nav bar
        contentPanel.add(titleLabel, gbc);

        JLabel monthYearLabel = new JLabel(getMonthYearString(currentYearMonth), SwingConstants.CENTER);
        monthYearLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        gbc.gridy = 1;
        contentPanel.add(monthYearLabel, gbc);

        JPanel topNavPanel = createTopNavPanel(monthYearLabel);
        gbc.gridy = 2;
        contentPanel.add(topNavPanel, gbc);

        JPanel daysPanel = createDaysPanel();
        gbc.gridy = 3;
        contentPanel.add(daysPanel, gbc);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(createBottomNavBar("Calendar", currentUser, "/Icons/home.png", "/Icons/logbookfull.png", "/Icons/graph.png", "/Icons/profile.png"), BorderLayout.SOUTH);

        add(mainPanel);
        revalidate();
        repaint();
    }

    public JPanel createTopNavPanel(JLabel monthYearLabel) {
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
    public JButton createArrowButton(String symbol) {
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
    public JPanel createDaysPanel() {
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

    private void changeMonth(int increment, JLabel monthYearLabel) {
        currentYearMonth = currentYearMonth.plusMonths(increment);
        monthYearLabel.setText(getMonthYearString(currentYearMonth));
        refreshCalendar();
    }

    String getMonthYearString(YearMonth yearMonth) {
        return yearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + yearMonth.getYear();
    }
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

    public JPanel createQuickLogPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton quickLogButton = new RoundedButton("Quick Log", new Color(237, 165, 170));
        quickLogButton.setPreferredSize(new Dimension(150, 40));
        quickLogButton.setFont(new Font("SansSerif", Font.BOLD, 14));

        quickLogButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Quick Log Clicked!", "Info", JOptionPane.INFORMATION_MESSAGE));

        panel.add(quickLogButton);
        return panel;
    }

    public static class RoundedButton extends JButton {
        private final Color bgColor;

        public RoundedButton(String text, Color bgColor) {
            super(text);
            this.bgColor = bgColor;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            super.paintComponent(g);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            // No border
        }
    }

    JPanel createInputPanel(String labelText) {
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
        containerPanel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setForeground(Color.GRAY);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setBorder(BorderFactory.createEmptyBorder(0,5,5,0));

        RoundedPanel roundedPanel = new RoundedPanel(70, 10);
        roundedPanel.setPreferredSize(new Dimension(300, 75));
        roundedPanel.setBackground(new Color(240, 240, 240));
        roundedPanel.setLayout(new BorderLayout());

        if (labelText.equalsIgnoreCase("Username")) {
            usernameField = new JTextField();
            usernameField.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 10));
            usernameField.setBackground(new Color(240, 240, 240));
            usernameField.setFont(new Font("SansSerif", Font.PLAIN, 16));
            usernameField.setOpaque(false);
            roundedPanel.add(usernameField, BorderLayout.CENTER);
        } else if (labelText.equalsIgnoreCase("Password")) {
            passwordField = new JPasswordField();
            passwordField.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 10));
            passwordField.setBackground(new Color(240, 240, 240));
            passwordField.setFont(new Font("SansSerif", Font.PLAIN, 16));
            passwordField.setOpaque(false);
            roundedPanel.add(passwordField, BorderLayout.CENTER);
        }

        containerPanel.add(label);
        containerPanel.add(roundedPanel);

        if (labelText.equalsIgnoreCase("Password")) {
            rememberMeCheckBox = new JCheckBox("Remember Me");
            rememberMeCheckBox.setOpaque(false);
            rememberMeCheckBox.setFont(new Font("SansSerif", Font.PLAIN, 12));
            rememberMeCheckBox.setForeground(Color.GRAY);
            containerPanel.add(rememberMeCheckBox);
            loadCredentials(); // Load saved credentials if available
        }

        return containerPanel;
    }

    protected void handleSignIn() {
        String email = (usernameField != null) ? usernameField.getText() : "";
        String pass = (passwordField != null) ? new String(passwordField.getPassword()) : "";

        if (email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill out both fields.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        UserDAO userDAO = new UserDAO();
        java.util.Optional<User> maybeUser = userDAO.getUserByEmail(email);

        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            if (user.getPassword() != null && user.getPassword().equals(pass)) {
                JOptionPane.showMessageDialog(this, "Login successful!");

                // Handle "Remember Me" functionality
                if (rememberMeCheckBox.isSelected()) {
                    saveCredentials(email, pass);
                } else {
                    clearCredentials();
                }

                dispose();
                new Home(user);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Incorrect password!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "User not found!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveCredentials(String email, String password) {
        try (FileWriter writer = new FileWriter("credentials.properties")) {
            Properties props = new Properties();
            props.setProperty("username", email);
            props.setProperty("password", password);
            props.store(writer, "User Credentials");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCredentials() {
        try (FileReader reader = new FileReader("credentials.properties")) {
            Properties props = new Properties();
            props.load(reader);
            String savedUsername = props.getProperty("username");
            String savedPassword = props.getProperty("password");
            if (savedUsername != null && savedPassword != null) {
                usernameField.setText(savedUsername);
                passwordField.setText(savedPassword);
                rememberMeCheckBox.setSelected(true);
            }
        } catch (IOException e) {
            // File might not exist, which is okay
        }
    }

    private void clearCredentials() {
        File file = new File("credentials.properties");
        if (file.exists()) {
            file.delete();
        }
    }


    public JPanel createInfoSection(
            String sectionTitle,
            Font headerFont,
            Font labelFont,
            String[] fieldNames,
            String[] fieldValues
    ) {
        // Outer panel to hold the title and the RoundedPanel
        JPanel outerPanel = new JPanel();
        outerPanel.setLayout(new BorderLayout(0, 10));
        outerPanel.setOpaque(false); // Make the outer layer transparent

        // Section title outside the RoundedPanel
        JLabel titleLabel = new JLabel(sectionTitle);
        titleLabel.setFont(headerFont);
        titleLabel.setForeground(Color.BLACK);
        outerPanel.add(titleLabel, BorderLayout.NORTH);

        // Create a rounded container panel using RoundedPanel
        RoundedPanel container = new RoundedPanel(20, 10); // 20px arc and 10px shadow
        container.setPreferredSize(new Dimension(75, 75));
        container.setBackground(new Color(240, 240, 240)); // Light gray background
        container.setLayout(new BorderLayout(0, 10));

        // Panel for info rows
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setOpaque(false); // Transparent to inherit RoundedPanel background
        container.add(infoPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        for (int i = 0; i < fieldNames.length; i++) {
            JLabel fieldNameLabel = new JLabel(fieldNames[i] + ": ");
            fieldNameLabel.setFont(labelFont.deriveFont(Font.BOLD));
            fieldNameLabel.setForeground(Color.BLACK); // Black text for field names

            JLabel fieldValueLabel = new JLabel(fieldValues[i]);
            fieldValueLabel.setFont(labelFont);
            fieldValueLabel.setForeground(Color.BLACK); // Black text for field values

            gbc.gridx = 0;
            infoPanel.add(fieldNameLabel, gbc);

            gbc.gridx = 1;
            infoPanel.add(fieldValueLabel, gbc);

            gbc.gridy++;
        }

        // Add the rounded container to the outer panel
        outerPanel.add(container, BorderLayout.CENTER);

        return outerPanel;
    }

    public static class RoundedButtonLogin extends JButton {
        private final Color backgroundColor;

        public RoundedButtonLogin(String text, Color backgroundColor) {
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
            // no border
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login::new);
    }
}
