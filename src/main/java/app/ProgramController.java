package app;

import database.Connect;
import menu.*;
import menu.authentication.Captcha;
import menu.authentication.Login;
import menu.authentication.SignUp;
import menu.play.Play;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class ProgramController {
    public void run() throws SQLException, IOException {
        Scanner scanner = new Scanner(System.in);
        Card.allCards = Connect.getCards();
        User.signedUpUsers = Connect.getUsers();
        String logoutCommand = "^log out$";
        String helpCommand = "^help$";
        while (true) {
            String command = scanner.nextLine().trim();
            if (checkQuit(command)) {
                Connect.updateDatabase();
                break;
            }
            else if(command.matches(logoutCommand)){
                if(Menu.isLoggedIn()){
                    Menu.logOut();
                    System.out.println("Logged out successfully");
                }
                else{
                    System.out.println("You should log in first");
                }
            }
            else if(command.matches(helpCommand)){
                switch (Menu.currentMenu) {
                    case Authentication -> {
                        System.out.println("-".repeat(100));
                        System.out.println("login: user login -u (?<Username>\\S+) -p (?<Pass>\\S+)\n" +
                                "Forgot pass: forgot my password -u (?<Username>\\S+)\n" +
                                "Admin: login admin (?<Pass>\\S+)");
                        System.out.println("-".repeat(100));
                        System.out.println("Signup: user create -u (?<Username>\\\\S+) -p (?<Pass>\\\\S+) (?<PassConfirm>\\\\S+) -email (?<Email>\\\\S+) -n (?<Nickname>\\\\S+)\n" +
                                "\n" +
                                "Signup random: user create -u (?<Username>\\\\S+) -p random -email (?<Email>\\\\S+) -n (?<Nickname>\\\\S+)");
                        System.out.println("-".repeat(100));
                    }
                    case Main -> {
                        System.out.println("-".repeat(100));
                        System.out.println("To play: select menu play\n" +
                                "To shop: select menu shop\n" +
                                "To profile: select menu profile\n" +
                                "To history: select menu history\n" +
                                "Show cards: show cards\n" +
                                "Logout: logout\n" +
                                "Sort history: sort by (?<Field>\\d+) (?<Type>\\d+)");
                        System.out.println("-".repeat(100));
                    }
                    case Profile -> {
                        System.out.println("-".repeat(100));
                        System.out.println("Show info: Show information\n" +
                                "Change username: Profile change -u (?<Username>\\S+)\n" +
                                "Change nickname: Profile change -n (?<Nickname>\\S+)\n" +
                                "Change password: Profile change password -o (?<oldPassword>\\S+) -n (?<newPassword>\\S+)\n" +
                                "Change email: Profile change -e (?<Email>\\S+)");
                        System.out.println("-".repeat(100));
                    }
                    case Admin -> {
                        System.out.println("You should know better :)");
                    }
                    case Shop -> {
                        System.out.println("-".repeat(100));
                        System.out.println("Upgrade card: upgrade card number (?<cardNum>\\d+)\n" +
                                "Show balance: show wallet\n" +
                                "Buy card: buy card number (?<cardNum>\\d+)\n" +
                                "Showing upgradable cards: show upgradable cards\n" +
                                "Showing purchasable cards: show purchasable cards\n" +
                                "Showing Properties\" show properties of card number (?<cardNum>\\d+)\n");
                        System.out.println("-".repeat(100));
                    }
                    case Play -> {
                        System.out.println("-".repeat(100));
                        System.out.println("Select mode: select (?<Mode>\\w+) as the play mode\n" +
                                "Select character: (?<Character>\\w+)\n" +
                                "Select Card: select card number (?<number>\\d+) player (?<player>\\S+)\n" +
                                "Place Card: place card number (?<cardNum>\\d+) in block (?<cellNum>\\d+)\n" +
                                "Show playground: show playground\n" +
                                "Show hand: show hand");
                        System.out.println("-".repeat(100));
                    }
                }
            }
            else {
                switch (Menu.currentMenu) {
                    case Authentication -> {
                        SignUp.handleInput(command, scanner);
                        Login.handleInput(command, scanner);
                    }
                    case Main -> {
                        MainMenu.handleInput(command, scanner);
                    }
                    case Profile -> {
                        ProfileMenu.handleInput(command, scanner);
                    }
                    case Admin -> {
                        Admin.handleInput(command, scanner);
                    }
                    case Shop -> {
                        Shop.handleInput(command, scanner);
                    }
                    case Play -> {
                        Play.handleInput(command, scanner);
                    }
                }
            }
        }
    }

    static public boolean checkQuit(String command){
        String endCommand = "^(quit|exit)$";
        return command.matches(endCommand);
    }
}
