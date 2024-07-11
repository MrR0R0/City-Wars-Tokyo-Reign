package com.menu;

import com.app.User;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.Stack;
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

    public static void clipImage(ImageView imageView, double radius, String shape) {
        if (shape.equals("circle")) {
            double xCenter = imageView.getFitWidth() / 2;
            double yCenter = imageView.getFitHeight() / 2;
            Circle clip = new Circle(xCenter, yCenter, radius);
            imageView.setClip(clip);
        } else if (shape.equals("rectangle")) {
            Rectangle clip = new Rectangle(imageView.getFitWidth(), imageView.getFitHeight());
            clip.setArcWidth(radius);
            clip.setArcHeight(radius);
            imageView.setClip(clip);
        }
    }

    public static StackPane stackImage(ImageView imageView) {
        StackPane stackPane = new StackPane();
        stackPane.setPrefHeight(imageView.getFitHeight());
        stackPane.setPrefWidth(imageView.getFitWidth());
        stackPane.getChildren().add(imageView);
        stackPane.setAlignment(Pos.CENTER);
        return stackPane;
    }
}
