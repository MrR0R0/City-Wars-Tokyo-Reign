import app.User;
import java.sql.*;
import java.util.ArrayList;


public class Connect {
    private static final String DB_URL = "jdbc:sqlite:identifier.sqlite";
    private static Connection connection;

    public static void connectToDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void insertUser(String user_username, String user_cards, String user_password, String user_nickname, String user_email,
                                  String user_recoverQuestion, String user_recoveryAnswer, Integer user_wallet) {
        String sql = "INSERT INTO user(user_username, user_cards, user_password, user_nickname, user_email, "
                + "user_recoverQuestion, user_recoveryAnswer, user_wallet) VALUES(?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, user_username);
            pstmt.setString(2, user_cards);
            pstmt.setString(3, user_password);
            pstmt.setString(4, user_nickname);
            pstmt.setString(5, user_email);
            pstmt.setString(6, user_recoverQuestion);
            pstmt.setString(7, user_recoveryAnswer);
            pstmt.setInt(8, user_wallet);
            pstmt.executeUpdate();
            System.out.println("user has been added to the database.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static ArrayList<User> getUsers() {
        String sql = "SELECT * FROM user";
        ArrayList<User> users = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
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

                users.add(user);
            }
            System.out.println("User has been retrieved and added to the ArrayList.");
            return users;
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
