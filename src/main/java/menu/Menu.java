package menu;

import app.User;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Menu extends app.Error {
    public enum MenuType {Profile, Main, Shop, Authentication, Play, Admin}
    public static boolean isInMenu = false;
    public static User loggedInUser = new User();
    public static MenuType currentMenu = MenuType.Authentication;

    public static boolean isLoggedIn(){
        return !(loggedInUser.getUsername() == null);
    }
    public static Matcher getCommandMatcher(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(input);
    }
    public static void logOut(){
        loggedInUser = new User();
        currentMenu = MenuType.Authentication;
    }
}
