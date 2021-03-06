/**
 * Hosts the main interface for users to interact with Neurotic Job Search
 * Allows users to login and signup, and directs the user to the correct interface
 * based upon what type of account they have
 * @author Brennan Cain, Robert Sturman
 */
import java.util.Scanner;

import dataTypes.Admin;
import dataTypes.Employer;
import dataTypes.Professor;
import dataTypes.Student;
import dataTypes.User;

public class MainUI {
    private Scanner scanner;

    public MainUI() {
        scanner = new Scanner(System.in);
    }
    /**
     * Creates a main menu interface for users to get into the system
     */
    public void doMainMenu() {
        JobSystem.getInstance().loadData();

        System.out.println("Welcome to the Neurotic Job Search!");
        boolean doLoop = true;
        while (doLoop) {
            System.out.print("(1) Login\n(2) Sign Up\n(0) Exit\nChoice: ");
            switch (Integer.parseInt(scanner.nextLine())) {
            case 1:
                doLogin();
                break;
            case 2:
                doSignup();
                break;
            case 0:
                System.out.println("Thank you for using the Neurotic Job Search!");
                doLoop = false;
                break;
            default:
                System.out.println("Invalid selection. Please try again.");
                break;
            }
        }

        JobSystem.getInstance().saveData();
    }
    /**
     * Matches a user to their login info, and sends them to the correct Interface
     */
    public void doLogin() {
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        User user = JobSystem.getInstance().login(username, password);
        if (user == null) {
            System.out.println("Invalid username or password.");
        } else if (user instanceof Student) {
            new StudentUI(scanner, (Student) user).doMainMenu();
        } else if (user instanceof Employer) {
            new EmployerUI().doMainMenu((Employer) user); // TODO: inconsistant constructors
        } else if (user instanceof Admin) {
            new AdminUI().doMainMenu((Admin) user); // TODO: inconsistant constructor
        } else if (user instanceof Professor) {
            new ProfessorUI().doMainMenu((Professor) user);// TODO: inconsistant constructor
        } else {
            System.out.println("Invalid user type.");
        }
    }
    /**
     * Creates a new user based on input, and makes sure they are sent to the correct
     * interface
     */
    public void doSignup() {
        System.out.println("Are you an...\n");
        System.out.println("1) Student");
        System.out.println("2) Employer");
        System.out.println("3) Professor");
        System.out.print("Your input: ");
        int input = Integer.parseInt(scanner.nextLine());

        switch (input) {
        case 1:
            new StudentUI(scanner).doSignup();
            break;
        case 2:
            EmployerUI.doSignup();
            break;
        case 3:
            new ProfessorUI().doSignup();
            break;
        default:
            System.out.println("Invalid selection. Please try again.");
            break;
        }
    }
}
