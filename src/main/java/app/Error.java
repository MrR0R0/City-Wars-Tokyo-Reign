package app;

import menu.Menu;

public class Error {
    static public boolean emptyField(String input, String fieldName){
        if(input.isEmpty()){
            System.out.println("The field \"" + fieldName + "\" is empty!");
        }
        return input.isEmpty();
    }

    static public boolean alreadyLoggedIn(){
        if(Menu.isLoggedIn()){
            System.out.println("You are already logged in!");
            return true;
        }
        return false;
    }
    static public boolean loginFirst(){
        if(!Menu.isLoggedIn()){
            System.out.println("You are not logged in!");
            return true;
        }
        return false;
    }

    static public boolean userRegistered(String username){
        if(!User.signedUpUsers.containsKey(User.getIdByUsername(username))){
            System.out.println("Username \"" + username + "\" does not exist!");
            return false;
        }
        return true;
    }
}
