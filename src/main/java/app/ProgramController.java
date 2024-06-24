package app;

import menu.authentication.SignUp;
import java.util.Scanner;

public class ProgramController {
    public void run() {
        String endCommand = "^end";
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
