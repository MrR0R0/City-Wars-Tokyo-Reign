package menu.authentication;

import app.Error;
import app.User;
import menu.Menu;

import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.time.Duration;
import java.time.Instant;

public class Login extends Menu {
    private static final int maxTry = 3;

    public static void handleInput(String input, Scanner scanner) throws IOException {
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
        }
    }
    private static boolean checkLogIn(Matcher matcher, Scanner scanner) throws IOException {
        //check if the user is in the table
        String username = matcher.group("Username");
        String password = matcher.group("Pass");
        int credentials = checkCredentials(username, password);
        //valid username and password
        if(credentials == 0){
            return true;
        }
        //invalid username
        if(credentials == 2){
            System.out.println("Username does not exist!");
            return false;
        }
        //non-matching username and password
        if(credentials == 1){
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
                    if(checkCredentials(username, command) == 0){
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

    /*
    0: valid
    1: wrong password
    2: non-existent username
    */
    private static int checkCredentials(String username, String pass){
        if(User.signedUpUsers.containsKey(username)){
            if(User.signedUpUsers.get(username).getPassword().equals(pass)){
                return 0;
            }
            return 1;
        }
        return 2;
    }
}