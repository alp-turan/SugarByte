package ui;

import model.LogEntry;
import model.User;
import service.LogService;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

import javax.imageio.ImageIO;
import java.util.ArrayList;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Represents the `GlucoseGraph` class for displaying a user's glucose levels as a graph.
 * Includes options to select a date range and visualize the data.
 */
public class GlucoseGraph extends BaseUI {

    private User currentUser; // The currently logged-in user
    private LocalDate startDate; // The start date of the graph range
    private LocalDate endDate; // The end date of the graph range
    private ChartPanel chartPanel; // The panel to display the graph

    /**
     * Constructs a `GlucoseGraph` instance with the specified user.
     * Initializes the default date range to the last 7 days and builds the UI.
     *
     * @param user The logged-in user for whom the graph is generated.
     */
    public GlucoseGraph(User user) {
        super("Glucose Graph"); // Set the window title
        this.currentUser = user; // Assign the user to the current instance

        // Set the default date range to the last 7 days
        this.startDate = LocalDate.now().minusDays(6); // Start date is 6 days ago
        this.endDate = LocalDate.now(); // End date is today

        buildUI(); // Build the user interface
        setVisible(true); // Make the frame visible
    }

    /**
     * Builds the UI components for the glucose graph screen, including the graph and controls.
     */
    private void buildUI() {
        // Create the main panel with a gradient background
        JPanel mainPanel = createGradientPanel(Color.WHITE, Color.WHITE);
        mainPanel.setLayout(new BorderLayout()); // Use a BorderLayout for organization
        setContentPane(mainPanel); // Set the main panel as the content area

        // ===== TOP PANEL =====
        JPanel topPanel = new JPanel(); // Create the top panel
        topPanel.setOpaque(false); // Make the top panel transparent
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS)); // Use vertical stacking for layout
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // Add padding around the top panel

        JLabel titleLabel = createTitleLabel("SugarByte", lobsterFont, Color.BLACK); // Create the title label
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center-align the title
        topPanel.add(titleLabel); // Add the title label to the top panel

        // ===== DATE PICKERS PANEL =====
        JPanel datePickersPanel = new JPanel(); // Create a panel for date selection
        datePickersPanel.setOpaque(false); // Make the panel transparent
        datePickersPanel.setLayout(new BoxLayout(datePickersPanel, BoxLayout.Y_AXIS)); // Use vertical stacking for layout
        datePickersPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 20, 15)); // Add padding around the panel

        // ===== Start Date =====
        JLabel startDateLabel = new JLabel("Start date:"); // Label for the start date picker
        startDateLabel.setFont(startDateLabel.getFont().deriveFont(14f)); // Set a larger font size
        startDateLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center-align the label
        datePickersPanel.add(startDateLabel); // Add the label to the date pickers panel

        JComboBox<String> startDateBox = createDateComboBox(); // Create a combo box for the start date
        startDateBox.setPreferredSize(new Dimension(150, startDateBox.getPreferredSize().height)); // Set fixed dimensions
        startDateBox.setAlignmentX(Component.CENTER_ALIGNMENT); // Center-align the combo box
        startDateBox.setRenderer(new DefaultListCellRenderer() { // Customize the appearance of the combo box
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setHorizontalAlignment(SwingConstants.CENTER); // Center-align the text inside the combo box
                return label;
            }
        });
        datePickersPanel.add(startDateBox); // Add the start date combo box to the panel

        // ===== End Date =====
        JLabel endDateLabel = new JLabel("End date:"); // Label for the end date picker
        endDateLabel.setFont(endDateLabel.getFont().deriveFont(14f)); // Set a larger font size
        endDateLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center-align the label
        datePickersPanel.add(endDateLabel); // Add the label to the date pickers panel

        JComboBox<String> endDateBox = createDateComboBox(); // Create a combo box for the end date
        endDateBox.setPreferredSize(new Dimension(150, endDateBox.getPreferredSize().height)); // Set fixed dimensions
        endDateBox.setAlignmentX(Component.CENTER_ALIGNMENT); // Center-align the combo box
        endDateBox.setRenderer(new DefaultListCellRenderer() { // Customize the appearance of the combo box
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setHorizontalAlignment(SwingConstants.CENTER); // Center-align the text inside the combo box
                return label;
            }
        });
        datePickersPanel.add(endDateBox); // Add the end date combo box to the panel

        datePickersPanel.add(Box.createVerticalStrut(10)); // Add vertical space between components

        // ===== Generate Button =====
        RoundedButton generateButton = new RoundedButton("Generate Graph", new Color(237, 165, 170)); // Create a button for generating the graph
        generateButton.setFont(generateButton.getFont().deriveFont(14f)); // Set the font size
        generateButton.setMargin(new Insets(5, 15, 5, 15)); // Add padding around the button
        generateButton.addActionListener(e -> { // Add an action listener for the button
            try {
                startDate = parseDate((String) startDateBox.getSelectedItem()); // Parse the selected start date
                endDate = parseDate((String) endDateBox.getSelectedItem()); // Parse the selected end date
                if (startDate.isAfter(endDate)) { // Ensure the start date is not after the end date
                    JOptionPane.showMessageDialog(this,
                            "Start date cannot be after end date. Please select valid dates.",
                            "Date Error",
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    updateGraph(); // Update the graph for the selected date range
                }
            } catch (DateTimeParseException ex) { // Handle invalid date formats
                JOptionPane.showMessageDialog(this,
                        "Invalid date format. Please select a valid date.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // "Send to Doctor" Button
        RoundedButton sendToDoctorButton = new RoundedButton("Send to doctor", new Color(237, 165, 170));  // Create a button with a red-pink color
        Font sendButtonFont = sendToDoctorButton.getFont().deriveFont(14f);  // Set the font size to 14
        sendToDoctorButton.setFont(sendButtonFont);  // Apply the new font to the button
        sendToDoctorButton.addActionListener(e -> sendDataToDoctor());  // Add an action listener to handle the button click

// Panel for holding buttons at the top of the screen
        JPanel buttonPanel = new JPanel();  // Create a panel for the "Generate Graph" button
        buttonPanel.setOpaque(false);  // Make the panel background transparent
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));  // Center-align components within the panel
        buttonPanel.add(generateButton);  // Add the "Generate Graph" button to the panel

// Panel for holding the "Send to Doctor" button
        JPanel doctorButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));  // Center-align components within the panel
        doctorButtonPanel.setOpaque(false);  // Make the panel background transparent
        doctorButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));  // Add padding around the panel
        doctorButtonPanel.add(sendToDoctorButton);  // Add the "Send to Doctor" button to the panel

