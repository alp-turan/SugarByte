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

class UserDAOTest {

    private UserDAO userDAO;

    @Mock private Connection mockConnection;
    @Mock private PreparedStatement mockStatement;
    @Mock private ResultSet mockResultSet;

    private static MockedStatic<DatabaseManager> mockDatabaseManagerStatic;

    @BeforeAll
    static void setUpStaticMock() {
        mockDatabaseManagerStatic = Mockito.mockStatic(DatabaseManager.class);
    }

    @AfterAll
    static void tearDownStaticMock() {
        mockDatabaseManagerStatic.close();
    }

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);

        DatabaseManager mockDatabaseManager = mock(DatabaseManager.class);
        mockDatabaseManagerStatic.when(DatabaseManager::getInstance).thenReturn(mockDatabaseManager);

        when(mockDatabaseManager.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

        userDAO = new UserDAO();
    }

    @Test
    void testGetUserByEmail_UserExists() throws SQLException {
        String email = "test@example.com";

        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("name")).thenReturn("Test User");
        when(mockResultSet.getString("email")).thenReturn(email);

        Optional<User> user = userDAO.getUserByEmail(email);

        assertTrue(user.isPresent());
        assertEquals(email, user.get().getEmail());
        verify(mockStatement, times(1)).setString(1, email);
    }

    @Test
    void testGetUserByEmail_UserDoesNotExist() throws SQLException {
        String email = "nonexistent@example.com";

        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        Optional<User> user = userDAO.getUserByEmail(email);

        assertFalse(user.isPresent());
        verify(mockStatement, times(1)).setString(1, email);
    }

    @Test
    void testCreateUser_UserAlreadyExists() throws SQLException {
        User existingUser = new User();
        existingUser.setName("Existing User");
        existingUser.setEmail("test@example.com");

        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);

        User createdUser = userDAO.createUser(existingUser);

        assertNull(createdUser);
        verify(mockStatement, never()).executeUpdate();
    }

    @Test
    void testUpdateUser_UserExists() throws SQLException {
        User existingUser = new User();
        existingUser.setId(1);
        existingUser.setName("Updated User");
        existingUser.setEmail("updated@example.com");
        existingUser.setPassword("updatedPassword");

        when(mockStatement.executeUpdate()).thenReturn(1);

        userDAO.updateUser(existingUser);

        verify(mockStatement, times(1)).setString(1, existingUser.getName());
        verify(mockStatement, times(1)).setString(5, existingUser.getEmail());
        verify(mockStatement, times(1)).setInt(13, existingUser.getId());
        verify(mockStatement, times(1)).executeUpdate();
    }

    @Test
    void testUpdateUser_UserDoesNotExist() throws SQLException {
        User nonExistentUser = new User();
        nonExistentUser.setId(999);
        nonExistentUser.setName("Nonexistent User");

        when(mockStatement.executeUpdate()).thenReturn(0);

        userDAO.updateUser(nonExistentUser);

        verify(mockStatement, times(1)).setInt(13, nonExistentUser.getId());
        verify(mockStatement, times(1)).executeUpdate();
    }
}