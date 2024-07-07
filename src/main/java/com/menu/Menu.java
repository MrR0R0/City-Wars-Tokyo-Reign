package com.menu;

import com.app.User;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Menu extends com.app.Error {
    public enum MenuType {Profile, Main, Shop, Authentication, Play, Admin}
    public static boolean isInMenu = false;
    public static User loggedInUser = new User();
    public static MenuType currentMenu = MenuType.Authentication;
    public final static String backCommand = "^(?i)back$";

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
    public static void showCurrentMenu(){
        System.out.println("you are in " + currentMenu.name() + " menu");
    }
    public static <T extends Enum<T>> T getEnumValueByIndex(Class<T> enumClass, int index) {
        T[] enumConstants = enumClass.getEnumConstants();
        if (index >= 0 && index < enumConstants.length) {
            return enumConstants[index];
        } else {
            return null;
        }
    }
}
