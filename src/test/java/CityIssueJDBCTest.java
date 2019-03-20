import com.sun.org.apache.bcel.internal.generic.NOP;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.*;

import static org.junit.Assert.*;

public class CityIssueJDBCTest {
    CityIssueJDBC cityIssueJDBC;
    private String url = "jdbc:mysql://localhost/city_issue_db?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private String login = "root";
    private String password = "root784512";
    Connection testConnection;
    Statement statement;
    ResultSet resultSet;

    @Test
    public void getInstance() {
        cityIssueJDBC = CityIssueJDBC.getInstance();
        assertNotNull(cityIssueJDBC);
    }

    @Test
    public void connectToDB() {
        cityIssueJDBC = CityIssueJDBC.getInstance();
        cityIssueJDBC.connectToDB();
        assertNotNull(cityIssueJDBC.connection);
    }

    @After
    public void closeConnectToDB() throws SQLException {
        Connection connection = cityIssueJDBC.connection;
        connection.close();
    }

    @Before
    public void setUpConnectToDB1() {
        getInstance();
    }

    @Test
    public void connectToDB1() {
        cityIssueJDBC.connectToDB(url, login, password);
        assertNotNull(cityIssueJDBC.connection);
    }

    @After
    public void closeConnectToDB1() {
        Connection connection = cityIssueJDBC.connection;
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setUpBuildUserInfoIntoDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            testConnection = DriverManager.getConnection(url, login, password);
            statement = testConnection.createStatement();
        } catch (
                Exception ex) {
            System.out.println("Seems like there is a problem with test connection...");
        }
    }

    @Test
    public void buildUserInfoIntoDB() {
        cityIssueJDBC = CityIssueJDBC.getInstance();
        cityIssueJDBC.connectToDB();
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
        assertEquals("+38test#", phone);
    }

    @After
    public void closeResourcesForBuildUserInfoIntoDB() {
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
        } catch (SQLException e) {/*NOP*/
        }
    }

}