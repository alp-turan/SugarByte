package databaseTest;

import database.DatabaseManager;
import database.UserDAO;
import model.User;
import org.junit.jupiter.api.*;

import org.mockito.*;

import java.sql.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Unit tests for the `UserDAO` class, testing database operations like retrieving, creating, and updating user records.
class UserDAOTest {

    private UserDAO userDAO; // DAO instance being tested.

    // Mocked dependencies for simulating database interaction.
    @Mock private Connection mockConnection;
    @Mock private PreparedStatement mockStatement;
    @Mock private ResultSet mockResultSet;

    // Static mock for the `DatabaseManager` class, ensuring consistency across tests.
    private static MockedStatic<DatabaseManager> mockDatabaseManagerStatic;

    /**
     * Sets up the static mock for `DatabaseManager` before all tests.
     */
    @BeforeAll
    static void setUpStaticMock() {
        mockDatabaseManagerStatic = Mockito.mockStatic(DatabaseManager.class); // Mocking the static `DatabaseManager` class.
    }

    /**
     * Closes the static mock after all tests are complete.
     */
    @AfterAll
    static void tearDownStaticMock() {
        mockDatabaseManagerStatic.close(); // Closing the static mock to free resources.
    }

    /**
     * Initializes mocks and sets up the test environment before each test method.
     * Ensures a mock `DatabaseManager` instance returns a mock `Connection`.
     */
    /* reference 22 - inspiration for this was taken from https://stackoverflow.com/questions/12649020/how-to-force-a-sqlexception-in-junit*/
    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this); // Initializes the mock annotations.

        // Creating and configuring a mock `DatabaseManager`.
        DatabaseManager mockDatabaseManager = mock(DatabaseManager.class);
        mockDatabaseManagerStatic.when(DatabaseManager::getInstance).thenReturn(mockDatabaseManager);

        // Configuring the mock `Connection` and `PreparedStatement` for interactions.
        when(mockDatabaseManager.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

        userDAO = new UserDAO(); // Initializing the DAO under test.
    }
    /* end of reference 22*/

    /**
     * Tests retrieving a user by email when the user exists in the database.
     * Ensures the retrieved user matches the expected data.
     */
    @Test
    void testGetUserByEmail_UserExists() throws SQLException {
        String email = "test@example.com"; // Email of the user to retrieve.

        // Configuring the mock `ResultSet` to simulate a user record.
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("name")).thenReturn("Test User");
        when(mockResultSet.getString("email")).thenReturn(email);

        Optional<User> user = userDAO.getUserByEmail(email); // Retrieving the user by email.

        assertTrue(user.isPresent()); // Verifies that the user exists.
        assertEquals(email, user.get().getEmail()); // Confirms the email matches the expected value.
        verify(mockStatement, times(1)).setString(1, email); // Ensures the email parameter was correctly set.
    }

    /**
     * Tests retrieving a user by email when the user does not exist.
     * Ensures the result is empty.
     */
    @Test
    void testGetUserByEmail_UserDoesNotExist() throws SQLException {
        String email = "nonexistent@example.com"; // Email of a non-existent user.

        // Configuring the mock `ResultSet` to simulate no user found.
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        Optional<User> user = userDAO.getUserByEmail(email); // Attempting to retrieve the non-existent user.

        assertFalse(user.isPresent()); // Verifies the result is empty.
        verify(mockStatement, times(1)).setString(1, email); // Confirms the email parameter was set correctly.
    }

    /**
     * Tests creating a user when the user already exists.
     * Ensures no new user is created.
     */
    @Test
    void testCreateUser_UserAlreadyExists() throws SQLException {
        User existingUser = new User(); // Creating a user object to simulate an existing user.
        existingUser.setName("Existing User");
        existingUser.setEmail("test@example.com");

        // Configuring the mock `ResultSet` to simulate the user already existing.
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);

        User createdUser = userDAO.createUser(existingUser); // Attempting to create the user.

        assertNull(createdUser); // Verifies that no user was created.
        verify(mockStatement, never()).executeUpdate(); // Ensures no `INSERT` operation was executed.
    }

    /**
     * Tests updating an existing user in the database.
     * Verifies that the update operation modifies the correct fields.
     */
    @Test
    void testUpdateUser_UserExists() throws SQLException {
        User existingUser = new User(); // Creating a user object to simulate an existing user.
        existingUser.setId(1);
        existingUser.setName("Updated User");
        existingUser.setEmail("updated@example.com");
        existingUser.setPassword("updatedPassword");

        // Configuring the mock `PreparedStatement` to simulate a successful update.
        when(mockStatement.executeUpdate()).thenReturn(1);

        userDAO.updateUser(existingUser); // Updating the user.

        // Verifying that the correct parameters were set on the `PreparedStatement`.
        verify(mockStatement, times(1)).setString(1, existingUser.getName());
        verify(mockStatement, times(1)).setString(5, existingUser.getEmail());
        verify(mockStatement, times(1)).setInt(13, existingUser.getId());
        verify(mockStatement, times(1)).executeUpdate(); // Ensures the update operation was executed.
    }

    /**
     * Tests updating a non-existent user.
     * Ensures the operation does not affect any database records.
     */
    @Test
    void testUpdateUser_UserDoesNotExist() throws SQLException {
        User nonExistentUser = new User(); // Creating a user object to simulate a non-existent user.
        nonExistentUser.setId(999);
        nonExistentUser.setName("Nonexistent User");

        // Configuring the mock `PreparedStatement` to simulate no rows updated.
        when(mockStatement.executeUpdate()).thenReturn(0);

        userDAO.updateUser(nonExistentUser); // Attempting to update the non-existent user.

        // Verifying that the correct ID parameter was set on the `PreparedStatement`.
        verify(mockStatement, times(1)).setInt(13, nonExistentUser.getId());
        verify(mockStatement, times(1)).executeUpdate(); // Ensures the update operation was attempted.
    }
}

