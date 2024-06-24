package app;

import authentication.SignUp;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static Matcher getCommandMatcher(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(input);
    }

}
