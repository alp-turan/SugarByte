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

        JPanel datePickersPanel = new JPanel();
        datePickersPanel.setOpaque(false);
        datePickersPanel.setLayout(new BoxLayout(datePickersPanel, BoxLayout.Y_AXIS));
        datePickersPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel startRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        startRow.setOpaque(false);
        JLabel startDateLabel = new JLabel("Start Date:");
        startDateLabel.setFont(startDateLabel.getFont().deriveFont(Font.BOLD));
        JComboBox<String> startDateBox = createDateComboBox();
        startRow.add(startDateLabel);
        startRow.add(startDateBox);

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

        endRow.add(endDateLabel);
        endRow.add(Box.createHorizontalStrut(10));
        endRow.add(endDateBox);
        endRow.add(Box.createHorizontalGlue());
        endRow.add(generateButton);

        datePickersPanel.add(startRow);
        datePickersPanel.add(endRow);

        topPanel.add(datePickersPanel);
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

        JPanel doctorButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        doctorButtonPanel.setOpaque(false);
        doctorButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JButton sendToDoctorButton = new JButton("Send to Doctor");
        sendToDoctorButton.addActionListener(e -> sendDataToDoctor());
        doctorButtonPanel.add(sendToDoctorButton);

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

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(doctorEmail));
            message.setSubject("Glucose Graph: " + startDate + " to " + endDate);

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(String.format(
                    "Dear Dr. %s,\n\nPlease find attached the glucose graph for your patient %s from %s to %s.\n\nBest regards,\nSugarByte",
                    doctorName, userName, startDate, endDate));

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
