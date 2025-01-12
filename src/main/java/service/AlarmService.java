package service;

import model.LogEntry;
import model.User;

import javax.mail.*;
import javax.swing.JOptionPane;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.HashSet;
import java.util.Set;
import java.util.Properties;

/**
 * This entire class provides the methods for checking and sending the email alarm to the doctor based on glucose readings.
 */
public class AlarmService {

    // Blood sugar thresholds
    private static final double MIN_THRESHOLD = 3.9; // Hypoglycemia
    private static final double MAX_THRESHOLD_POST_MEAL = 11.0; //Hyperglycemia 2 hours after a meal
    private static final double MAX_THRESHOLD_FASTING = 7.0; // Hyperglycemia when fasting

    private static final Set<String> notifiedMeals = new HashSet<>(); // Tracks the meals that have been notified for each user

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
        String mealTime = entry.getTimeOfDay(); // assuming `mealTime` is a String like "Breakfast", "Lunch", etc.

        // Check if this meal has already triggered an alarm for this user
        if (notifiedMeals.contains(user.getName() + "_" + mealTime)) {
            System.out.println("Skipping alarm for " + mealTime + " as it has already been notified.");
            return; // Skip sending alarm for this meal type if already notified
        }

        // Determine the maximum threshold based on hours since last meal
        double maxThreshold = getMaxThreshold(hoursSinceMeal);

        // Check if blood sugar is below minimum threshold or above the maximum threshold
        if (bloodSugar < MIN_THRESHOLD || bloodSugar > maxThreshold) {
            System.out.println(user.getName() + ": Blood sugar out of range for " + mealTime + ". Triggering alarm.");

            // Send email notification for this meal time if not notified already
            sendEmailAlarm(user.getDoctorName(), user.getDoctorEmail(), user.getName(), bloodSugar, hoursSinceMeal);

            // Mark this meal as notified for the user
            notifiedMeals.add(user.getName() + "_" + mealTime); // Save the notified meal (e.g., userName_breakfast)
        }
    }

    /**
     * Determines the maximum threshold based on the hours since the last meal.
     *
     * @param hoursSinceMeal The number of hours since the last meal.
     * @return The appropriate threshold based on meal timing.
     */
    private static double getMaxThreshold(int hoursSinceMeal) {
        if (hoursSinceMeal >= 10) { // 10+ hours = fasting
            return MAX_THRESHOLD_FASTING;
        } else if (hoursSinceMeal >= 2) { // 2–10 hours = post-meal
            return MAX_THRESHOLD_POST_MEAL;
        } else {
            return Double.MAX_VALUE; // No threshold, as it's within 2 hours of eating
        }
    }

    /**
     * Sends an alarm email to the user's doctor using the app's email, sugarbyte.app@gmail.com, via Gmail's SMTP server.
     *
     * @param doctorName   The doctor's name.
     * @param doctorEmail  The doctor's email address.
     * @param userName     The name of the user.
     * @param bloodSugar   The blood sugar value triggering the alarm.
     * @param hoursSinceMeal The number of hours since the user's last meal.
     */
    private static void sendEmailAlarm(String doctorName, String doctorEmail, String userName, double bloodSugar, int hoursSinceMeal) {
        // SugarByte's Gmail credentials:
        final String fromEmail = "sugarbyte.app@gmail.com"; // SugarByte's email address
        final String appPassword = "twym wigt ytak botd"; // SugarByte's app password for IntelliJ (new one may need to be generated if different code manager is used)

        // SMTP server properties
        Properties props = new Properties(); // new object of type Properties created to add key-value pairs
        props.put("mail.smtp.host", "smtp.gmail.com"); //assigning the email server as 'gmail' (since the app's email address is @gmail.com)
        props.put("mail.smtp.port", "587");  // 587 is the port number commonly used for email submissions with STARTTLS encryption
        props.put("mail.smtp.auth", "true"); // introduces authentication which improves the security of the system
        props.put("mail.smtp.starttls.enable", "true"); // enables encryption of the emails (safer)
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com"); // Disable SSL certificate validation TEMPORARY FIX

        // Create a mail session with authentication
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, appPassword); // defined above as the fields for sendEmailAlarm
            }
        });

        try {
            // Create the email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail)); // sets the sending address as the app's email
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(doctorEmail)); // sets the recipient by parsing the inputted user's doctor's email (string) into an email address
            message.setSubject("Urgent: Blood Sugar Alert for Patient " + userName);

            String emailBody = String.format( // using .format facilitates formatting the desired text with the values log entry values
                    "Dear Doctor %s,\n\nYour patient %s recorded a blood sugar level of %.2f mmol/L, which is %s the safe range.\n"
                            + "This level was recorded %d hours after their last meal.\n\n"
                            + "Please review and advise.\n\n"
                            + "Best regards,\nSugarByte - The Comprehensive Diabetes Monitoring App",
                    doctorName, userName, bloodSugar,
                    (bloodSugar < MIN_THRESHOLD ? "below" : "above"),
                    hoursSinceMeal); // each of these arguments are passed sequentially into the indicated places (eg %s corresponds to the firsr arg, doctorName)

            message.setText(emailBody);

            // Sends the email
            Transport.send(message);
            System.out.println("Alarm email sent to " + userName + "'s doctor's email " + doctorEmail);

            // Shows a pop-up notification on the app to confirm the sending of the alarm (centred in the centre as there is no parent component assigned)
            String notificationMessage = String.format(
                    "Doctor has been notified of %s glucose value (%.2f mmol/L).",
                    (bloodSugar < MIN_THRESHOLD ? "low" : "high"), bloodSugar);
            JOptionPane.showMessageDialog(null, notificationMessage, "Notification", JOptionPane.INFORMATION_MESSAGE);

        } catch (MessagingException e) { // Runs if the 'try' block fails. MessagingException is specific to JavaMail operations.
            e.printStackTrace(); // The stack trace provides detailed info about the exception
            System.err.println("Failed to send email to " + userName + "'s doctor's email " + doctorEmail);

            // Shows a pop-up notification on the app to indicate an error occurred with sending the email.
            JOptionPane.showMessageDialog(null, "Failed to notify the doctor. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
