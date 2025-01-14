package serviceTest;

import service.AlarmService;
import model.LogEntry;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class AlarmServiceTest {

    // Class-level test setup for testing AlarmService behavior
    private LogEntry logEntry; // Mocked instance of LogEntry, representing an individual log entry
    private User user;         // Mocked instance of User, representing a user interacting with the service

    /**
     * Sets up the mock objects before each test case is executed.
     * Uses JUnit's @BeforeEach to ensure clean state for each test.
     */
    @BeforeEach
    void setUp() {
        logEntry = mock(LogEntry.class); // Mockito's mock() creates a dummy LogEntry object
        user = mock(User.class);        // Another mock object for User, isolating the test scope
    }

    /**
     * Verifies that an alarm is not sent more than once for the same meal under the same conditions.
     */
    @Test
    void testCheckAndSendAlarm_AlarmAlreadySentForMeal() {
        // Arranging test conditions using mock behaviors
        when(logEntry.getBloodSugar()).thenReturn(3.5); // Configures the mock to return 3.5 for getBloodSugar()
        when(logEntry.getHoursSinceMeal()).thenReturn(2); // Hours since meal mocked to 2
        when(logEntry.getTimeOfDay()).thenReturn("Breakfast"); // Returns "Breakfast" as the meal time

        when(user.getName()).thenReturn("John Doe"); // User's name mocked as "John Doe"
        when(user.getDoctorName()).thenReturn("Dr. Smith"); // Doctor's name mocked
        when(user.getDoctorEmail()).thenReturn("dr.smith@example.com"); // Mocked doctor email

        // First call to checkAndSendAlarm
        AlarmService.checkAndSendAlarm(logEntry, user);

        // Assert that the alarm was sent and meal is tracked in notifiedMeals
        assertTrue(AlarmService.getNotifiedMeals().contains("John Doe_Breakfast"));

        // Re-invoking to test idempotency
        AlarmService.checkAndSendAlarm(logEntry, user);

        // Verifying notifiedMeals is unchanged after a second call
        assertTrue(AlarmService.getNotifiedMeals().contains("John Doe_Breakfast"));
        assertEquals(1, AlarmService.getNotifiedMeals().size(), "Alarm should only be sent once for Breakfast");
    }

    /**
     * Tests whether an alarm is triggered for blood sugar levels exceeding the maximum post-meal threshold.
     */
    @Test
    void testCheckAndSendAlarm_AlarmTriggeredForOutOfRangeBloodSugar() {
        // Simulating high blood sugar levels
        when(logEntry.getBloodSugar()).thenReturn(12.0); // Blood sugar level set to 12.0
        when(logEntry.getHoursSinceMeal()).thenReturn(3); // 3 hours since last meal
        when(logEntry.getTimeOfDay()).thenReturn("Lunch"); // Meal set to "Lunch"

        when(user.getName()).thenReturn("John Doe");
        when(user.getDoctorName()).thenReturn("Dr. Smith");
        when(user.getDoctorEmail()).thenReturn("dr.smith@example.com");

        // Executing the service logic
        AlarmService.checkAndSendAlarm(logEntry, user);

        // Validating that "John Doe_Lunch" is logged
        assertTrue(AlarmService.getNotifiedMeals().contains("John Doe_Lunch"));
    }

    /**
     * Confirms that no alarm is triggered for normal blood sugar levels.
     */
    @Test
    void testCheckAndSendAlarm_AlarmNotTriggeredForNormalBloodSugar() {
        // Normal blood sugar and meal context
        when(logEntry.getBloodSugar()).thenReturn(5.0); // Within normal range
        when(logEntry.getHoursSinceMeal()).thenReturn(3);
        when(logEntry.getTimeOfDay()).thenReturn("Dinner");

        when(user.getName()).thenReturn("John Doe");
        when(user.getDoctorName()).thenReturn("Dr. Smith");
        when(user.getDoctorEmail()).thenReturn("dr.smith@example.com");

        // Running the service logic
        AlarmService.checkAndSendAlarm(logEntry, user);

        // Verifying no alarm was triggered
        assertFalse(AlarmService.getNotifiedMeals().contains("John Doe_Dinner"));
    }

    /**
     * Validates threshold values for fasting, post-meal, and minimum levels.
     */
    @Test
    void testGetMaxThreshold() {
        // Ensuring the thresholds match the expected constants
        assertEquals(7.0, AlarmService.getMaxThresholdFasting()); // Fasting threshold
        assertEquals(11.0, AlarmService.getMaxThresholdPostMeal()); // Post-meal threshold
        assertEquals(3.9, AlarmService.getMinThreshold()); // Minimum acceptable threshold
    }

    /**
     * Verifies that meals are tracked correctly in the notifiedMeals set.
     */
    @Test
    void testGetNotifiedMeals() {
        // Adding a mocked meal
        AlarmService.getNotifiedMeals().add("John Doe_Breakfast");

        // Asserting presence of the meal in the set
        assertTrue(AlarmService.getNotifiedMeals().contains("John Doe_Breakfast"));
    }

    /**
     * Handles edge cases where the user object contains null values.
     */
    @Test
    void testEdgeCaseForMissingUserInfo() {
        // Simulating user information with null values
        when(logEntry.getBloodSugar()).thenReturn(5.0);
        when(logEntry.getHoursSinceMeal()).thenReturn(3);
        when(logEntry.getTimeOfDay()).thenReturn("Dinner");

        when(user.getName()).thenReturn(null); // User's name not set
        when(user.getDoctorName()).thenReturn(null); // Doctor's name not set
        when(user.getDoctorEmail()).thenReturn(null); // Doctor's email not set

        // Executing the service logic
        AlarmService.checkAndSendAlarm(logEntry, user);

        // Ensuring no alarm is triggered for null user info
        assertFalse(AlarmService.getNotifiedMeals().contains("null_Dinner"));
    }

}