package menu;

import app.ProgramController;
import database.Connect;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.IntStream;

public class MainMenu extends Menu {
    private static final int LINES_ON_PAGE = 10, NAME_PAD = 19, NUM_PAD = 3, CONS_PAD = 5;
    static public void handleInput(String input, Scanner scanner) throws SQLException {
        String switchToPlay = "^select menu play$";
        String switchToShop = "^select menu shop$";
        String showCards = "^show cards$";
        String showHistory = "^show history$";
        String logoutCommand = "^log out$";
        if(input.matches(logoutCommand)){
            Menu.logOut();
            System.out.println("Logged out successfully");
        }
        else if(input.matches(switchToPlay)){
            Menu.currentMenu = MenuType.Play;
            System.out.println("You are now in \"Play\" menu");
        }
        else if(input.matches(switchToShop)){
            Menu.currentMenu = MenuType.Shop;
            System.out.println("You are now in \"Shop\" menu");
        }
        else if(input.matches(showCards)){
            System.out.println("Here are your cards:");
            Menu.loggedInUser.showCards();
        }
        else if(input.matches(showHistory)){
            showHistory(scanner);
        }
    }

    static private void showHistory(Scanner scanner) throws SQLException {
        ArrayList<String> history = Connect.getUserHistory(String.valueOf(Menu.loggedInUser.getId()), NAME_PAD, CONS_PAD, NUM_PAD);
        int len = history.size();
        int numberOfPages = Math.ceilDiv(len, LINES_ON_PAGE);
        int currentPage = 1;
        System.out.println("History:");
        showPage(history, 1, numberOfPages);
        if(len > 10){
            while(true){
                System.out.println("If you want to view other pages, enter the page number; otherwise, enter 'quit'");
                String command = scanner.nextLine().trim().replaceAll(" +", " ");
                if(ProgramController.checkQuit(command)){
                    System.out.println("You will be directed to Main menu");
                    return;
                }
                else if(command.matches("\\d+")){
                    if(Integer.parseInt(command) > numberOfPages){
                        System.out.println("Please enter a number between 1 & " + numberOfPages);
                    }
                    else{
                        currentPage = Integer.parseInt(command);
                        showPage(history, currentPage, numberOfPages);
                    }
                }
                else{
                    System.out.println("Invalid input!");
                }
            }
        }
        System.out.println();
    }
    static private void showPage(ArrayList<String> history, int page, int pages){
        int start = LINES_ON_PAGE*(page-1);
        int end = LINES_ON_PAGE*(page);
        showTopBar(NAME_PAD, CONS_PAD, NUM_PAD);
        IntStream.range(start, Math.min(end, history.size())).forEach(i -> System.out.println(history.get(i)));
        showPageBar(pages, page);
    }
    static private void showPageBar(int pages, int page){
        System.out.print("Pages: ");
        if(page-2 > 1){
            System.out.print("...");
        }
        for(int i=Math.max(pages-2, 1); i<=Math.min(page+2, pages); i++){
            System.out.print(" " + i);
        }
        if(page+2 < pages){
            System.out.print(" ...");
        }
        System.out.println();
    }
    static private void showTopBar(int namePad, int consPad, int numPad){
        String index = String.format("%-"+numPad+"s", "NO.");
        String host = String.format("%-"+namePad+"s", "host");
        String guest = String.format("%-"+namePad+"s", "guest");
        System.out.println(index + "|" + host + "|" + guest);
    }
}
