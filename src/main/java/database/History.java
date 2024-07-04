package database;

import app.User;

public class History {
    private final int index;
    private final String name, level, opponentName, opponentLevel, result, time, userCons, oppCons;
    History(int index, String name, String level, String opponentName, String opponentLevel,
            String result, String time, String userCons, String oppCons){
        this.opponentName = opponentName;
        this.opponentLevel = opponentLevel;
        this.result = result;
        this.time = time;
        this.userCons = userCons;
        this.oppCons = oppCons;
        this.index = index;
        this.name = name;
        this.level = level;
    }
    public void show(int namePad, int consPad, int numPad){
        String self = String.format("%-"+namePad+"s", User.formatUsername(name) + " ("+level+")");
        String opponent = String.format("%-"+namePad+"s", User.formatUsername(opponentName) + " ("+opponentLevel+")");
            System.out.println(
                    String.format("%-"+numPad+"s", index) + "|" +
                            self + "|" + opponent + "|" + result + "|" + time + "|" +
                            String.format("%-"+consPad+"s", userCons) +
                            String.format("%-"+consPad+"s", oppCons)
            );
    }

    public String getTime() {
        return time;
    }
    public String getOpponentLevel() {
        return opponentLevel;
    }
    public String getOpponentName() {
        return opponentName;
    }
    public String getResult() {
        return result;
    }
}
