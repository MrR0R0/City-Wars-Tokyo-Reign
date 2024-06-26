package menu.authentication;

import app.Card;
import app.Error;
import app.User;
import menu.Menu;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;

public class SignUp extends Menu {
    static private String username, pass, email, nickname, recoveryAns, recoveryQ;

    static final private Integer initialMoney = 100;

    static public final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    static public final String USERNAME_REGEX = "[a-zA-Z0-9_]+";
    static public final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z]).+$";

    public static void handleInput(String input, Scanner scanner) throws SQLException {
        String createUserCommand = "^user create -u (?<Username>\\S+) -p (?<Pass>\\S+) (?<PassConfirm>\\S+)" +
                                    " -email (?<Email>\\S+) -n (?<Nickname>\\S+)$";
        String createUserRandomCommand = "^user create -u (?<Username>\\S+) -p random" +
                                    " -email (?<Email>\\S+) -n (?<Nickname>\\S+)$";

        if (input.matches(createUserCommand)) {
            if(Error.alreadyLoggedIn())
                return;
            Matcher matcher = getCommandMatcher(input, createUserCommand);
            if (SignUp.createUser(matcher)) {
                if(twoStepVerification(scanner)){
                    System.out.println("You signed up successfully!");
                }
            }
        }
        else if (input.matches(createUserRandomCommand)) {
            if(Error.alreadyLoggedIn())
                return;
            Matcher matcher = getCommandMatcher(input, createUserRandomCommand);
            if (SignUp.createUserRandom(matcher, scanner)) {
                if(twoStepVerification(scanner)){
                    System.out.println("You signed up successfully!");
                }
            }
        }
    }

    private static boolean createUser(Matcher matcher) {
        String passConf;
        matcher.find();
        username = matcher.group("Username");
        pass = matcher.group("Pass");
        passConf = matcher.group("PassConfirm");
        email = matcher.group("Email");
        nickname = matcher.group("Nickname");

        if (!checkCommonFields(username, email, nickname)) {
            return false;
        }

        //checking whether fields are empty
        if (emptyField(passConf, "Password Confirmation")) {
            return false;
        }
        //validating password
        if(!isValidPasswordFormat(pass)) {
            return false;
        }
        //checking password confirmation
        if (!pass.equals(passConf)) {
            System.out.println("Password confirmation does not match the original password");
            return false;
        }

        return true;
    }

    private static boolean createUserRandom(Matcher matcher, Scanner scanner) {
        matcher.find();
        username = matcher.group("Username");
        email = matcher.group("Email");
        nickname = matcher.group("Nickname");
        if (!checkCommonFields(username, email, nickname)) {
            return false;
        }
        String randomPass = Captcha.generatePassword(10);
        System.out.println("Your random password: " + randomPass);
        System.out.print("Please enter your password: ");
        String command = scanner.nextLine().trim();
        if (command.equals(randomPass)) {
            pass = randomPass;
            return true;
        }
        return false;
    }

    //false: quit
    //true: proper answers have been provided
    private static boolean securityQuestion(Scanner scanner) {
        String pickAQuestion = "^(?i)question pick -q (?<QNumber>.+) -a (?<Ans>.+) -c (?<Confirm>.+)$";
        System.out.println("User created successfully. Please choose a security question :");
        System.out.println("\t ⚫1-What is your father’s name?");
        System.out.println("\t ⚫2-What is your favourite color?");
        System.out.println("\t ⚫3-What was the name of your first pet?");
        System.out.println("In case you wish not to continue, type \"quit\"");
        String command;
        Matcher matcher;

        while (true) {
            command = scanner.nextLine().trim();
            if (command.equalsIgnoreCase("quit")) {
                return false;
            }
            if (command.matches(pickAQuestion)) {
                matcher = getCommandMatcher(command, pickAQuestion);
                matcher.find();
                String qNumber = matcher.group("QNumber");
                String ans = matcher.group("Ans");
                String ansConf = matcher.group("Confirm");
                if (!qNumber.matches("\\d+")) {
                    System.out.println("You should pick a number");
                } else if (Integer.parseInt(qNumber) < 1 || Integer.parseInt(qNumber) > 3) {
                    System.out.println("You should pick a number between 1 & 3");
                } else if (ans.length() < 2) {
                    System.out.println("Your answer should have more than two characters");
                } else if (!ans.equals(ansConf)) {
                    System.out.println("Answer confirmation does not match the original Answer");
                } else {
                    recoveryQ = qNumber;
                    recoveryAns = ans;
                    return true;
                }
            } else {
                System.out.println("Answers should be in the following format:");
                System.out.println("question pick -q <question-number> -a <answer> -c <answer-confirm>");
                System.out.println("If you wish to quit, type \"quit\"");
            }
        }
    }

    // Consolidate common logic for createUser and createUserRandom to avoid duplication
    private static boolean checkCommonFields(String username, String email, String nickname) {

        if (emptyField(username, "Username")) {
            return false;
        }
        if (emptyField(email, "Email")) {
            return false;
        }
        if (emptyField(nickname, "Nickname")) {
            return false;
        }

        //checking the requirements for username
        if (!username.matches(USERNAME_REGEX)) {
            System.out.println("The username should consist of lowercase or uppercase letters, numbers, and underscores.");
            return false;
        }

        //checking email format
        if (!email.matches(EMAIL_REGEX)) {
            System.out.println("The email does not have the correct format");
            return false;
        }

        if(User.signedUpUsers.containsKey(username)){
            System.out.println("A user with the same username exists");
            return false;
        }
        return true;
    }

    static private boolean twoStepVerification(Scanner scanner){
        if (securityQuestion(scanner)) {
            if (Captcha.checkCaptcha(scanner)) {
                User tmpUser = new User(username, pass, nickname, email, recoveryAns,
                        recoveryQ, "", initialMoney, 1, User.signedUpUsers.size()+1);
                tmpUser.giveRandomCard();
                Card.updateUserCards(tmpUser);
                User.signedUpUsers.put(username, tmpUser);
                return true;
            }
        }
        return false;
    }

    static public boolean isValidPasswordFormat(String password){
        if (emptyField(password, "Password")) {
            System.out.println("Password is empty");
            return false;
        }

        //checking whether the password is weak
        if (password.length() < 8) {
            System.out.println("Password should be at least 8 characters!");
            return false;
        }
        if (!password.matches(PASSWORD_REGEX)) {
            System.out.println("The password must contain at least one uppercase letter and one lowercase letter.");
            return false;
        }
        if (password.replaceAll("[a-zA-Z0-9]", "").isEmpty()) {
            System.out.println("The password must contain at least one special character");
            return false;
        }
        return true;
    }
}
