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
        JPanel mainPanel = createGradientPanel(Color.WHITE, Color.WHITE);
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);

        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel titleLabel = createTitleLabel("SugarByte", lobsterFont, Color.BLACK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(titleLabel);

        // Panel for the start and end date pickers
        JPanel datePickersPanel = new JPanel();
        datePickersPanel.setOpaque(false);
        datePickersPanel.setLayout(new BoxLayout(datePickersPanel, BoxLayout.Y_AXIS));  // Stack components vertically
        datePickersPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Start Date Label and ComboBox
        JLabel startDateLabel = new JLabel("Start date:");
        startDateLabel.setFont(startDateLabel.getFont().deriveFont(14f));  // Set font size
        startDateLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the label

        JComboBox<String> startDateBox = createDateComboBox();
        startDateBox.setPreferredSize(new Dimension(150, startDateBox.getPreferredSize().height));  // Set fixed width and make it shorter
        startDateBox.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the combo box
        startDateBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setHorizontalAlignment(SwingConstants.CENTER); // Center the text inside the combo box
                return label;
            }
        });

        // End Date Label and ComboBox
        JLabel endDateLabel = new JLabel("End date:");
        endDateLabel.setFont(endDateLabel.getFont().deriveFont(14f));  // Set font size
        endDateLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the label

        JComboBox<String> endDateBox = createDateComboBox();
        endDateBox.setPreferredSize(new Dimension(150, endDateBox.getPreferredSize().height));  // Set fixed width and make it shorter
        endDateBox.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the combo box
        endDateBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setHorizontalAlignment(SwingConstants.CENTER); // Center the text inside the combo box
                return label;
            }
        });

        // Add start and end date components to the datePickersPanel
        datePickersPanel.add(startDateLabel);
        datePickersPanel.add(startDateBox);
        datePickersPanel.add(Box.createVerticalStrut(10));  // Add some vertical space
        datePickersPanel.add(endDateLabel);
        datePickersPanel.add(endDateBox);

        // "Generate Graph" Button centered below the date pickers
        RoundedButton generateButton = new RoundedButton("Generate graph", new Color(237, 165, 170));  // Light Blue Color
        generateButton.setPreferredSize(new Dimension(150, 40));  // Control the button size
        Font buttonFont = generateButton.getFont().deriveFont(14f);  // Set font size
        generateButton.setFont(buttonFont);
        generateButton.addActionListener(e -> {
            try {
                // Parse the selected dates
                startDate = parseDate((String) startDateBox.getSelectedItem());
                endDate = parseDate((String) endDateBox.getSelectedItem());

                // Check if start date is after the end date
                if (startDate.isAfter(endDate)) {
                    // Show a warning message
                    JOptionPane.showMessageDialog(this,
                            "Start date cannot be after end date. Please select valid dates.",
                            "Date Error",
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    // If the dates are valid, update the graph
                    updateGraph();
                }
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid date format. Please select a valid date.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });


        // "Send to Doctor" Button
        RoundedButton sendToDoctorButton = new RoundedButton("Send to doctor", new Color(237, 165, 170));  // Green Color
        sendToDoctorButton.setPreferredSize(new Dimension(140, 40));  // Button size
        Font sendButtonFont = sendToDoctorButton.getFont().deriveFont(14f);  // Set font size
        sendToDoctorButton.setFont(sendButtonFont);
        sendToDoctorButton.addActionListener(e -> sendDataToDoctor());

        // Add components to the top panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(generateButton);

        JPanel doctorButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        doctorButtonPanel.setOpaque(false);
        doctorButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        doctorButtonPanel.add(sendToDoctorButton);

        topPanel.add(datePickersPanel);
        topPanel.add(buttonPanel);  // Centered "Generate Graph" button
        mainPanel.add(topPanel, BorderLayout.NORTH);

        chartPanel = new ChartPanel(null);
        chartPanel.setOpaque(false);
        chartPanel.setPreferredSize(new Dimension(800, 250));
        chartPanel.revalidate();
        chartPanel.repaint();
        mainPanel.add(chartPanel, BorderLayout.CENTER);

        JPanel bottomWrapper = new JPanel();
        bottomWrapper.setLayout(new BoxLayout(bottomWrapper, BoxLayout.Y_AXIS));
        bottomWrapper.setOpaque(false);

        JPanel navBar = createBottomNavBar("GlucoseGraph", currentUser,
                "/Icons/home.png", "/Icons/logbook.png", "/Icons/graphfull.png", "/Icons/profile.png");

        bottomWrapper.add(doctorButtonPanel);
        bottomWrapper.add(navBar);

        mainPanel.add(bottomWrapper, BorderLayout.SOUTH);

        updateGraph();
    }





    private void updateGraph() {
        XYDataset dataset = buildDatasetForRange();
        JFreeChart chart = ChartFactory.createXYLineChart(
                "",
                "Date",
                "Blood Glucose [mmol/L]",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                false,
                false
        );

        XYPlot plot = chart.getXYPlot();
        DateAxis dateAxis = new DateAxis("Date");
        dateAxis.setLabelFont(dateAxis.getLabelFont().deriveFont(Font.BOLD));
        Font newFont = dateAxis.getLabelFont().deriveFont(14f);  // Set the font size to 16
        dateAxis.setLabelFont(newFont);
        dateAxis.setDateFormatOverride(new SimpleDateFormat("d MMM"));
        plot.setDomainAxis(dateAxis);

        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setLabelFont(rangeAxis.getLabelFont().deriveFont(Font.BOLD));

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesLinesVisible(0, true);
        plot.setRenderer(renderer);

        chartPanel.setChart(chart);
    }

    private XYDataset buildDatasetForRange() {
        DefaultXYDataset dataset = new DefaultXYDataset();

        int numDays = (int) (endDate.toEpochDay() - startDate.toEpochDay() + 1);
        if (numDays <= 0) {
            return dataset;
        }

        double[] xValues = new double[numDays];
        double[] yValues = new double[numDays];

        for (int i = 0; i < numDays; i++) {
            LocalDate currentDate = startDate.plusDays(i);
            xValues[i] = java.sql.Date.valueOf(currentDate).getTime();
            List<LogEntry> dayEntries = LogService.getEntriesForDate(currentUser.getId(), currentDate.toString());

            if (dayEntries.isEmpty()) {
                yValues[i] = Double.NaN;
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

    private JComboBox<String> createDateComboBox() {
        JComboBox<String> dateBox = new JComboBox<>();
        dateBox.addItem("Please select date for graph generation");

        // Ensure the default prompt is displayed as the selected option
        dateBox.setSelectedIndex(0);
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 30; i++) {
            LocalDate date = today.minusDays(i);
            dateBox.addItem(formatDateWithOrdinal(date));
        }
        return dateBox;
    }

    private String formatDateWithOrdinal(LocalDate date) {
        int day = date.getDayOfMonth();
        String suffix = getDaySuffix(day);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d'" + suffix + "' MMM yyyy");
        return date.format(formatter);
    }

    private String getDaySuffix(int day) {
        if (day >= 11 && day <= 13) return "th";
        switch (day % 10) {
            case 1: return "st";
            case 2: return "nd";
            case 3: return "rd";
            default: return "th";
        }
    }

    private LocalDate parseDate(String dateString) {
        String cleanedDateString = dateString.replaceAll("(\\d+)(st|nd|rd|th)", "$1");
        return LocalDate.parse(cleanedDateString, DateTimeFormatter.ofPattern("d MMM yyyy"));
    }

    private void sendDataToDoctor() {
        final String fromEmail = "sugarbyte.app@gmail.com";
        final String appPassword = "twym wigt ytak botd";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, appPassword);
            }
        });

        try {
            BufferedImage chartImage = chartPanel.getChart().createBufferedImage(chartPanel.getWidth(), chartPanel.getHeight());
            File tempFile = new File("glucose_graph.png");
            ImageIO.write(chartImage, "png", tempFile);

            String doctorEmail = currentUser.getDoctorEmail();
            String doctorName = currentUser.getDoctorName();
            String userName = currentUser.getName();

            // Format the dates with ordinal suffix
            String formattedStartDate = formatDateWithOrdinal(startDate);
            String formattedEndDate = formatDateWithOrdinal(endDate);

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(doctorEmail));
            message.setSubject("Glucose Graph: " + formattedStartDate + " to " + formattedEndDate);

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(String.format(
                    "Dear Dr. %s,\n\nPlease find attached the glucose graph for your patient %s from %s to %s.\n\nBest regards,\nSugarByte",
                    doctorName, userName, formattedStartDate, formattedEndDate));

            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(tempFile);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(attachmentPart);

            message.setContent(multipart);
            Transport.send(message);

            JOptionPane.showMessageDialog(this, "Graph sent to your doctor successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to send the email: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
