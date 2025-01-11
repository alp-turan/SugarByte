package ui;

import model.LogEntry;
import model.User;
import service.LogService;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

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
        // Main gradient background
        JPanel mainPanel = createGradientPanel(Color.WHITE, Color.WHITE);
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);

        // ===== TOP PANEL =====
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // SugarByte title
        JLabel titleLabel = createTitleLabel("SugarByte", lobsterFont, Color.BLACK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(titleLabel);

        // Date selection controls
        JPanel dateSelectionPanel = new JPanel();
        dateSelectionPanel.setOpaque(false);
        dateSelectionPanel.setLayout(new FlowLayout());

        JLabel startDateLabel = new JLabel("Start Date:");
        JLabel endDateLabel = new JLabel("End Date:");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        JComboBox<String> startDateBox = createDateComboBox(formatter);
        JComboBox<String> endDateBox = createDateComboBox(formatter);

        JButton generateButton = new JButton("Generate Graph");
        generateButton.addActionListener(e -> {
            startDate = LocalDate.parse((String) startDateBox.getSelectedItem(), formatter);
            endDate = LocalDate.parse((String) endDateBox.getSelectedItem(), formatter);
            updateGraph();
        });

        // Add Send to Doctor button
        JButton sendToDoctorButton = new JButton("Send to Doctor");
        sendToDoctorButton.addActionListener(e -> {
            sendDataToDoctor();
        });

        dateSelectionPanel.add(startDateLabel);
        dateSelectionPanel.add(startDateBox);
        dateSelectionPanel.add(endDateLabel);
        dateSelectionPanel.add(endDateBox);
        dateSelectionPanel.add(generateButton);
        dateSelectionPanel.add(sendToDoctorButton); // Add the button next to generate

        topPanel.add(dateSelectionPanel);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // ===== CHART PANEL =====
        chartPanel = new ChartPanel(null);
        chartPanel.setOpaque(false);

        // Set the graph height to be 3/4 (lower height)
        chartPanel.setPreferredSize(new Dimension(800, 400)); // Change the dimensions as needed
        chartPanel.revalidate();
        chartPanel.repaint();
        mainPanel.add(chartPanel, BorderLayout.CENTER);

        updateGraph();

        // ===== BOTTOM NAV BAR =====
        JPanel navBar = createBottomNavBar("GlucoseGraph", currentUser,
                "/Icons/home.png", "/Icons/logbook.png", "/Icons/graphfull.png", "/Icons/profile.png");
        mainPanel.add(navBar, BorderLayout.SOUTH);
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
        plot.getRangeAxis().setRange(1.0, 14.0);  // No change in range but will be squashed

        // Squash the graph vertically
        plot.getRangeAxis().setInverted(false); // Make sure Y-axis is increasing downwards
        plot.getRangeAxis().setLowerBound(1);
        plot.getRangeAxis().setUpperBound(14);

        // Set custom date formatter for the x-axis
        DateAxis dateAxis = new DateAxis("Date");
        dateAxis.setDateFormatOverride(new SimpleDateFormat("d MMM"));
        plot.setDomainAxis(dateAxis); // Set custom date axis

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
        double[] xValues = new double[numDays];
        double[] yValues = new double[numDays];

        Date[] dateValues = new Date[numDays]; // Change xValues to Date array
        DateTimeFormatter dFmt = DateTimeFormatter.ofPattern("d MMM");
        StringBuilder missingDataMessages = new StringBuilder("<html>");

        for (int i = 0; i < numDays; i++) {
            LocalDate currentDate = startDate.plusDays(i);
            dateValues[i] = java.sql.Date.valueOf(currentDate); // Convert LocalDate to Date
            xValues[i] = dateValues[i].getTime(); // Store time in milliseconds for Date axis

            List<LogEntry> dayEntries = LogService.getEntriesForDate(currentUser.getId(), currentDate.toString());

            if (dayEntries.isEmpty()) {
                yValues[i] = Double.NaN;
                missingDataMessages.append("No log data found for ")
                        .append(currentDate.format(dFmt))
                        .append("<br>");
            } else {
                double sum = 0;
                for (LogEntry e : dayEntries) {
                    sum += e.getBloodSugar();
                }
                yValues[i] = sum / dayEntries.size();
            }
        }

        missingDataMessages.append("</html>");

        double[][] seriesData = new double[2][numDays];
        seriesData[0] = xValues;
        seriesData[1] = yValues;
        dataset.addSeries("BG Trend", seriesData);

        if (!missingDataMessages.toString().equals("<html></html>")) {
            SwingUtilities.invokeLater(() -> {
                JDialog warningDialog = new JDialog(this, "Missing Data", false);
                warningDialog.setLayout(new BorderLayout());
                JLabel messageLabel = new JLabel(missingDataMessages.toString());
                messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                warningDialog.add(messageLabel, BorderLayout.CENTER);

                JButton closeButton = new JButton("OK");
                closeButton.addActionListener(e -> warningDialog.dispose());
                JPanel buttonPanel = new JPanel();
                buttonPanel.add(closeButton);
                warningDialog.add(buttonPanel, BorderLayout.SOUTH);

                warningDialog.pack();
                warningDialog.setLocationRelativeTo(this);
                warningDialog.setVisible(true);
            });
        }

        return dataset;
    }

    /**
     * Creates a JComboBox with date values for the past 30 days.
     */
    private JComboBox<String> createDateComboBox(DateTimeFormatter formatter) {
        JComboBox<String> dateBox = new JComboBox<>();
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 30; i++) {
            LocalDate date = today.minusDays(i);
            dateBox.addItem(date.format(formatter));
        }
        return dateBox;
    }

    /**
     * Simulate sending data to the doctor.
     */
    private void sendDataToDoctor() {
        // Here you would implement the logic to send the data and graph to the doctor.
        // For now, we will just show a message.
        JOptionPane.showMessageDialog(this, "Data sent to your doctor for the selected date range.");
    }
}
