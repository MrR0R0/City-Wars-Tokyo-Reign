package database;

import app.Card;
import app.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;


public class Connect {
    private static final String DB_URL = "jdbc:sqlite:identifier.sqlite";

    private static Connection connection;

    public static void connectToDatabase(){
        try {
            connection = DriverManager.getConnection(DB_URL);
            //System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void insertUser(String user_username, String user_cards, String user_password, String user_nickname,
                                  String user_email, String user_recoveryQuestion, String user_recoveryAnswer,
                                  Integer user_wallet) throws SQLException {
        String sql = "INSERT INTO user(user_username, user_cards, user_password, user_nickname, user_email, "
                + "user_recoveryQuestion, user_recoveryAnswer, user_wallet) VALUES(?,?,?,?,?,?,?,?)";
        try {
            connectToDatabase();
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, user_username);
            pstmt.setString(2, user_cards);
            pstmt.setString(3, user_password);
            pstmt.setString(4, user_nickname);
            pstmt.setString(5, user_email);
            pstmt.setString(6, user_recoveryQuestion);
            pstmt.setString(7, user_recoveryAnswer);
            pstmt.setInt(8, user_wallet);
            pstmt.executeUpdate();
            //System.out.println("user has been added to the database.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        finally{
            connection.close();
        }
    }
    public static LinkedHashMap<String, User> getUsers() throws SQLException {
        String sql = "SELECT * FROM user";
        LinkedHashMap<String, User> userMap = new LinkedHashMap<>();
        try {
            connectToDatabase();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setLevel(rs.getInt("user_level"));
                user.setCards(rs.getString("user_cards"));
                user.setUsername(rs.getString("user_username"));
                user.setPassword(rs.getString("user_password"));
                user.setNickname(rs.getString("user_nickname"));
                user.setEmail(rs.getString("user_email"));
                user.setRecoveryQ(rs.getString("user_recoveryQuestion"));
                user.setRecoveryAns(rs.getString("user_recoveryAnswer"));
                user.setWallet(rs.getInt("user_wallet"));

                userMap.put(rs.getString("user_username"), user);
            }
            //System.out.println("Users has been retrieved and added to the LinkedHashMap.");
            return userMap;
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
        finally {
            connection.close();
        }
    }
    public static LinkedHashMap<Integer, Card> getCards() throws SQLException {
        connectToDatabase();
        String sql = "SELECT * FROM card";
        LinkedHashMap<Integer, Card> cardMap = new LinkedHashMap<>();
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Card card = new Card();
                card.setName(rs.getString("card_name"));
                card.setPrice(rs.getInt("card_price"));
                card.setAcc(rs.getInt("card_ACC"));
                card.setBreakable(rs.getInt("card_isBreakable"));
                card.setDamage(rs.getInt("card_damage"));
                card.setLevel(rs.getInt("card_level"));
                card.setUpgradeCost(rs.getInt("card_upgradeCost"));
                card.setDuration(rs.getInt("card_duration"));
                card.setType(rs.getString("card_type"));
                card.setId(rs.getInt("card_id"));
                card.setAttackOrDefense(rs.getInt("card_attackOrDefense"));
                card.setSpecialProperty(rs.getInt("card_specialProperty"));
                card.setCharacter(String.valueOf(convertCharacterType(rs.getInt("card_character"))));

                cardMap.put(rs.getInt("card_id"), card);
                //System.out.println("Cards has been retrieved and added to the LinkedHashMap.");
            }
            return cardMap;
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
        finally {
            connection.close();
        }
    }
    public static void insertCard(String name, String type, Integer level, Integer price, Integer damage, Integer duration,
                                  Integer upgradeCost, Integer attackOrDefense, Integer user_id, Integer specialProperty,
                                  Integer Acc, Integer isBreakable, String character) throws SQLException {
        connectToDatabase();
        String sql = "INSERT INTO card(card_name, card_type, card_level, card_price, card_damage, card_duration, card_upgradeCost, card_attackOrDefense, user_id, card_specialProperty, card_Acc, card_isBreakable, card_character) "
                + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, type);
            pstmt.setInt(3, level);
            pstmt.setInt(4, price);
            pstmt.setInt(5, damage);
            pstmt.setInt(6, duration);
            pstmt.setInt(7, upgradeCost);
            pstmt.setInt(8, attackOrDefense);
            pstmt.setInt(9, user_id);
            pstmt.setInt(10, specialProperty);
            pstmt.setInt(11, Acc);
            pstmt.setInt(12, isBreakable);
            pstmt.setInt(13, Integer.parseInt(convertCharacterType(character)));

            pstmt.executeUpdate();
