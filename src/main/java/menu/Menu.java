package menu;

import app.User;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Menu extends app.Error {
    public static boolean isInMenu = false;

    public static User loggedInUser = new User();

    public static boolean isLoggedIn(){
        return !(loggedInUser == null);
    }
    public static Matcher getCommandMatcher(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(input);
    }
}
