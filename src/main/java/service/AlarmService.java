package service;

import model.LogEntry;
import model.User;

public class AlarmService {

    private static final double MIN_SUGAR = 70.0;
    private static final double MAX_SUGAR = 250.0;

    /**
     * If the blood sugar is out of [70, 250], "send" an alarm to the doctor.
     * In reality, you'd integrate an email or SMS service.
     */
    public static void checkAndSendAlarm(LogEntry entry, User user) {
        double sugar = entry.getBloodSugar();
        if (sugar < MIN_SUGAR || sugar > MAX_SUGAR) {
            // "Sending" alarm
            System.out.println("ALARM! Blood sugar (" + sugar + ") for user '" + user.getName() +
                    "' is out of control. Notify doctor at " + user.getDoctorEmail());
            // Example: possibly call an EmailService here
        }
    }
}
