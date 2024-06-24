package menu.authentication;

import app.User;
import database.Connect;
import menu.Menu;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;

public class SignUp extends Menu {
    static private String username, pass, passConf, email, nickname, recoveryAns, recoveryQ;
    static private User tmpUser;
    static private Integer initialMoney = 100;
    static public ArrayList<User> signedUpdUsers;

    public static void handleInput(String input, Scanner scanner) throws SQLException {
        String createUserCommand = "^user create -u (?<Username>\\S+) -p (?<Pass>\\S+) (?<PassConfirm>\\S+)" +
                                    " -email (?<Email>\\S+) -n (?<Nickname>\\S+)$";
        String createUserRandomCommand = "^user create -u (?<Username>\\S+) -p random" +
                                    " -email (?<Email>\\S+) -n (?<Nickname>\\S+)$";

        signedUpdUsers = Connect.getUsers();

        if (input.matches(createUserCommand)) {
            Matcher matcher = getCommandMatcher(input, createUserCommand);
            if (SignUp.createUser(matcher)) {
                twoStepVerification(scanner);
            }
        }
        else if (input.matches(createUserRandomCommand)) {
            Matcher matcher = getCommandMatcher(input, createUserRandomCommand);
            if (SignUp.createUserRandom(matcher, scanner)) {
                twoStepVerification(scanner);
            }
        }
        else{
            System.out.println("What?");
        }
    }

    private static boolean createUser(Matcher matcher) {

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
        if (emptyField(pass, "Password")) {
            return false;
        }
        if (emptyField(passConf, "Password Confirmation")) {
            return false;
        }

        //checking whether the password is weak
        if (pass.length() < 8) {
            System.out.println("Password should be at least 8 characters!");
            return false;
        }
        if (!pass.matches("^(?=.*[a-z])(?=.*[A-Z]).+$")) {
            System.out.println("The password must contain at least one uppercase letter and one lowercase letter.");
            return false;
        }
        if (pass.replaceAll("[a-zA-Z0-9]", "").isEmpty()) {
            System.out.println("The password must contain at least one special character");
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
        String randomPass = generatePassword(10);
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
        String pickAQuestion = "^question pick -q (?<QNumber>.+) -a (?<Ans>.+) -c (?<Confirm>.+)$";
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
        final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

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
        if (!username.matches("[a-zA-Z0-9_]+")) {
            System.out.println("The username should consist of lowercase or uppercase letters, numbers, and underscores.");
            return false;
        }

        //checking email format
        if (!email.matches(EMAIL_REGEX)) {
            System.out.println("The email does not have the correct format");
            return false;
        }

        if(User.usernameInArray(username, signedUpdUsers)){
            System.out.println("A user with the same username exists");
            return false;
        }
        return true;
    }

    //Creates a strong password
    private static String generatePassword(int PASSWORD_LENGTH) {
        final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String LOWER = UPPER.toLowerCase();
        final String DIGITS = "0123456789";
        final String SPECIAL = "!@#$%^&*()-_=+[]{}|;:,.<>?";
        final String ALL_CHARS = UPPER + LOWER + DIGITS + SPECIAL;
        SecureRandom random = new SecureRandom();
        List<Character> chars = new ArrayList<>();

        // Add characters from each character set
        chars.add(UPPER.charAt(random.nextInt(UPPER.length())));
        chars.add(LOWER.charAt(random.nextInt(LOWER.length())));
        chars.add(DIGITS.charAt(random.nextInt(DIGITS.length())));
        chars.add(SPECIAL.charAt(random.nextInt(SPECIAL.length())));

        // Fill remaining characters randomly
        for (int i = 4; i < PASSWORD_LENGTH; i++) {
            chars.add(ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length())));
        }

        // Shuffle characters and create password string
        Collections.shuffle(chars);
        StringBuilder password = new StringBuilder();
        for (char c : chars) {
            password.append(c);
        }
        return password.toString();
    }

    private static boolean checkCaptcha(int chances, Scanner scanner) {
        System.out.println("Confirm you're not a robot");
        String randomText = generatePassword(5);
        System.out.println(Captcha.textToASCII(randomText));
        String command;
        int counter = chances;

        while (counter > 0) {
            counter--;
            command = scanner.nextLine().trim();
            if (command.equals(randomText)) {
                return true;
            } else if (command.equals("quit")) {
                return false;
            } else {
                System.out.println("Remaining chances: " + counter);
                randomText = generatePassword(5);
                System.out.println(Captcha.textToASCII(randomText));
            }
        }
        return false;
    }

    static private void twoStepVerification(Scanner scanner) throws SQLException {
        if (securityQuestion(scanner)) {
            if (checkCaptcha(3, scanner)) {
                tmpUser = new User(username, pass, nickname, email, recoveryAns,
                        recoveryQ, "", initialMoney, 1);
                tmpUser.addToTable();
            }
        }
    }
}
