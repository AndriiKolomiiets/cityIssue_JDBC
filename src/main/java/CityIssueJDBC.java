import java.sql.*;

import com.sun.org.apache.bcel.internal.generic.NOP;
import org.apache.logging.log4j.*;

public class CityIssueJDBC {
    static CityIssueJDBC jdbcInstance;
    private String url;
    private String login;
    private String password;
    PreparedStatement preparedStatement;
    protected Connection connection;

    private static final Logger logger = LogManager.getLogger(CityIssueJDBC.class);

    //Private constructor and getInstance() for Singleton ensure;
    private CityIssueJDBC() {
    }

    public static CityIssueJDBC getInstance() {
        if (jdbcInstance == null) {
            synchronized (CityIssueJDBC.class) {
                jdbcInstance = new CityIssueJDBC();
                logger.info("JDBC instance was created.");
            }
        }
        return jdbcInstance;
    }

    /*        Adding mySQL via IntellijIdea:
            File->Project Structure->Libraries
            Then Click on the plus(+) sign and select From Meven....
            After you'll get a search box there you should put
            mysql:mysql-connector-java:5.1.40
    */
    public void connectToDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            url = "jdbc:mysql://localhost/city_issue_db?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
            login = "root";
            password = "root784512";
            connection = DriverManager.getConnection(url, login, password);
            logger.trace("Connection successful!");
        } catch (
                Exception ex) {
            logger.error("Connection failed...");
            logger.error(ex);
        }
    }

    public void connectToDB(String url, String login, String pass) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, login, pass);
            logger.trace("Connection successful!");
        } catch (
                Exception ex) {
            logger.error("Connection failed...");
            logger.error(ex);
        }
    }

    public void buildUserInfoIntoDB(String firstName, String lastName, String phone, String email) {
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            logger.error("AutoCommit can't be turned of...");
            e.printStackTrace();
        }
        try {
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO users(first_name, last_name, phone, email) VALUES (?, ?, ?, ?)");
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, phone);
            preparedStatement.setString(4, email);
            preparedStatement.addBatch();
            preparedStatement.executeBatch();
            logger.trace("Contact: " + firstName + " " + lastName + " was created successfully.");
        } catch (SQLException e) {
            logger.error("Something goes wrong with the DB...");
            e.printStackTrace();
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    logger.error("Closing preparedStatement failed.");
                }
            }
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                logger.error("AutoCommit can't be turned on...");
                e.printStackTrace();
            }
            if (connection != null) {
                try {
                    connection.close();
                    logger.info("Connection was closed successfully.");
                } catch (SQLException e) {
                    logger.error("Closing connection failed.");
                }
            }

        }
    }
}
