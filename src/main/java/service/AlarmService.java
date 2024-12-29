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
        //commented this all out for now as it doesnt work with current log entry times
       // String logTime = entry.getTimeOfDay(); // Time of the log entry - but gives string breakfast/lunch/dinner
        //String logDate = entry.getDate(); // commented this out for now as we dont have acc time
        //String lastMealTime = entry.getTimeOfDay();
        LocalTime currentTime = LocalTime.now();
        String lastMealTimeString = "07:00"; //hard-coded for now (must be in "HH:mm" format)
        LocalTime lastMealTime;
        try {
            lastMealTime = LocalTime.parse(lastMealTimeString); // Parse to LocalTime, so 15:00 gets transformed into an object of type time
        } catch (Exception e) {
            System.err.println("Error parsing lastMealTime: " + e.getMessage());
            return; // Exit if parsing fails
        }
        // Calculate the time difference from the last meal
        // timeSinceLastMeal is defined in minutes
        long timeSinceLastMeal = calculateTimeDifference(currentTime, lastMealTime);

        //Determine the maximum threshold based on the time since the last meal
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
            System.out.println(user.getName() + ":" + " Blood sugar below 3.9 mmol/L. Triggering alarm.");
            sendEmailAlarm(user.getDoctorName(),user.getDoctorEmail(), user.getName(), bloodSugar, lastMealTime);
        } else if (bloodSugar > maxThreshold) {
            System.out.println(user.getName() + ":" + " Blood sugar exceeds " + maxThreshold + " mmol/L. Triggering alarm.");
            sendEmailAlarm(user.getDoctorName(),user.getDoctorEmail(), user.getName(), bloodSugar, lastMealTime);
        }
    }

    /**
     * Helper method to calculate the time difference in minutes.
     *
     * @param currentTime       The date of the log entry.
     * @param lastMealTime  Time of last meal (currently stored as a string: eg "breakfast pre", which throws parsing error)
     * @return The time difference in minutes.
     */
    //commented this out for now as I'd have to correct the logDate & lastMealTime times
    private static long calculateTimeDifference(LocalTime currentTime, LocalTime lastMealTime) {
        return Duration.between(lastMealTime, currentTime).toMinutes();
    }

    /**
     * Sends an alarm email to the user's doctor using Gmail's SMTP server.
     * @param doctorEmail The doctor's email address.
     * @param doctorName The doctor's name.
     * @param userName    The name of the user.
     * @param bloodSugar  The blood sugar value triggering the alarm.
     * @param lastMealTime   The time of day of the log entry.
     */
    private static void sendEmailAlarm(String doctorName, String doctorEmail, String userName, double bloodSugar, LocalTime lastMealTime) {
        // SugarByte's Gmail credentials:
        final String fromEmail = "sugarbyte.app@gmail.com"; // SugarByte's email address
        final String appPassword = "twym wigt ytak botd"; // SugarByte's app password for IntelliJ (new one may need to generated if different code manager is used)

        // SMTP server properties
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // enables encryption of the emails (safer)

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
            message.setSubject("Urgent: Blood Sugar Alert of your patient " + userName);

            String emailBody = String.format( // by using .format, we can avoid concatenating alternating strings of
                    // text & values, eg 'userName', can instead be passed at the end of the string & have placeholders
                    // inside the message to be replaced with the corresponding values
                    "Dear Doctor %s,\n\nYour patient %s recorded a blood sugar level of %.2f mmol/L at %s. "
                            + "This level is outside the safe range.\n\n"
                            + "Please review and advise.\n\n"
                            + "Best regards,\nSugarByte - The Comprehensive Diabetes Monitoring App",
                    doctorName, userName, bloodSugar, lastMealTime);

            message.setText(emailBody);

            // Send the email
            Transport.send(message);
            System.out.println("Alarm email sent to " + userName + "'s doctor's email " + doctorEmail);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Failed to send email to " + userName + "'s doctor's email " + doctorEmail);
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
        logEntry.setBloodSugar(12); // Example out-of-range value
        //logEntry.setTimeOfDay("10:00");
        //logEntry.setDate("14:00"); // Dummy same time for simplicity

        // Test the AlarmService
        checkAndSendAlarm(logEntry, user);
    }
}


