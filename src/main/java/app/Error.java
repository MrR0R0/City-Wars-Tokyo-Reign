package app;

public class Error {
    static public boolean emptyField(String input, String fieldName){
        if(input.isEmpty()){
            System.out.println("The field \"" + fieldName + "\" is empty!");
        }
        return input.isEmpty();
    }
}
