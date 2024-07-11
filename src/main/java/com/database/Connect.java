package com.database;

import com.app.Card;
import com.app.User;
import com.controllers.HistoryModel;
import javafx.scene.image.Image;

import java.net.URI;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;


public class Connect {
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
                + "user_recoveryQuestion, user_recoveryAnswer, user_wallet, user_XP, user_profile) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
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
            pstmt.setInt(10, user.getXP());
            if(user.getProfile()!=null) {
                pstmt.setString(11, user.getProfile().getUrl());
            }
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
                user.setXP(rs.getInt("user_XP"));
                user.updateCardsByCardSeries();
                userMap.put(rs.getInt("user_id"), user);
                if (rs.getString("user_profile") != null && !rs.getString("user_profile").isEmpty() && !rs.getString("profile_img").isBlank()) {
                    URI profileImgURI = new URI(rs.getString("user_profile"));
                    Image profileImage = new Image(profileImgURI.toURL().toExternalForm());
                    user.setProfile(profileImage);
                }
            }
            //System.out.println("Users has been retrieved and added to the LinkedHashMap.");
            return userMap;
        } catch (Exception e) {
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
                card.setRarity(rs.getString("rarity"));
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
                                  Integer upgradeCost, Integer attackOrDefense, String rarity,
                                  Integer Acc, Integer isBreakable, String character) throws SQLException {
        connectToDatabase();
        String sql = "INSERT INTO card(card_name, card_type, card_level, card_price, card_damage, card_duration, card_upgradeCost, card_attackOrDefense, rarity, card_Acc, card_isBreakable, card_character) "
                + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
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
            pstmt.setString(9, rarity);
            pstmt.setInt(10, Acc);
            pstmt.setInt(11, isBreakable);
            pstmt.setInt(12, Integer.parseInt(convertCharacterType(character)));

            pstmt.executeUpdate();
//            System.out.println("card has been added to the database.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
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

    public static ArrayList<HistoryModel> getHistory(String userID) throws SQLException {
        String query = "SELECT * FROM history WHERE host_id = ? OR guest_id = ?";
        ArrayList<HistoryModel> historyArray = new ArrayList<>();
        try {
            connectToDatabase();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, userID);
            pstmt.setString(2, userID);
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                int hostId = resultSet.getInt("host_id");
                int guestId = resultSet.getInt("guest_id");
                HistoryModel tmpHist = new HistoryModel(
                        resultSet.getString("host_cons"),
                        String.valueOf(resultSet.getInt("host_level")),
                        User.signedUpUsers.get(hostId).getUsername(),
                        resultSet.getString("time"),
                        resultSet.getString("guest_cons"),
                        String.valueOf(resultSet.getInt("guest_level")),
                        User.signedUpUsers.get(guestId).getUsername(),
                        resultSet.getInt("winner_id"),
                        resultSet.getInt("loser_id")
                );
                historyArray.add(tmpHist);
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

    public static void insertHistory(String guestName, int guestLevel, String guestCons,
                                     String hostName, int hostLevel, String hostCons,
                                     String result, String time, int hostId, int guestId,
                                     int winner_id, int loser_id) {
        connectToDatabase();
        try {
            String sql = "INSERT INTO history (guest_name, guest_level, guest_cons, host_name, host_level, host_cons, result, time, host_id, guest_id, winner_id, loser_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);

            // Set parameters
            statement.setString(1, guestName);
            statement.setInt(2, guestLevel);
            statement.setString(3, guestCons);
            statement.setString(4, hostName);
            statement.setInt(5, hostLevel);
            statement.setString(6, hostCons);
            statement.setString(7, result);
            statement.setString(8, time);
            statement.setInt(9, hostId);
            statement.setInt(10, guestId);
            statement.setInt(11, winner_id);
            statement.setInt(12, loser_id);

            // Execute the insert statement
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
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