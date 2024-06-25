package menu.authentication;

import app.Error;
import app.ProgramController;
import app.User;
import database.Connect;
import menu.Menu;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.time.Duration;
import java.time.Instant;

public class Login extends Menu {
    private static final int maxTry = 3;
    public static void handleInput(String input, Scanner scanner) throws IOException, SQLException {
        String loginCommand = "^user login -u (?<Username>\\S+) -p (?<Pass>\\S+)$";
        String forgotPassword = "^forgot my password -u (?<Username>\\S+)$";
        if(input.matches(loginCommand)){
            if(Error.alreadyLoggedIn())
                return;
            Matcher matcher = getCommandMatcher(input, loginCommand);
            matcher.find();
            if(checkLogIn(matcher, scanner)){
                String username = matcher.group("Username");
                System.out.println("user logged in successfully");
                System.out.println("Welcome " + username + "!");
                Menu.loggedInUser = User.signedUpUsers.get(username);
                return;
            }
        }
        if(input.matches(forgotPassword)){
            if(Error.alreadyLoggedIn())
                return;
            Matcher matcher = getCommandMatcher(input, forgotPassword);
            matcher.find();
            resetPassword(matcher, scanner);
        }
    }
    private static boolean checkLogIn(Matcher matcher, Scanner scanner) throws IOException {
        //check if the user is in the table
        String username = matcher.group("Username");
        String password = matcher.group("Pass");
        //valid username and password
        if(matchingPassword(username, password)){
            return true;
        }
        //invalid username
        else if(!Error.userRegistered(username)){
            return false;
        }
        //non-matching username and password
        else{
            int counter = maxTry - 1;
            Instant start = Instant.now();
            long elapsed;
            System.out.println("Incorrect password :(");
            String command = "";
            while(counter >= 0){
                if(System.in.available() > 0) {
                    command = scanner.nextLine().trim();
                }
                elapsed = Duration.between(start, Instant.now()).getSeconds();
                //if enough time has passed from the last try
                if(elapsed >= 5L *(maxTry - counter)){
                    System.out.println("Re-enter password");
                    command = scanner.nextLine().trim();
                    //if re-entered password is correct
                    if(matchingPassword(username, command)){
                        return true;
                    }
                    //if re-entered password is incorrect
                    else{
                        counter--;
                        start = Instant.now();
                        elapsed = Duration.between(start, Instant.now()).getSeconds();
                        System.out.println("You can quit by typing \"quit\" or try again in: " + (5L *(maxTry - counter) - elapsed));
                    }
                }
                //if user should wait until the next attempt
                else if(!command.isEmpty() && elapsed < 5L *(maxTry - counter)){
                    System.out.println("You can quit by typing \"quit\" or try again in: "
                            + (5L *(maxTry - counter) - elapsed));
                    command = "";
                }
            }
        }
        System.out.println("You will be directed to authentication page!");
        return false;
    }

    private static void resetPassword(Matcher matcher, Scanner scanner) throws SQLException {
        String username = matcher.group("Username");
        if(Error.userRegistered(username)){
            ArrayList<String> questions = new ArrayList<>(Arrays.asList("What is your father’s name?",
                    "What is your favourite color?", "What was the name of your first pet?"));
            User tmpUser = User.signedUpUsers.get(username);
            System.out.println(questions.get(tmpUser.getRecoveryQ()-1));
            String command;
            command = scanner.nextLine().trim();
            //if user answers correctly
            if(tmpUser.getRecoveryAns().equals(command)){
                System.out.println("You have answered correctly \uD83C\uDF89");
                System.out.println("You can quit or enter you new password:");
                command = scanner.nextLine().trim();
                while(true){
                    if(ProgramController.checkQuit(command)){
                        System.out.println("You will be directed to the main menu");
                        return;
                    }
                    if(SignUp.isValidPasswordFormat(command)){
                        System.out.println("Password changed successfully");
                        User.signedUpUsers.get(username).setPassword(command);
                        return;
                    }
                    command = scanner.nextLine().trim();
                }
            }
            //if user answers incorrectly
            else{
                System.out.println("You have answered incorrectly");
            }
        }
    }

    private static boolean matchingPassword(String username, String pass){
        return User.signedUpUsers.get(username).getPassword().equals(pass);
    }
}