// Add the date pickers and buttons to the top panel
        topPanel.add(datePickersPanel);  // Add the date pickers panel to the top panel
        topPanel.add(buttonPanel);  // Add the "Generate Graph" button panel to the top panel
        mainPanel.add(topPanel, BorderLayout.NORTH);  // Place the top panel at the top of the main layout

// Chart panel for displaying the glucose graph
        chartPanel = new ChartPanel(null);  // Create a ChartPanel without a dataset initially
        chartPanel.setOpaque(false);  // Make the chart panel background transparent
        chartPanel.setPreferredSize(new Dimension(800, 250));  // Set the preferred size of the chart panel
        chartPanel.revalidate();  // Refresh the chart panel layout
        chartPanel.repaint();  // Trigger a repaint to reflect any changes
        mainPanel.add(chartPanel, BorderLayout.CENTER);  // Place the chart panel at the center of the main layout

// Bottom wrapper for holding navigation bar and doctor button panel
        JPanel bottomWrapper = new JPanel();  // Create a wrapper panel for the bottom components
        bottomWrapper.setLayout(new BoxLayout(bottomWrapper, BoxLayout.Y_AXIS));  // Stack components vertically
        bottomWrapper.setOpaque(false);  // Make the panel background transparent

// Navigation bar for the glucose graph page
        JPanel navBar = createBottomNavBar("GlucoseGraph", currentUser,
                "/Icons/home.png", "/Icons/logbook.png", "/Icons/graphfull.png", "/Icons/profile.png");  // Create a navigation bar with icons

// Add the doctor button panel and navigation bar to the bottom wrapper
        bottomWrapper.add(doctorButtonPanel);  // Add the "Send to Doctor" button panel
        bottomWrapper.add(navBar);  // Add the navigation bar

