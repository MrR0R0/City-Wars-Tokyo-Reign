package app;

import database.Connect;
import menu.authentication.Login;
import menu.authentication.SignUp;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Scanner;

public class ProgramController {
    public void run() throws SQLException, IOException {
        String endCommand = "(?=.*quit)(?=.*exit)";
        Scanner scanner = new Scanner(System.in);
        User.signedUpUsers = Connect.getUsers();
        while (true) {
            String command = scanner.nextLine().trim().replaceAll(" +", " ");
            if (command.matches(endCommand)) {
                break;
            }
            SignUp.handleInput(command, scanner);
            Login.handleInput(command, scanner);
        }
    }
}
