package service;

import model.LogEntry;
import model.User;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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
        // Retrieve hours since last meal (this should now be part of the LogEntry)
        int hoursSinceMeal = entry.getHoursSinceMeal();

        // Determine the maximum threshold based on hours since last meal
        double maxThreshold = 0.0;
        if (hoursSinceMeal >= 10) { // 10+ hours = fasting
            maxThreshold = 7.0;
            System.out.println("Fasting detected. Applying threshold of 7.0 mmol/L.");
        } else if (hoursSinceMeal >= 2) { // 2–10 hours = post-meal
            maxThreshold = 11.0;
            System.out.println("Post-meal detected. Applying threshold of 11.0 mmol/L.");
        } else {
            System.out.println("Within 2 hours of eating. No alarm needed.");
            return;
        }

        // Check if blood sugar is below minimum threshold
        if (bloodSugar < MIN_THRESHOLD) {
            System.out.println(user.getName() + ":" + " Blood sugar below 3.9 mmol/L. Triggering alarm.");
            sendEmailAlarm(user.getDoctorName(), user.getDoctorEmail(), user.getName(), bloodSugar, hoursSinceMeal);
        } else if (bloodSugar > maxThreshold) {
            System.out.println(user.getName() + ":" + " Blood sugar exceeds " + maxThreshold + " mmol/L. Triggering alarm.");
            sendEmailAlarm(user.getDoctorName(), user.getDoctorEmail(), user.getName(), bloodSugar, hoursSinceMeal);
        }
    }

    /**
     * Sends an alarm email to the user's doctor using Gmail's SMTP server.
     *
     * @param doctorEmail The doctor's email address.
     * @param doctorName  The doctor's name.
     * @param userName    The name of the user.
     * @param bloodSugar  The blood sugar value triggering the alarm.
     * @param hoursSinceMeal The number of hours since the user's last meal.
     */
    private static void sendEmailAlarm(String doctorName, String doctorEmail, String userName, double bloodSugar, int hoursSinceMeal) {
        // SugarByte's Gmail credentials:
        final String fromEmail = "sugarbyte.app@gmail.com"; // SugarByte's email address
        final String appPassword = "twym wigt ytak botd"; // SugarByte's app password for IntelliJ (new one may need to be generated if different code manager is used)

        // SMTP server properties
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // enables encryption of the emails (safer)
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com"); // Disable SSL certificate validation   TEMPORARY FIX

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
                    "Dear Doctor %s,\n\nYour patient %s recorded a blood sugar level of %.2f mmol/L, which is %s the safe range.\n"
                            + "This level was recorded %d hours after their last meal at %s.\n\n"
                            + "Please review and advise.\n\n"
                            + "Best regards,\nSugarByte - The Comprehensive Diabetes Monitoring App",
                    doctorName, userName, bloodSugar, (bloodSugar < MIN_THRESHOLD || bloodSugar > (hoursSinceMeal >= 10 ? 7.0 : 11.0)) ? "outside" : "within", hoursSinceMeal, "the time of day or specific log time here");

            message.setText(emailBody);

            // Send the email
            Transport.send(message);
            System.out.println("Alarm email sent to " + userName + "'s doctor's email " + doctorEmail);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Failed to send email to " + userName + "'s doctor's email " + doctorEmail);
        }
    }


}
