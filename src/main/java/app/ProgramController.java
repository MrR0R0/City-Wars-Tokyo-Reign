package app;

import menu.authentication.SignUp;

import java.sql.SQLException;
import java.util.Scanner;

public class ProgramController {
    public void run() throws SQLException {
        String endCommand = "(?=.*quit)(?=.*exit)";
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.nextLine().trim().replaceAll(" +", " ");
            if (command.matches(endCommand)) {
                break;
            }
            SignUp.handleInput(command, scanner);
        }
    }
}