// Place the bottom wrapper at the bottom of the main layout
        mainPanel.add(bottomWrapper, BorderLayout.SOUTH);

// Generate the initial graph for the default date range
        updateGraph();

    }

    /**
     * Updates the chart panel with a new glucose graph based on the selected date range.
     */
    private void updateGraph() {
        // Build the dataset for the selected date range
        XYDataset dataset = buildDatasetForRange();

        // Create the chart with a vertical orientation and no legend, tooltips, or URLs
        JFreeChart chart = ChartFactory.createXYLineChart(
                "",  // No title
                "Date",  // X-axis label
                "Blood Glucose [mmol/L]",  // Y-axis label
                dataset,  // Dataset for the chart
                PlotOrientation.VERTICAL,  // Vertical plot orientation
                false,  // No legend
                false,  // No tooltips
                false   // No URLs
        );

        // Customize the plot
        XYPlot plot = chart.getXYPlot();

        // Configure the domain axis (X-axis)
        DateAxis dateAxis = new DateAxis("Date");  // Create a date axis with the label "Date"
        dateAxis.setLabelFont(dateAxis.getLabelFont().deriveFont(Font.BOLD));  // Set the label font to bold
        Font newFont = dateAxis.getLabelFont().deriveFont(14f);  // Set the font size to 14
        dateAxis.setLabelFont(newFont);  // Apply the new font size
        dateAxis.setDateFormatOverride(new SimpleDateFormat("d MMM"));  // Format dates as "day month"
        plot.setDomainAxis(dateAxis);  // Set the configured axis as the domain axis

        // Configure the range axis (Y-axis)
        ValueAxis rangeAxis = plot.getRangeAxis();  // Get the range axis
        rangeAxis.setLabelFont(rangeAxis.getLabelFont().deriveFont(Font.BOLD));  // Set the label font to bold

        // Customize the renderer for the chart
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();  // Create a line-and-shape renderer
        renderer.setSeriesShapesVisible(0, true);  // Enable shapes for the first series
        renderer.setSeriesLinesVisible(0, true);  // Enable lines for the first series
        plot.setRenderer(renderer);  // Set the renderer for the plot

        // Update the chart panel with the new chart
        chartPanel.setChart(chart);
    }

    /**
     * Builds the dataset for the glucose graph based on the selected date range.
     *
     * @return An XYDataset containing blood glucose levels over the specified date range.
     */
    private XYDataset buildDatasetForRange() {
        DefaultXYDataset dataset = new DefaultXYDataset();  // Dataset to store the series
        int numDays = (int) (endDate.toEpochDay() - startDate.toEpochDay() + 1);  // Calculate the number of days in the range
        if (numDays <= 0) {  // If the range is invalid (negative or zero days), return an empty dataset
            return dataset;
        }

        double[] xValues = new double[numDays];  // Array to store X-axis values (dates)
        double[] yValues = new double[numDays];  // Array to store Y-axis values (blood glucose levels)
        List<String> missingDates = new ArrayList<>();  // List to collect dates with no entries

        for (int i = 0; i < numDays; i++) {
            LocalDate currentDate = startDate.plusDays(i);  // Get the current date in the range
            xValues[i] = java.sql.Date.valueOf(currentDate).getTime();  // Convert the date to a timestamp for X-axis

            List<LogEntry> dayEntries = LogService.getEntriesForDate(currentUser.getId(), currentDate.toString());  // Get log entries for the date

            if (dayEntries.isEmpty()) {
                yValues[i] = Double.NaN;  // Mark missing data points with NaN
                missingDates.add(currentDate.format(DateTimeFormatter.ofPattern("d MMM yyyy")));  // Add the missing date to the list
            } else {
                double sum = 0;
                for (LogEntry e : dayEntries) {
                    sum += e.getBloodSugar();  // Sum up all blood glucose values for the day
                }
                yValues[i] = sum / dayEntries.size();  // Calculate the average glucose level for the day
            }
        }

        dataset.addSeries("BG Trend", new double[][]{xValues, yValues});  // Add the series to the dataset

        // Notify the user of missing dates
        if (!missingDates.isEmpty()) {
            StringBuilder missingDatesStr = new StringBuilder();  // Build the warning message
            for (String missingDate : missingDates) {
                missingDatesStr.append(missingDate).append("\n");  // Append each date to the message
            }

            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this,
                        "The following dates have no blood glucose entries:\n" + missingDatesStr.toString() +
                                "The graph might be incomplete.",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
            });
        }

        return dataset;  // Return the dataset
    }

    /**
     * Creates a combo box for date selection.
     *
     * @return A JComboBox pre-populated with dates from the last 30 days.
     */
    private JComboBox<String> createDateComboBox() {
        JComboBox<String> dateBox = new JComboBox<>();  // Create a new combo box
        dateBox.addItem("Please select date for graph generation");  // Add a placeholder item
        dateBox.setSelectedIndex(0);  // Set the placeholder as the default selection

        LocalDate today = LocalDate.now();  // Get the current date
        for (int i = 0; i < 30; i++) {
            LocalDate date = today.minusDays(i);  // Generate the date for each day in the past 30 days
            dateBox.addItem(formatDateWithOrdinal(date));  // Format the date with an ordinal suffix and add it to the combo box
        }
        return dateBox;
    }

    /**
     * Formats a LocalDate with an ordinal suffix for display.
     *
     * @param date The date to format.
     * @return A string representation of the date with an ordinal suffix.
     */
    private String formatDateWithOrdinal(LocalDate date) {
        int day = date.getDayOfMonth();  // Get the day of the month
        String suffix = getDaySuffix(day);  // Determine the appropriate ordinal suffix
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d'" + suffix + "' MMM yyyy");  // Define the date format
        return date.format(formatter);  // Format and return the date
    }

    /**
     * Determines the ordinal suffix for a given day of the month.
     *
     * @param day The day of the month.
     * @return The ordinal suffix as a string (e.g., "st", "nd", "rd", "th").
     */
    private String getDaySuffix(int day) {
        if (day >= 11 && day <= 13) return "th";  // Special case for 11th, 12th, and 13th
        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    /**
     * Parses a date string in the format "d MMM yyyy" (with ordinal suffixes) into a LocalDate.
     *
     * @param dateString The date string to parse.
     * @return The parsed LocalDate.
     */
    private LocalDate parseDate(String dateString) {
        String cleanedDateString = dateString.replaceAll("(\\d+)(st|nd|rd|th)", "$1");  // Remove ordinal suffixes
        return LocalDate.parse(cleanedDateString, DateTimeFormatter.ofPattern("d MMM yyyy"));  // Parse the cleaned date
    }

    /**
     * Sends the glucose graph to the user's doctor via email.
     */
    private void sendDataToDoctor() {
        final String fromEmail = "sugarbyte.app@gmail.com";  // Sender's email
        final String appPassword = "twym wigt ytak botd";  // App-specific password

        // Configure SMTP properties
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        // Authenticate with the SMTP server
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, appPassword);
            }
        });

        try {
            // Capture the chart as an image
            BufferedImage chartImage = chartPanel.getChart().createBufferedImage(chartPanel.getWidth(), chartPanel.getHeight());
            File tempFile = new File("glucose_graph.png");  // Temporary file for the image
            ImageIO.write(chartImage, "png", tempFile);  // Write the image to the file

            // Prepare email details
            String doctorEmail = currentUser.getDoctorEmail();
            String doctorName = currentUser.getDoctorName();
            String userName = currentUser.getName();
            String formattedStartDate = formatDateWithOrdinal(startDate);  // Format start date
            String formattedEndDate = formatDateWithOrdinal(endDate);  // Format end date

            // Create and configure the email message
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(doctorEmail));
            message.setSubject("Glucose Graph: " + formattedStartDate + " to " + formattedEndDate);

            // Add the email body
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(String.format(
                    "Dear Dr. %s,\n\nPlease find attached the glucose graph for your patient %s from %s to %s.\n\nBest regards,\nSugarByte",
                    doctorName, userName, formattedStartDate, formattedEndDate));

            // Attaching the graph image
            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(tempFile);

            // combining the email body and attachment
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(attachmentPart);

            message.setContent(multipart);

            // Sending the email using Java Mail's in-built transport method
            Transport.send(message);

            // Notify the user of success
            JOptionPane.showMessageDialog(this, "Graph sent to your doctor successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to send the email: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}