//            System.out.println("card has been added to the database.");
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        finally {
            connection.close();
        }
    }

    public static void updateUserPassword(String username, String newPass) throws SQLException {
        String updateSQL = "UPDATE user SET user_password = ? WHERE user_username = ?";
        try {
            connectToDatabase();
            PreparedStatement pstmt = connection.prepareStatement(updateSQL);
            pstmt.setString(1, newPass);
            pstmt.setString(2, username);
            // Execute the update
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println("Something went wrong when writing in SQL table");
            e.printStackTrace();
        }
        finally {
            connection.close();
        }
    }
    //Getting user's match history
    public static ArrayList<String> getUserHistory(String username, int namePad, int consPad, int numPad) throws SQLException {
        String query = "SELECT * FROM history WHERE host_name = ? OR guest_name = ?";
        int counter = 1;
        ArrayList<String> historyArray = new ArrayList<>();
        try{
            connectToDatabase();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, username);
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                //host (host_level), right padded
                String host = User.formatUsername(resultSet.getString("host_name")) +
                        " (" + resultSet.getString("host_level") + ")";
                host = String.format("%-"+namePad+"s", host);
                //guest (guest_level), right padded
                String guest = User.formatUsername(resultSet.getString("guest_name")) +
                        " (" + resultSet.getString("guest_level") + ")";
                guest = String.format("%-"+namePad+"s", guest);
                String result = resultSet.getString("result");
                String time = resultSet.getString("time");
                String hostCons = resultSet.getString("host_cons");
                hostCons = String.format("%-"+consPad+"s", hostCons);
                String guestCons = resultSet.getString("guest_cons");
                guestCons = String.format("%-"+consPad+"s", guestCons);
                historyArray.add(String.format("%-"+numPad+"s", counter) + "|" + host +
                        "|" + guest + "|" + result + "|" + time + "|" + hostCons + "|" + guestCons);
                counter++;
            }
        }
        catch (SQLException e) {
            System.out.println("Something went wrong when writing in SQL table");
            e.printStackTrace();
        }
        finally {
            connection.close();
            Collections.reverse(historyArray);
        }
        return historyArray;
    }

    // Converting between character strings and integers.
    private static  <T> T convertCharacterType(T var){
        if (var instanceof String){
            Card.Characters character = Card.Characters.valueOf((String) var);
            switch (character) {
                case Character1 -> {
                    return  (T) "1";
                }
                case Character2 -> {
                    return (T) "2";
                }
                case Character3 -> {
                    return  (T) "3";
                }
                case Character4 -> {
                    return (T) "4";
                }
                case Unity -> {
                    return  (T) "0";
                }
                default -> {
                    return null;
                }
            }
        }
        if (var instanceof Integer){
            return switch ((Integer) var) {
                case 1 -> (T) String.valueOf(Card.Characters.Character1);
                case 2 -> (T) String.valueOf(Card.Characters.Character2);
                case 3 -> (T) String.valueOf(Card.Characters.Character3);
                case 4 -> (T) String.valueOf(Card.Characters.Character4);
                case 0 -> (T) String.valueOf(Card.Characters.Unity);
                default -> throw new IllegalStateException("Unexpected value: " + (Integer) var);
            };
        }
        return null;
    }
}