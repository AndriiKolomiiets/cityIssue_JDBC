import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;

import java.sql.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CityIssueJDBCTest {
    CityIssueJDBC cityIssueJDBC;
    private static final String URL = "jdbc:mysql://localhost/city_issue_db?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private static final String LOGIN = "root";
    private static final String PASSWORD = "root784512";
    Connection testConnection;
    Statement statement;
    ResultSet resultSet;

    @Rule
    public TestRule timeout = new Timeout(3000);

    @Before
    public void setUp() {
        cityIssueJDBC = CityIssueJDBC.getInstance();
        cityIssueJDBC.connectToDB();
    }

    @Test
    public void getInstance() {
        assertNotNull(cityIssueJDBC);
    }

    @Test
    public void connectToDB() {
        assertNotNull(cityIssueJDBC.connection);
    }

    @Test
    public void mockConnectToDB() throws SQLException {
        final DriverManager mockDriverManager = mock(DriverManager.class);
        mockDriverManager.getConnection(URL, LOGIN, PASSWORD);
        verify(mockDriverManager, times(1)).getConnection(URL, LOGIN, PASSWORD);
    }

    @Test
    public void buildUserInfoIntoDB_Mock() throws SQLException {
        final CityIssueJDBC mockCityIssueJDBC = mock(CityIssueJDBC.class);
        final Connection mockConnection = mock(Connection.class);
        final String sqlQuery = "INSERT INTO users(first_name, last_name, phone, email) VALUES ('firstName', 'lastName', '+38test#', '@email')";
        final PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        when(mockConnection.prepareStatement(sqlQuery)).thenReturn(mockPreparedStatement);
        mockConnection.prepareStatement(sqlQuery);
        mockPreparedStatement.executeBatch();

        mockCityIssueJDBC.buildUserInfoIntoDB("firstName", "lastName", "+38test#", "@email");

        verify(mockConnection, times(1)).prepareStatement(sqlQuery);
        verify(mockPreparedStatement, times(1)).executeBatch();
        verify(mockCityIssueJDBC, times(1)).buildUserInfoIntoDB("firstName", "lastName", "+38test#", "@email");
    }

    @Test
    public void buildUserInfoIntoDB() {
        createTestConnection();
        cityIssueJDBC.buildUserInfoIntoDB("firstName", "lastName", "+38test#", "@email");
        try {
            resultSet = statement.executeQuery("SELECT first_name, last_name, phone, email FROM users WHERE email='@email'");
        } catch (SQLException e) {
//            e.printStackTrace();
            System.out.println("Can't execute an update...");
        }
        String phone = null;
        try {
            while (resultSet.next()) {
                phone = resultSet.getString("phone");
            }
        } catch (SQLException e) {
//            e.printStackTrace();
            System.out.println("Can't get phone by email...");
        }
        closeTestConnection();
        assertEquals("+38test#", phone);
    }

    private void createTestConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            testConnection = DriverManager.getConnection(URL, LOGIN, PASSWORD);
            statement = testConnection.createStatement();
        } catch (
                Exception ex) {
            System.out.println("Seems like there is a problem with test connection...");
        }
    }

    private void closeTestConnection() {
        try {
            statement.executeUpdate("DELETE FROM users WHERE email='@email'");

            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (testConnection != null) {
                testConnection.close();
            }
        } catch (SQLException e) {
            /*NOP*/
        }
    }

    @After
    public void tearDown() {
        try {
            Connection connection = cityIssueJDBC.connection;
            connection.close();
        } catch (SQLException e) {
            /*NOP*/
        }
    }

}