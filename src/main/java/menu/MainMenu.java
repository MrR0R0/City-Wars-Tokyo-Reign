package menu;

import app.ProgramController;
import database.Connect;
import database.History;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.stream.IntStream;

public class MainMenu extends Menu {
    final static private int LINES_ON_PAGE = 10, NAME_PAD = 19, NUM_PAD = 3, CONS_PAD = 5;
    static private ArrayList<History> history;

    final static private String switchToPlay = "^select menu play$";
    final static private String switchToShop = "^select menu shop$";
    final static private String showCards = "^show cards$";
    final static private String showHistory = "^show history$";
    final static private String logoutCommand = "^log out$";

    static public void handleInput(String input, Scanner scanner) throws SQLException {

        if(input.matches(logoutCommand)){
            Menu.logOut();
            System.out.println("Logged out successfully");
        } else if (input.matches(switchToPlay)) {
            Menu.currentMenu = MenuType.Play;
            System.out.println("You are now in \"Play\" menu");
        } else if (input.matches(switchToShop)) {
            Menu.currentMenu = MenuType.Shop;
            System.out.println("You are now in \"Shop\" menu");
        } else if (input.matches(showCards)) {
            System.out.println("Here are your cards:");
            Menu.loggedInUser.showCards();
        } else if (input.matches(showHistory)) {
            showHistory(scanner);
        }
    }

    static private void showHistory(Scanner scanner) throws SQLException {
        String sortCommand = "^sort by (?<Field>\\d+) (?<Type>\\d+)$";
        history = Connect.getUserHistory(String.valueOf(Menu.loggedInUser.getId()));
        int len = history.size();
        int numberOfPages = Math.ceilDiv(len, LINES_ON_PAGE);
        int currentPage = 1;
        System.out.println("History:");
        System.out.println("You can also sort your match history by (1-date 2-opp's name 3-opp's level 4-result) (1-asc 2-des)");
        System.out.println("Using the following command: sort by #field_name #asc_des");
        System.out.println("for instance \"sort by 1 1\" will sort by date in ascending order");
        showPage(1, numberOfPages);
        while (true) {
            System.out.println("For viewing other pages enter the page's number;");
            System.out.println("You can also sort here");
            System.out.println("otherwise, enter 'quit'");
            String command = scanner.nextLine().trim().replaceAll(" +", " ");
            if (ProgramController.checkQuit(command)) {
                System.out.println("You will be directed to Main menu");
                return;
            } else if (command.matches(sortCommand)) {
                Matcher matcher = getCommandMatcher(command, sortCommand);
                matcher.find();
                String field = matcher.group("Field");
                String type = matcher.group("Type");
                sortHistory(field, type);
            } else if (command.matches("\\d+")) {
                if (Integer.parseInt(command) > numberOfPages) {
                    System.out.println("Please enter a number between 1 & " + numberOfPages);
                } else {
                    currentPage = Integer.parseInt(command);
                    showPage(currentPage, numberOfPages);
                }
            } else {
                System.out.println("Invalid input!");
            }
        }
    }

    static private void showPage(int page, int pages) {
        int start = LINES_ON_PAGE * (page - 1);
        int end = LINES_ON_PAGE * (page);
        showTopBar(NAME_PAD, CONS_PAD, NUM_PAD);
        IntStream.range(start, Math.min(end, history.size()))
                .forEach(i -> history.get(i).show(NAME_PAD, CONS_PAD, NUM_PAD));
        showPageBar(pages, page);
    }

    static private void showPageBar(int pages, int page) {
        System.out.print("Pages: ");
        if (page - 2 > 1) {
            System.out.print("...");
        }
        for (int i = Math.max(pages - 2, 1); i <= Math.min(page + 2, pages); i++) {
            System.out.print(" " + i);
        }
        if (page + 2 < pages) {
            System.out.print(" ...");
        }
        System.out.println();
    }

    static private void showTopBar(int namePad, int consPad, int numPad) {
        String index = String.format("%-" + numPad + "s", "NO.");
        String host = String.format("%-" + namePad + "s", "You");
        String guest = String.format("%-" + namePad + "s", "Opponent");
        System.out.println(index + "|" + host + "|" + guest);
    }

    static private void sortHistory(String field, String type) {
        //default ascending
        switch (field) {
            case "1" -> history.sort(Comparator.comparing(History::getTime));
            case "2" -> history.sort(Comparator.comparing(History::getOpponentName));
            case "3" -> history.sort(Comparator.comparing(History::getOpponentLevel).reversed());
            case "4" -> history.sort(Comparator.comparing(History::getResult));
            default -> {
                System.out.println("Field should be a number between 1 & 4");
                return;
            }
        }
        if (type.equals("2")) {
            Collections.reverse(history);
        }
    }
}
