package app;

public class User {
    private String username, password, nickname, email, recoveryAns, recoveryQ;
    public User(String username, String password, String nickname, String email, String recoveryAns, String recoveryQ){
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.recoveryAns = recoveryAns;
        this.recoveryQ = recoveryQ;
    }
    public void showProperties(){
        System.out.println("Username: " + username);
        System.out.println("password: " + password);
        System.out.println("nickname: " + nickname);
        System.out.println("email: " + email);
        System.out.println("recoveryAns: " + recoveryAns);
        System.out.println("recoveryQ: " + recoveryQ);

    }
}