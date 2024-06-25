package app;

import database.Connect;
import menu.Menu;
import menu.authentication.Login;
//import menu.ProfileMenu;
import menu.authentication.SignUp;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class ProgramController {
    public void run() throws SQLException, IOException {
        Scanner scanner = new Scanner(System.in);
        User.signedUpUsers = Connect.getUsers();
        String logoutCommand = "log out";
        while (true) {
            String command = scanner.nextLine().trim().replaceAll(" +", " ");
            if (checkQuit(command)) {
                break;
            }
            else if(command.matches(logoutCommand)){
                if(Menu.isLoggedIn()){
                    Menu.loggedInUser = new User();
                    System.out.println("Logged out successfully");
                }
                else{
                    System.out.println("You should log in first");
                }
            }
            else {
                SignUp.handleInput(command, scanner);
                Login.handleInput(command, scanner);
            }
            //ProfileMenu.handleInput(command, scanner);
        }
    }

    static public boolean checkQuit(String command){
        String endCommand = "^(quit|exit)$";
        return command.matches(endCommand);
    }
}
