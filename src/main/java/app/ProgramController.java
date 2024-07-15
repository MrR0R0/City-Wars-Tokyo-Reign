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
        Menu.loggedInUser = User.signedUpUsers.get(1);
        Menu.currentMenu = Menu.MenuType.Main;
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
