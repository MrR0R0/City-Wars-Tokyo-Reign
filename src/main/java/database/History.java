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
                self + "|" + opponent + "|" + padAndTruncate(result, 20) + "|" + time + "|" +
                String.format("%-"+consPad+"s", "Yours: " + userCons) +
                String.format("%-"+consPad+"s", "Opponent's: " + oppCons)
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

    public static String padAndTruncate(String str, int length) {
        if (str.length() > length) {
            if (length < 3) {
                throw new IllegalArgumentException("Length must be at least 3 to accommodate '...'");
            }
            return str.substring(0, length - 3) + "...";
        } else {
            return String.format("%-" + length + "s", str);
        }
    }
}
