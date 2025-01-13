package serviceTest;

import service.AlarmService;
import model.LogEntry;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class AlarmServiceTest {

    private LogEntry logEntry;
    private User user;

    @BeforeEach
    void setUp() {
        // Initialize mock objects for log entry and user
        logEntry = mock(LogEntry.class);
        user = mock(User.class);
    }

    @Test
    void testCheckAndSendAlarm_AlarmAlreadySentForMeal() {
        // Arrange
        when(logEntry.getBloodSugar()).thenReturn(3.5);  // Blood sugar value
        when(logEntry.getHoursSinceMeal()).thenReturn(2);  // Hours since meal
        when(logEntry.getTimeOfDay()).thenReturn("Breakfast");  // Meal time

        when(user.getName()).thenReturn("John Doe");  // User name
        when(user.getDoctorName()).thenReturn("Dr. Smith");  // Doctor's name
        when(user.getDoctorEmail()).thenReturn("dr.smith@example.com");  // Doctor's email

        // Call the checkAndSendAlarm method for the first time
        AlarmService.checkAndSendAlarm(logEntry, user);

        // Assert: Ensure that the alarm was triggered for "Breakfast"
        assertTrue(AlarmService.getNotifiedMeals().contains("John Doe_Breakfast"));

        // Act: Try to call the method again with the same meal type
        AlarmService.checkAndSendAlarm(logEntry, user);

        // Assert: Ensure that the alarm is NOT triggered a second time for "Breakfast"
        assertTrue(AlarmService.getNotifiedMeals().contains("John Doe_Breakfast"));
        assertEquals(1, AlarmService.getNotifiedMeals().size(), "Alarm should only be sent once for Breakfast");
    }

    @Test
    void testCheckAndSendAlarm_AlarmTriggeredForOutOfRangeBloodSugar() {
        // Arrange
        when(logEntry.getBloodSugar()).thenReturn(12.0);  // Blood sugar value (out of range)
        when(logEntry.getHoursSinceMeal()).thenReturn(3);  // Hours since meal
        when(logEntry.getTimeOfDay()).thenReturn("Lunch");  // Meal time

        when(user.getName()).thenReturn("John Doe");  // User name
        when(user.getDoctorName()).thenReturn("Dr. Smith");  // Doctor's name
        when(user.getDoctorEmail()).thenReturn("dr.smith@example.com");  // Doctor's email

        // Call the checkAndSendAlarm method
        AlarmService.checkAndSendAlarm(logEntry, user);

        // Assert: Ensure that the alarm was triggered for "Lunch"
        assertTrue(AlarmService.getNotifiedMeals().contains("John Doe_Lunch"));
    }

    @Test
    void testCheckAndSendAlarm_AlarmNotTriggeredForNormalBloodSugar() {
        // Arrange
        when(logEntry.getBloodSugar()).thenReturn(5.0);  // Blood sugar value (within range)
        when(logEntry.getHoursSinceMeal()).thenReturn(3);  // Hours since meal
        when(logEntry.getTimeOfDay()).thenReturn("Dinner");  // Meal time

        when(user.getName()).thenReturn("John Doe");  // User name
        when(user.getDoctorName()).thenReturn("Dr. Smith");  // Doctor's name
        when(user.getDoctorEmail()).thenReturn("dr.smith@example.com");  // Doctor's email

        // Call the checkAndSendAlarm method
        AlarmService.checkAndSendAlarm(logEntry, user);

        // Assert: Ensure that no alarm was triggered for "Dinner"
        assertFalse(AlarmService.getNotifiedMeals().contains("John Doe_Dinner"));
    }
}
