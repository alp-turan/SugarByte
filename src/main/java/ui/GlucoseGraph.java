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

/**
 * Displays a glucose graph for a custom date range,
 * with a shorter vertical chart area and separate rows for date pickers.
 * There's also space at the bottom for a "Send to Doctor" button and the nav bar.
 */
public class GlucoseGraph extends BaseUI {

    private User currentUser;
    private LocalDate startDate;
    private LocalDate endDate;
    private ChartPanel chartPanel;

    public GlucoseGraph(User user) {
        super("Glucose Graph");
        this.currentUser = user;

        // Default to last 7 days
        this.startDate = LocalDate.now().minusDays(6);
        this.endDate = LocalDate.now();

        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        // ========== MAIN PANEL WITH GRADIENT ==========
        JPanel mainPanel = createGradientPanel(Color.WHITE, Color.WHITE);
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);

        // ========== TOP PANEL ==========
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Title Label (SugarByte)
        JLabel titleLabel = createTitleLabel("SugarByte", lobsterFont, Color.BLACK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(titleLabel);

        // Create a panel for date pickers (BoxLayout Y -> separate lines)
        JPanel datePickersPanel = new JPanel();
        datePickersPanel.setOpaque(false);
        datePickersPanel.setLayout(new BoxLayout(datePickersPanel, BoxLayout.Y_AXIS));
        datePickersPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // ========== ROW 1: START DATE ==========
        JPanel startRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        startRow.setOpaque(false);
        JLabel startDateLabel = new JLabel("Start Date:");
        startDateLabel.setFont(startDateLabel.getFont().deriveFont(Font.BOLD));
        JComboBox<String> startDateBox = createDateComboBox();
        startRow.add(startDateLabel);
        startRow.add(startDateBox);

        // ========== ROW 2: END DATE + GENERATE BUTTON ==========
        // Instead of FlowLayout, we'll use BoxLayout horizontally
        // so the button can be placed on the right, and the combo on the left.
        JPanel endRow = new JPanel();
        endRow.setLayout(new BoxLayout(endRow, BoxLayout.X_AXIS));
        endRow.setOpaque(false);

        JLabel endDateLabel = new JLabel("End Date:");
        endDateLabel.setFont(endDateLabel.getFont().deriveFont(Font.BOLD));
        JComboBox<String> endDateBox = createDateComboBox();

        JButton generateButton = new JButton("Generate Graph");
        generateButton.addActionListener(e -> {
            try {
                startDate = parseDate((String) startDateBox.getSelectedItem());
                endDate = parseDate((String) endDateBox.getSelectedItem());
                updateGraph();
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid date format. Please select a valid date.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Add label + combo on the left
        endRow.add(endDateLabel);
        endRow.add(Box.createHorizontalStrut(10)); // small spacing
        endRow.add(endDateBox);

        // A "glue" that takes remaining horizontal space and pushes the button to the right
        endRow.add(Box.createHorizontalGlue());
        endRow.add(generateButton);

        // Add both rows to datePickersPanel
        datePickersPanel.add(startRow);
        datePickersPanel.add(endRow);

        topPanel.add(datePickersPanel);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // ========== CENTER: CHART PANEL ==========
        chartPanel = new ChartPanel(null);
        chartPanel.setOpaque(false);
        // Make the chart physically shorter in height
        chartPanel.setPreferredSize(new Dimension(800, 250));

        chartPanel.revalidate();
        chartPanel.repaint();
        mainPanel.add(chartPanel, BorderLayout.CENTER);

        // ========== BOTTOM AREA: "Send to Doctor" + Nav Bar ==========
        JPanel bottomWrapper = new JPanel();
        bottomWrapper.setLayout(new BoxLayout(bottomWrapper, BoxLayout.Y_AXIS));
        bottomWrapper.setOpaque(false);

        // Send to Doctor row
        JPanel doctorButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        doctorButtonPanel.setOpaque(false);
        doctorButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JButton sendToDoctorButton = new JButton("Send to Doctor");
        sendToDoctorButton.addActionListener(e -> sendDataToDoctor());
        doctorButtonPanel.add(sendToDoctorButton);

        // Bottom Nav
        JPanel navBar = createBottomNavBar("GlucoseGraph", currentUser,
                "/Icons/home.png", "/Icons/logbook.png", "/Icons/graphfull.png", "/Icons/profile.png");

        bottomWrapper.add(doctorButtonPanel);
        bottomWrapper.add(navBar);

        mainPanel.add(bottomWrapper, BorderLayout.SOUTH);

        // Initialize the chart
        updateGraph();
    }

    /**
     * Updates the graph based on the selected start and end dates.
     */
    private void updateGraph() {
        XYDataset dataset = buildDatasetForRange();
        JFreeChart chart = ChartFactory.createXYLineChart(
                "", // No title
                "Date", // X-axis label
                "Blood Glucose [mmol/L]", // Y-axis label
                dataset,
                PlotOrientation.VERTICAL,
                false, // No legend
                false, // No tooltips
                false  // No URLs
        );

        XYPlot plot = chart.getXYPlot();

        // Set custom date formatter for the x-axis
        DateAxis dateAxis = new DateAxis("Date");
        dateAxis.setLabelFont(dateAxis.getLabelFont().deriveFont(Font.BOLD)); // Bold the "Date" label
        dateAxis.setDateFormatOverride(new SimpleDateFormat("d MMM"));
        plot.setDomainAxis(dateAxis);

        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setLabelFont(rangeAxis.getLabelFont().deriveFont(Font.BOLD)); // Bold the Y-axis label

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesLinesVisible(0, true);
        plot.setRenderer(renderer);

        chartPanel.setChart(chart);
    }

    /**
     * Builds an XYDataset for the specified date range.
     */
    private XYDataset buildDatasetForRange() {
        DefaultXYDataset dataset = new DefaultXYDataset();

        int numDays = (int) (endDate.toEpochDay() - startDate.toEpochDay() + 1);
        if (numDays <= 0) {
            // If the user picks start date after end date
            return dataset; // empty
        }

        double[] xValues = new double[numDays];
        double[] yValues = new double[numDays];

        for (int i = 0; i < numDays; i++) {
            LocalDate currentDate = startDate.plusDays(i);
            xValues[i] = java.sql.Date.valueOf(currentDate).getTime();
            List<LogEntry> dayEntries = LogService.getEntriesForDate(currentUser.getId(), currentDate.toString());

            if (dayEntries.isEmpty()) {
                yValues[i] = Double.NaN; // no data
            } else {
                double sum = 0;
                for (LogEntry e : dayEntries) {
                    sum += e.getBloodSugar();
                }
                yValues[i] = sum / dayEntries.size();
            }
        }

        dataset.addSeries("BG Trend", new double[][]{xValues, yValues});
        return dataset;
    }

    /**
     * Creates a JComboBox with date values formatted as "31st Jan 2025".
     * We'll fill it with the last 30 days for demonstration.
     */
    private JComboBox<String> createDateComboBox() {
        JComboBox<String> dateBox = new JComboBox<>();
        LocalDate today = LocalDate.now();

        for (int i = 0; i < 30; i++) {
            LocalDate date = today.minusDays(i);
            dateBox.addItem(formatDateWithOrdinal(date));
        }
        return dateBox;
    }

    /**
     * Formats a LocalDate as "31st Jan 2025".
     */
    private String formatDateWithOrdinal(LocalDate date) {
        int day = date.getDayOfMonth();
        String suffix = getDaySuffix(day);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d'" + suffix + "' MMM yyyy");
        return date.format(formatter);
    }

    /**
     * Returns the ordinal suffix for a given day of the month (st, nd, rd, th).
     */
    private String getDaySuffix(int day) {
        if (day >= 11 && day <= 13) return "th";
        switch (day % 10) {
            case 1:  return "st";
            case 2:  return "nd";
            case 3:  return "rd";
            default: return "th";
        }
    }

    /**
     * Parses a date string with ordinal suffix (e.g., "11th Jan 2025") into a LocalDate object.
     */
    private LocalDate parseDate(String dateString) {
        // Remove ordinal suffixes like "st", "nd", "rd", "th"
        String cleanedDateString = dateString.replaceAll("(\\d+)(st|nd|rd|th)", "$1");
        return LocalDate.parse(cleanedDateString, DateTimeFormatter.ofPattern("d MMM yyyy"));
    }

    /**
     * Captures the chart as an image and (simulated) sends it to the user's doctor.
     */
    private void sendDataToDoctor() {
        try {
            // Capture the chart as an image
            BufferedImage chartImage = chartPanel.getChart().createBufferedImage(
                    chartPanel.getWidth(),
                    chartPanel.getHeight()
            );
            File tempFile = new File("graph.png");
            ImageIO.write(chartImage, "png", tempFile);

            // Retrieve the doctor's email
            String doctorEmail = currentUser.getDoctorEmail();

            // Simulated email sending
            System.out.println("Sending graph to " + doctorEmail);
            JOptionPane.showMessageDialog(this,
                    "Graph sent to your doctor at " + doctorEmail,
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to send the graph: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}