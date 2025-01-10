package ui;

import model.LogEntry;
import model.User;
import service.LogService;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Displays a line chart with dots for the last 7 days of
 * average blood glucose. If a day has no data, shows a message.
 */
public class GlucoseGraph extends BaseUI {

    private User currentUser;

    public GlucoseGraph(User user) {
        super("Glucose Graph");
        this.currentUser = user;

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

        // Subheader in red
        JLabel subHeader = new JLabel("Last Week's Blood Glucose Trend");
        subHeader.setForeground(new Color(200, 40, 40)); // The red color used in your app
        subHeader.setFont(new Font("SansSerif", Font.BOLD, 14));
        subHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        subHeader.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        topPanel.add(subHeader);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // ===== CHART PANEL =====
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setOpaque(false);

        // Build the dataset for the last 7 days
        XYDataset dataset = buildLast7DaysDataset();
        JFreeChart chart = ChartFactory.createXYLineChart(
                "", // Title not needed here
                "Date", // X-axis label
                "Blood Glucose [mmol/L]", // Y-axis label
                dataset,
                PlotOrientation.VERTICAL,
                false, // no legend
                false, // no tooltips
                false  // no URLs
        );

        // Adjust range to 0–14
        XYPlot plot = chart.getXYPlot();
        plot.getRangeAxis().setRange(0.0, 14.0);

        // Show shapes (dots) on each point
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesShapesVisible(0, true);  // Show shapes
        renderer.setSeriesLinesVisible(0, true);   // Connect with lines
        plot.setRenderer(renderer);

        // Put chart in a ChartPanel
        ChartPanel cPanel = new ChartPanel(chart);
        chartPanel.add(cPanel, BorderLayout.CENTER);

        mainPanel.add(chartPanel, BorderLayout.CENTER);

        // ===== BOTTOM NAV BAR =====
        JPanel navBar = createBottomNavBar("Home", currentUser,
                "/Icons/home.png", "/Icons/logbook.png", "/Icons/profile.png");
        mainPanel.add(navBar, BorderLayout.SOUTH);
    }

    /**
     * Builds an XYDataset with 7 points for the last 7 days,
     * each point is the average BG for that day (or no data => message).
     */
    private XYDataset buildLast7DaysDataset() {
        DefaultXYDataset dataset = new DefaultXYDataset();

        // We'll create 2 arrays: xValues (dates as double), yValues (BG average)
        // Then we'll label the x-axis with day index or use date strings as tooltips.

        double[] xValues = new double[7];
        double[] yValues = new double[7];

        LocalDate today = LocalDate.now();
        DateTimeFormatter dFmt = DateTimeFormatter.ofPattern("d MMM");

        // We'll store strings for each date to see if it's empty
        // We'll display a separate label for "No log data found" if needed
        StringBuilder missingDataMessages = new StringBuilder("<html>");

        for (int i = 0; i < 7; i++) {
            // day i means (today - i)
            LocalDate thisDate = today.minusDays(6 - i);
            // So that x=0 => 6 days ago, x=6 => today

            xValues[i] = i;  // numeric x
            List<LogEntry> dayEntries = LogService.getEntriesForDate(currentUser.getId(), thisDate.toString());

            if (dayEntries.isEmpty()) {
                // No logs => record 0 or NaN
                yValues[i] = Double.NaN; // so no dot is drawn
                missingDataMessages.append("No log data found for ")
                        .append(thisDate.format(dFmt))
                        .append("<br/>");
            } else {
                // Compute average
                double sum = 0;
                for (LogEntry e : dayEntries) {
                    sum += e.getBloodSugar();
                }
                double avg = sum / dayEntries.size();
                yValues[i] = avg;
            }
        }

        missingDataMessages.append("</html>");
        
        // data format: double[2][N] => [0]=x array, [1]=y array
        double[][] seriesData = new double[2][7];
        seriesData[0] = xValues;
        seriesData[1] = yValues;
        dataset.addSeries("BG Trend", seriesData);

        // If we had missing data, show a small dialog or label:
        // We'll show a small dialog if there's any missing lines
        String missingInfo = missingDataMessages.toString();
        if (!missingInfo.equals("<html></html>")) {
            // means we found at least one day with no logs
            JOptionPane.showMessageDialog(
                    this,
                    missingInfo,
                    "Missing Data",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }

        return dataset;
    }
}