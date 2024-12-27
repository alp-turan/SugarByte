package ui;
import model.User;

import javax.swing.*;

public class Calendar extends BaseUI {

    public Calendar(User user) {
        super("Calendar"); // Call BaseUI constructor with the title
        refreshCalendar();
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // For testing purposes, create a dummy User
            User dummyUser = new User(); // Ensure User has setters
            dummyUser.setName("Mark");
            new Calendar(dummyUser);
        });
    }
}
