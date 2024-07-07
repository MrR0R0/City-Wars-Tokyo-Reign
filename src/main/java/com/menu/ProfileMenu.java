package com.menu;

import com.app.Error;
import com.app.User;
import com.menu.authentication.Captcha;
import com.menu.authentication.SignUp;

import java.util.Scanner;
import java.util.regex.Matcher;

public class ProfileMenu extends Menu {
    final static private String showInfoCommand = "^(?i)Show information\\s*$";
    final static private String changeUsernameCommand = "^(?i)Profile change -u (?<Username>\\S+)$";
    final static private String changeNicknameCommand = "^(?i)Profile change -n (?<Nickname>\\S+)$";
    final static private String changePasswordCommand = "^(?i)Profile change password -o (?<oldPassword>\\S+) -n (?<newPassword>\\S+)$";
    final static private String changeEmailCommand = "^(?i)Profile change -e (?<Email>\\S+)$";


    static public void handleInput(String input, Scanner scanner) {
        Matcher matcher;


        if (input.matches(backCommand)){
            if (Error.loginFirst())
                return;
            currentMenu = MenuType.Main;
            showCurrentMenu();
        }

        if (input.matches(showInfoCommand)) {
            if (Error.loginFirst())
                return;
            matcher = getCommandMatcher(input, showInfoCommand);
            if (matcher.find())
                loggedInUser.showProperties();
        }

        if (input.matches(changeUsernameCommand)) {
            if (Error.loginFirst())
                return;
            matcher = getCommandMatcher(input, changeUsernameCommand);
            if (matcher.find()) {
                changeUsername(matcher);
            }
        }

        if (input.matches(changeNicknameCommand)) {
            if (Error.loginFirst())
                return;
            matcher = getCommandMatcher(input, changeNicknameCommand);
            if (matcher.find()) {
                changeNickname(matcher);
            }
        }

        if (input.matches(changePasswordCommand)) {
            if (Error.loginFirst())
                return;
            matcher = getCommandMatcher(input, changePasswordCommand);
            if (matcher.find()) {
                changePassword(matcher, scanner);
            }
        }

        if (input.matches(changeEmailCommand)) {
            if (Error.loginFirst())
                return;
            matcher = getCommandMatcher(input, changeEmailCommand);
            if (matcher.find()) {
                changeEmail(matcher);
            }
        }
    }

    static private void changeUsername(Matcher matcher) {
        String username = matcher.group("Username");
        if (emptyField(username, "Username")) {
            return;
        }
        if (!username.matches(SignUp.USERNAME_REGEX)) {
            System.out.println("The username should consist of lowercase or uppercase letters, numbers, and underscores.");
            return;
        }
        if (User.signedUpUsers.containsKey(username)) {
            System.out.println("A user with the same username exists");
        } else {
            loggedInUser.setUsername(username);
            System.out.println("username has been changed to " + username + " successfully");
        }
    }

    static private void changeNickname(Matcher matcher) {
        String nickname = matcher.group("Nickname");
        if (emptyField(nickname, "Nickname")) {
            return;
        }
        if (User.isInUsersList("nickname", nickname))
            System.out.println("A user with the same nickname exists");
        else {
            loggedInUser.setNickname(nickname);
            System.out.println("nickname has been changed to " + nickname + " successfully");
        }
    }

    static private void changePassword(Matcher matcher, Scanner scanner) {
        String oldPassword = matcher.group("oldPassword");
        String newPassword = matcher.group("newPassword");

        if (emptyField(oldPassword, "Old password") || emptyField(newPassword, "New password")) {
            return;
        }
        if (oldPassword.length() < 8 || newPassword.length() < 8) {
            System.out.println("Password should be at least 8 characters!");
            return;
        }
        if (!oldPassword.matches(SignUp.PASSWORD_REGEX) || !newPassword.matches(SignUp.PASSWORD_REGEX)) {
            System.out.println("The password must contain at least one uppercase letter and one lowercase letter.");
            return;
        }
        if (oldPassword.replaceAll("[a-zA-Z0-9]", "").isEmpty() || newPassword.replaceAll("[a-zA-Z0-9]", "").isEmpty()) {
            System.out.println("The password must contain at least one special character");
            return;
        }
        if (Captcha.checkCaptcha(scanner)) {
            if (!loggedInUser.getPassword().equals(oldPassword)) {
                System.out.println("Current password is incorrect!");
                return;
            }
            if (newPassword.equals(oldPassword)) {
                System.out.println("Please enter a new password!");
            }
            else {
                loggedInUser.setPassword(newPassword);
                System.out.println("Password has been changed to " + newPassword + " successfully");
            }
        }
    }

    static private void changeEmail(Matcher matcher) {
        String email = matcher.group("Email");
        if (emptyField(email, "Email")) {
            return;
        }
        if (!email.matches(SignUp.EMAIL_REGEX)) {
            System.out.println("The email does not have the correct format");
        }
        if (User.isInUsersList("email", email))
            System.out.println("A user with the same email exists");
        else {
            loggedInUser.setEmail(email);
            System.out.println("Email has been changed to " + email + " successfully");
        }
    }
}