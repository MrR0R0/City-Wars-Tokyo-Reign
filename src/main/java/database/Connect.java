package database;

import app.Card;
import app.User;

import java.sql.*;
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
    public static void insertUser(String user_username, String user_cards, String user_password, String user_nickname, String user_email,
                                  String user_recoveryQuestion, String user_recoveryAnswer, Integer user_wallet) throws SQLException {
        connectToDatabase();
        String sql = "INSERT INTO user(user_username, user_cards, user_password, user_nickname, user_email, "
                + "user_recoveryQuestion, user_recoveryAnswer, user_wallet) VALUES(?,?,?,?,?,?,?,?)";
        try {
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
        connectToDatabase();
        String sql = "SELECT * FROM user";
        LinkedHashMap<String, User> userMap = new LinkedHashMap<>();
        try {
            connection = DriverManager.getConnection(DB_URL);
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
                user.setRecoveryQ(rs.getString("user_recoveryAnswer"));
                user.setRecoveryAns(rs.getString("user_recoveryQuestion"));
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
    public static void insertCard(String name, String type, Integer level, Integer price, Integer damage, Integer duration, Integer upgradeCost, Integer attackOrDefense, Integer user_id, Integer specialProperty, Integer Acc, Integer isBreakable) throws SQLException {
        connectToDatabase();
        String sql = "INSERT INTO card(card_name, card_type, card_level, card_price, card_damage, card_duration, card_upgradeCost, card_attackOrDefense, user_id, card_specialProperty, card_Acc, card_isBreakable) "
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
            pstmt.setInt(9, user_id);
            pstmt.setInt(10, specialProperty);
            pstmt.setInt(11, Acc);
            pstmt.setInt(12, isBreakable);

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
}