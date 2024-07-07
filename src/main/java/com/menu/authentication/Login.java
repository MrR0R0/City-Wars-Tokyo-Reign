package com.menu.authentication;

import com.app.Error;
import com.app.ProgramController;
import com.app.User;
import com.menu.Menu;

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
    private static final String adminPass = "sonic";

    final static public String loginCommand = "^user login -u (?<Username>\\S+) -p (?<Pass>\\S+)$";
    final static public String forgotPassword = "^forgot my password -u (?<Username>\\S+)$";
    final static public String adminLogin = "^login admin (?<Pass>\\S+)$";

    public static void handleInput(String input, Scanner scanner) throws IOException, SQLException {
        if(input.matches(loginCommand)){
            if(Error.alreadyLoggedIn())
                return;
            Matcher matcher = getCommandMatcher(input, loginCommand);
            matcher.find();
            if(checkLogIn(matcher, scanner)){
                String username = matcher.group("Username");
                System.out.println("user logged in successfully");
                System.out.println("Welcome " + username + "!");
                Menu.loggedInUser = User.signedUpUsers.get(User.getIdByUsername(username));
                Menu.currentMenu = MenuType.Main;
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
        if(input.matches(adminLogin) && Menu.currentMenu == MenuType.Main){
            Matcher matcher = getCommandMatcher(input, adminLogin);
            matcher.find();
            String pass = matcher.group("Pass");
            if(pass.equals(adminPass)){
                Menu.currentMenu = Menu.MenuType.Admin;
            }
            else{
                System.out.println("You are not admin :)");
            }
        }
    }
    public static boolean checkLogIn(Matcher matcher, Scanner scanner) throws IOException {
        //check if the user is in the table
        String username = matcher.group("Username");
        String password = matcher.group("Pass");
        Integer id = User.getIdByUsername(username);
        //invalid username
        if(!Error.userRegistered(username)){
            return false;
        }
        //valid username and password
        if(matchingPassword(id, password)){
            return true;
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
                    if(matchingPassword(id, command)){
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

    public static void resetPassword(Matcher matcher, Scanner scanner) {
        String username = matcher.group("Username");
        if(Error.userRegistered(username)){
            ArrayList<String> questions = new ArrayList<>(Arrays.asList("What is your father’s name?",
                    "What is your favourite color?", "What was the name of your first pet?"));
            User tmpUser = User.signedUpUsers.get(User.getIdByUsername(username));
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
                        System.out.println("You will be directed to the main com.menu");
                        return;
                    }
                    if(SignUp.isValidPasswordFormat(command)){
                        System.out.println("Password changed successfully");
                        User.signedUpUsers.get(User.getIdByUsername(username)).setPassword(command);
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

    public static boolean matchingPassword(Integer id, String pass){
        return User.signedUpUsers.get(id).getPassword().equals(pass);
    }
}