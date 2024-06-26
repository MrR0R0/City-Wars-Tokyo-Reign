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
        String logoutCommand = "^log out$";
        while (true) {
            String command = scanner.nextLine().trim().replaceAll(" +", " ");
            if (checkQuit(command)) {
                break;
            }
            else if(Menu.currentMenu.equals(Menu.MenuType.Authentication)){
                SignUp.handleInput(command, scanner);
                Login.handleInput(command, scanner);
            }
            else if(Menu.currentMenu.equals(Menu.MenuType.Profile)){
                ProfileMenu.handleInput(command, scanner);
            }
            else if(Menu.currentMenu.equals(Menu.MenuType.Main)){
                MainMenu.handleInput(command, scanner);
            }
        }
    }

    static public boolean checkQuit(String command){
        String endCommand = "^(quit|exit)$";
        return command.matches(endCommand);
    }
}
