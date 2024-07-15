package app;

public class Help {
    public static void authentication() {
        System.out.println("Authentication Menu");
        int padLogin = 60;
        System.out.println("-".repeat(padLogin + 1));
        System.out.println(padAndCat("LOGIN COMMANDS", padLogin));
        System.out.println(padAndCat("", padLogin));
        System.out.println(padAndCat("-Login command: login -u #username -p #password", padLogin));
        System.out.println(padAndCat("    Ex: login -u firstUser -p VerySecure!!!", padLogin));
        System.out.println(padAndCat("-Forgot password command: forgot password -u #username", padLogin));
        System.out.println(padAndCat("    Ex: forgot password -u firstUser", padLogin));
        System.out.println(padAndCat("-Admin: login admin #password", padLogin));

        int padSignup = 95;
        System.out.println("-".repeat(padSignup + 1));
        System.out.println(padAndCat("SIGNUP COMMANDS", padSignup));
        System.out.println(padAndCat("", padSignup));
        System.out.println(padAndCat("-Signup: create -u #username -p #password #confirmation -email #email -n #nickname", padSignup));
        System.out.println(padAndCat("    Ex: Signup: create -u firstUser -p VerySecure! VerySecure! -email test@gmail.com -n first", padSignup));
        System.out.println(padAndCat("-Signup with random password: create -u #username -p random -email #email -n #nickname", padSignup));
        System.out.println(padAndCat("    Ex: Signup: create -u firstUser -p random -email test@gmail.com -n first", padSignup));
        System.out.println("-".repeat(padSignup));
    }

    public static void main() {
        int pad = 40;
        System.out.println("Main Menu");
        System.out.println("-".repeat(pad));
        System.out.println(padAndCat("-To play: go to play", pad));
        System.out.println(padAndCat("-To shop: go to shop", pad));
        System.out.println(padAndCat("-To profile: go to profile", pad));
        System.out.println(padAndCat("-To see history: show history", pad));
        System.out.println(padAndCat("-Show cards: show cards", pad));
        System.out.println(padAndCat("-To logout: log out", pad));
        System.out.println("-".repeat(pad));
    }

    public static void history() {
        int pad = 90;
        System.out.println("History");
        System.out.println("-".repeat(pad));
        System.out.println(padAndCat("-Sort match history: sort by #field_name #asc_des", pad));
        System.out.println(padAndCat("    field options: 1-date 2-opponent's name 3-opponent's level 4-result", pad));
        System.out.println(padAndCat("    order options: 1-asc 2-des", pad));
        System.out.println(padAndCat("    Ex: \"sort by 1 1\" will sort by date in ascending order", pad));
        System.out.println(padAndCat("-For viewing other pages enter the page's number", pad));
        System.out.println(padAndCat("-To main menu: back", pad));
        System.out.println("-".repeat(pad));
    }

    public static void shop() {
        int pad = 50;
        System.out.println("Shop Menu");
        System.out.println("-".repeat(pad));
        System.out.println(padAndCat("-To see upgradable cards: show upgradable cards", pad));
        System.out.println(padAndCat("-To see available cards: show purchasable cards", pad));
        System.out.println(padAndCat("-To see balance: show wallet", pad));
        System.out.println(padAndCat("-Main menu: back", pad));
        System.out.println("-".repeat(pad));
    }

    public static void upgradeCards() {
        int pad = 50;
        System.out.println("Upgrade Menu");
        System.out.println("-".repeat(pad));
        System.out.println(padAndCat("-For viewing other pages enter the page's number", pad));
        System.out.println(padAndCat("-To upgrade: upgrade card #index", pad));
        System.out.println(padAndCat("-See info: show properties of #index", pad));
        System.out.println(padAndCat("-Shop menu: back", pad));
        System.out.println("-".repeat(pad));

    }

    public static void buyCards() {
        int pad = 50;
        System.out.println("Purchase Menu");
        System.out.println("-".repeat(pad));
        System.out.println(padAndCat("-To buy: buy card #index", pad));
        System.out.println(padAndCat("-See info: show properties of #index", pad));
        System.out.println(padAndCat("-Shop menu: back", pad));
        System.out.println("-".repeat(pad));

    }

    public static void profile() {
        int pad = 75;
        System.out.println("Profile Menu");
        System.out.println("-".repeat(pad));
        System.out.println(padAndCat("-To see info: Show information", pad));
        System.out.println(padAndCat("-To change username: change -u #new_username", pad));
        System.out.println(padAndCat("-To change nickname: change -n #new_nickname", pad));
        System.out.println(padAndCat("-To change email: change -n #new_email", pad));
        System.out.println(padAndCat("-To change password: change password -o #old_password -n #new_password", pad));
        System.out.println(padAndCat("-Main menu: back", pad));
        System.out.println("-".repeat(pad));
    }

    public static void play() {
        int pad = 75;
        System.out.println("Play Menu");
        System.out.println("-".repeat(pad));
        System.out.println(padAndCat("-To choose a play mode: select [Betting-Normal] as the play mode", pad));
        System.out.println(padAndCat("-Second player login: login -u #username -p #password", pad));
        System.out.println(padAndCat("-To see your hand: show hand", pad));
        System.out.println(padAndCat("-To see duration line: show duration line", pad));
        System.out.println(padAndCat("-To place card: place card #card_index_in_hand in block #cell_index", pad));
        System.out.println(padAndCat("***Once the match commences, there is no \"help\" option", pad));
        System.out.println("-".repeat(pad));
    }


    private static String padAndCat(String str, int length) {
        return String.format("%-" + length + "s", str) + "|";
    }
}
