package database;

import app.Card;
import app.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;


public class Connect {
    //Current file: jdbc:sqlite:C:\Users\Mahdi\Downloads\history.db
    private static final String DB_URL = "jdbc:sqlite:identifier.sqlite";

    private static Connection connection;

    public static void connectToDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            //System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void insertUser(User user) throws SQLException {
        String sql = "INSERT INTO user(user_level, user_username, user_cards, user_password, user_nickname, user_email, "
                + "user_recoveryQuestion, user_recoveryAnswer, user_wallet) VALUES(?,?,?,?,?,?,?,?,?)";
        try {
            connectToDatabase();
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, user.getLevel());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getCardsSeries());
            pstmt.setString(4, user.getPassword());
            pstmt.setString(5, user.getNickname());
            pstmt.setString(6, user.getEmail());
            pstmt.setInt(7, user.getRecoveryQ());
            pstmt.setString(8, user.getRecoveryAns());
            pstmt.setInt(9, user.getWallet());
            pstmt.executeUpdate();
            //System.out.println("user has been added to the database.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            connection.close();
        }
    }

    public static LinkedHashMap<Integer, User> getUsers() throws SQLException {
        String sql = "SELECT * FROM user";
        LinkedHashMap<Integer, User> userMap = new LinkedHashMap<>();
        try {
            connectToDatabase();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setLevel(rs.getInt("user_level"));
                user.setCardsSeries(rs.getString("user_cards"));
                user.setUsername(rs.getString("user_username"));
                user.setPassword(rs.getString("user_password"));
                user.setNickname(rs.getString("user_nickname"));
                user.setEmail(rs.getString("user_email"));
                user.setRecoveryQ(rs.getString("user_recoveryQuestion"));
                user.setRecoveryAns(rs.getString("user_recoveryAnswer"));
                user.setWallet(rs.getInt("user_wallet"));
                user.setID(rs.getInt("user_id"));
                userMap.put(rs.getInt("user_id"), user);
            }
            //System.out.println("Users has been retrieved and added to the LinkedHashMap.");
            return userMap;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        } finally {
            connection.close();
        }
    }

    public static LinkedHashMap<Integer, Card> getCards() throws SQLException {
        connectToDatabase();
        String sql = "SELECT * FROM card";
        LinkedHashMap<Integer, Card> cardMap = new LinkedHashMap<>();
        try {
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
                card.setType(Card.CardType.valueOf(rs.getString("card_type")));
                card.setId(rs.getInt("card_id"));
                card.setAttackOrDefense(rs.getInt("card_attackOrDefense"));
                card.setSpecialProperty(rs.getInt("card_specialProperty"));
                card.setCharacter(String.valueOf(convertCharacterType(rs.getInt("card_character"))));

                cardMap.put(rs.getInt("card_id"), card);
                //System.out.println("Cards has been retrieved and added to the LinkedHashMap.");
            }
            return cardMap;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        } finally {
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
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
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
        } catch (SQLException e) {
            System.out.println("Something went wrong when writing in SQL table");
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }

    //Getting user's match history
    public static ArrayList<History> getUserHistory(String userID) throws SQLException {
        String query = "SELECT * FROM history WHERE host_id = ? OR guest_id = ?";
        int counter = 1;
        ArrayList<History> historyArray = new ArrayList<>();
        try {
            connectToDatabase();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, userID);
            pstmt.setString(2, userID);
            ResultSet resultSet = pstmt.executeQuery();
            ArrayList<Integer> self_opponent_ID = getOpponent(
                    Integer.parseInt(userID),
                    Integer.parseInt(resultSet.getString("host_id")),
                    Integer.parseInt(resultSet.getString("guest_id"))
            );
            while (resultSet.next()) {
                History tmpHist = new History(
                        counter,
                        User.signedUpUsers.get(self_opponent_ID.get(0)).getUsername(),
                        resultSet.getString("host_level"),
                        User.signedUpUsers.get(self_opponent_ID.get(1)).getUsername(),
                        resultSet.getString("guest_level"),
                        resultSet.getString("result"),
                        resultSet.getString("time"),
                        resultSet.getString("host_cons"),
                        resultSet.getString("guest_cons")
                );
                historyArray.add(tmpHist);
                counter++;
            }
        } catch (SQLException e) {
            System.out.println("Something went wrong when writing in SQL table");
            e.printStackTrace();
        } finally {
            connection.close();
            Collections.reverse(historyArray);
        }
        return historyArray;
    }

    // Converting between character strings and integers.
    public static <T> T convertCharacterType(T var) {
        if (var instanceof String) {
            Card.Characters character = Card.Characters.valueOf((String) var);
            switch (character) {
                case Character1 -> {
                    return (T) "1";
                }
                case Character2 -> {
                    return (T) "2";
                }
                case Character3 -> {
                    return (T) "3";
                }
                case Character4 -> {
                    return (T) "4";
                }
                case Unity -> {
                    return (T) "0";
                }
                default -> {
                    return null;
                }
            }
        }
        if (var instanceof Integer) {
            return switch ((Integer) var) {
                case 1 -> (T) String.valueOf(Card.Characters.Character1);
                case 2 -> (T) String.valueOf(Card.Characters.Character2);
                case 3 -> (T) String.valueOf(Card.Characters.Character3);
                case 4 -> (T) String.valueOf(Card.Characters.Character4);
                case 0 -> (T) String.valueOf(Card.Characters.Unity);
                default -> throw new IllegalStateException("Unexpected value: " + var);
            };
        }
        return null;
    }

    private static void rewriteUsers() {
        try {
            String sql = "DELETE FROM user";
            connectToDatabase();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();
            for (User user : User.signedUpUsers.values()) {
                user.addToTable();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateDatabase() {
        rewriteUsers();
    }

    //this function will return the player's and their opponent's name
    //[player_name, opponent_name]
    private static ArrayList<Integer> getOpponent(Integer selfID, Integer id1, Integer id2){
        if(selfID.equals(id1)){
            return new ArrayList<>(Arrays.asList(id1, id2));
        }
        return new ArrayList<>(Arrays.asList(id2, id1));
    }
}