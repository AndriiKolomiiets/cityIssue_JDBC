import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;

import static org.junit.Assert.*;

public class CityIssueJDBCTest {
    CityIssueJDBC cityIssueJDBC;
    private static final String URL = "jdbc:mysql://localhost/city_issue_db?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private static final String LOGIN = "root";
    private static final String PASSWORD = "root784512";
    Connection testConnection;
    Statement statement;
    ResultSet resultSet;

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