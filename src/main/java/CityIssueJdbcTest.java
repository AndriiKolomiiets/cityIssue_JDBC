import java.sql.SQLException;
import java.util.Scanner;

public class CityIssueJdbcTest {
    static String firstName;
    static String lastName;
    static String phone;
    static String email;

    public static void main(String[] args) {
        CityIssueJDBC cityIssueJDBC = CityIssueJDBC.getInstance();
        cityIssueJDBC.connectToDB();
        getUserInput();
        cityIssueJDBC.buildUserInfoIntoDB(firstName, lastName, phone, email);
    }

    public static void getUserInput() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your first name: ");
        firstName = scanner.nextLine();

        System.out.println("Enter your last name: ");
        lastName = scanner.nextLine();

        System.out.println("Enter your phone: ");
        phone = scanner.nextLine();

        System.out.println("Enter your email: ");
        email = scanner.nextLine();
    }
}

