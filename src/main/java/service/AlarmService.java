package service;

import model.LogEntry;
import model.User;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Properties;

/**
 * Provides methods for checking log entries and sending alarms.
 */
public class AlarmService {

    // Blood sugar thresholds
    private static final double MIN_THRESHOLD = 3.9; // Hypoglycemia

    /**
     * Checks the blood sugar value and sends an alarm email if out of range.
     *
     * @param entry The log entry to check.
     * @param user  The user associated with the log entry.
     */
    public static void checkAndSendAlarm(LogEntry entry, User user) {
        // Retrieve the blood sugar value and time of the log entry
        double bloodSugar = entry.getBloodSugar();
        String logTime = entry.getTimeOfDay(); // Time of the log entry
        String logDate = entry.getDate();
        String lastMealTime = entry.getTimeOfDay();

        // Calculate the time difference from the last meal
        // timeSinceLastMeal is defined in minutes
        long timeSinceLastMeal = calculateTimeDifference(logDate, lastMealTime);

        // Determine the maximum threshold based on the time since the last meal
        double maxThreshold = 0.0;
        if (timeSinceLastMeal >= 10 * 60) { // 10+ hours = fasting (chose 10 as the range of hours for 'fasting'
            // found in reputable health journals was 8-12, hence the midpoint of 10 hours was selected)
            maxThreshold = 7.0;
            System.out.println("Fasting detected. Applying threshold of 7.0 mmol/L.");
        } else if (timeSinceLastMeal >= 2 * 60) { // 2–10 hours = post-meal
            maxThreshold = 11.0;
            System.out.println("Post-meal detected. Applying threshold of 11.0 mmol/L.");
        } else {
            System.out.println("Within 2 hours of eating. No alarm needed.");
            return;
        }

        // Check if blood sugar is below minimum threshold
        if (bloodSugar < MIN_THRESHOLD) {
            System.out.println("Blood sugar below 3.9 mmol/L. Triggering alarm.");
            sendEmailAlarm(user.getDoctorEmail(), user.getName(), bloodSugar, logTime);
        } else if (bloodSugar > maxThreshold) {
            System.out.println("Blood sugar exceeds " + maxThreshold + " mmol/L. Triggering alarm.");
            sendEmailAlarm(user.getDoctorEmail(), user.getName(), bloodSugar, logTime);
        }
    }

    /**
     * Helper method to calculate the time difference in minutes.
     *
     * @param logDate       The date and time of the log entry.
     * @param lastMealTime  The time of the last meal.
     * @return The time difference in minutes.
     */
    private static long calculateTimeDifference(String logDate, String lastMealTime) {
        LocalTime logTime = LocalTime.parse(lastMealTime);
        LocalTime currentTime = LocalTime.parse(logDate);
        return Duration.between(logTime, currentTime).toMinutes();
    }

    /**
     * Sends an alarm email to the user's doctor using Gmail's SMTP server.
     *
     * @param doctorEmail The doctor's email address.
     * @param userName    The name of the user.
     * @param bloodSugar  The blood sugar value triggering the alarm.
     * @param timeOfDay   The time of day of the log entry.
     */
    private static void sendEmailAlarm(String doctorEmail, String userName, double bloodSugar, String timeOfDay) {
        // Your Gmail credentials
        final String fromEmail = "sugarbyte.app@gmail.com"; // Replace with your Gmail address
        final String appPassword = "twym wigt ytak botd"; // Replace with your app-specific password

        // SMTP server properties
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Create a mail session with authentication
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, appPassword);
            }
        });

        try {
            // Create the email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(doctorEmail));
            message.setSubject("Urgent: Blood Sugar Alert");

            String emailBody = String.format(
                    "Dear Doctor,\n\nYour patient %s recorded a blood sugar level of %.2f mmol/L at %s. "
                            + "This level is outside the safe range.\n\n"
                            + "Please review and advise.\n\n"
                            + "Best regards,\nSugarByte - The Comprehensive Diabetes Monitoring App",
                    userName, bloodSugar, timeOfDay);

            message.setText(emailBody);

            // Send the email
            Transport.send(message);
            System.out.println("Alarm email sent to " + doctorEmail);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Failed to send email to " + doctorEmail);
        }
    }
    /**
     * Temporary main method for testing AlarmService with dummy User and Log entries.
     * For cleanliness, can be deleted. However, doesn't affect flow of the code when running the global Main script
     */
    public static void main(String[] args) {
        System.out.println("Running AlarmService main method"); // to confirm execution
        // Create a dummy user
        User user = new User();
        user.setDoctorEmail("xvickywalkerx@gmail.com");
        user.setName("John Doe");

        // Create a dummy log entry
        LogEntry logEntry = new LogEntry();
        logEntry.setBloodSugar(12.5); // Example out-of-range value
        logEntry.setTimeOfDay("10:00");
        logEntry.setDate("14:00"); // Dummy same time for simplicity

        // Test the AlarmService
        checkAndSendAlarm(logEntry, user);
    }
}


