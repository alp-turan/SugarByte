import ui.OpeningWindow;

import javax.swing.SwingUtilities;

/**
 * The main entry point of the application.
 * Simply launches the OpeningWindow, which then leads to Login.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new OpeningWindow();
        });
    }
}
