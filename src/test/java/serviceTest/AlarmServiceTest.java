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
        logEntry = mock(LogEntry.class);
        user = mock(User.class);
    }

    @Test
    void testCheckAndSendAlarm_AlarmAlreadySentForMeal() {
        // Arrange
        when(logEntry.getBloodSugar()).thenReturn(3.5);
        when(logEntry.getHoursSinceMeal()).thenReturn(2);
        when(logEntry.getTimeOfDay()).thenReturn("Breakfast");

        when(user.getName()).thenReturn("John Doe");
        when(user.getDoctorName()).thenReturn("Dr. Smith");
        when(user.getDoctorEmail()).thenReturn("dr.smith@example.com");

        AlarmService.checkAndSendAlarm(logEntry, user);

        assertTrue(AlarmService.getNotifiedMeals().contains("John Doe_Breakfast"));

        AlarmService.checkAndSendAlarm(logEntry, user);

        assertTrue(AlarmService.getNotifiedMeals().contains("John Doe_Breakfast"));
        assertEquals(1, AlarmService.getNotifiedMeals().size(), "Alarm should only be sent once for Breakfast");
    }

    @Test
    void testCheckAndSendAlarm_AlarmTriggeredForOutOfRangeBloodSugar() {
        when(logEntry.getBloodSugar()).thenReturn(12.0);
        when(logEntry.getHoursSinceMeal()).thenReturn(3);
        when(logEntry.getTimeOfDay()).thenReturn("Lunch");

        when(user.getName()).thenReturn("John Doe");
        when(user.getDoctorName()).thenReturn("Dr. Smith");
        when(user.getDoctorEmail()).thenReturn("dr.smith@example.com");

        AlarmService.checkAndSendAlarm(logEntry, user);

        assertTrue(AlarmService.getNotifiedMeals().contains("John Doe_Lunch"));
    }

    @Test
    void testCheckAndSendAlarm_AlarmNotTriggeredForNormalBloodSugar() {
        when(logEntry.getBloodSugar()).thenReturn(5.0);
        when(logEntry.getHoursSinceMeal()).thenReturn(3);
        when(logEntry.getTimeOfDay()).thenReturn("Dinner");

        when(user.getName()).thenReturn("John Doe");
        when(user.getDoctorName()).thenReturn("Dr. Smith");
        when(user.getDoctorEmail()).thenReturn("dr.smith@example.com");

        AlarmService.checkAndSendAlarm(logEntry, user);

        assertFalse(AlarmService.getNotifiedMeals().contains("John Doe_Dinner"));
    }


    @Test
    void testGetMaxThreshold() {
        // Test the thresholds using getter methods
        assertEquals(7.0, AlarmService.getMaxThresholdFasting());
        assertEquals(11.0, AlarmService.getMaxThresholdPostMeal());
        assertEquals(3.9, AlarmService.getMinThreshold());
    }


    @Test
    void testGetNotifiedMeals() {
        // Add a notified meal and verify it's in the set
        AlarmService.getNotifiedMeals().add("John Doe_Breakfast");
        assertTrue(AlarmService.getNotifiedMeals().contains("John Doe_Breakfast"));
    }

    @Test
    void testEdgeCaseForMissingUserInfo() {
        // Test with a user that has missing information
        when(logEntry.getBloodSugar()).thenReturn(5.0);
        when(logEntry.getHoursSinceMeal()).thenReturn(3);
        when(logEntry.getTimeOfDay()).thenReturn("Dinner");

        when(user.getName()).thenReturn(null);
        when(user.getDoctorName()).thenReturn(null);
        when(user.getDoctorEmail()).thenReturn(null);

        AlarmService.checkAndSendAlarm(logEntry, user);

        assertFalse(AlarmService.getNotifiedMeals().contains("null_Dinner"));
    }

}
