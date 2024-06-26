package app;

import database.Connect;
import menu.MainMenu;
import menu.Menu;
import menu.ProfileMenu;
import menu.authentication.Login;
import menu.authentication.SignUp;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class ProgramController {
    public void run() throws SQLException, IOException {
        Scanner scanner = new Scanner(System.in);
        User.signedUpUsers = Connect.getUsers();
        Card.allCards = Connect.getCards();
        String logoutCommand = "^log out$";


        while (true) {
            String command = scanner.nextLine().trim().replaceAll(" +", " ");
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
            switch (Menu.currentMenu){
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
            }
        }
    }

    static public boolean checkQuit(String command){
        String endCommand = "^(quit|exit)$";
        return command.matches(endCommand);
    }
}
