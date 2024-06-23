public class ProgramController {
    public void run(){
        String createUserRegular = "^user create -u (?<Username>.+) -p (?<Pass>.+) (?<PassConfirm>.+)" +
                " –email (?<Email>.+) -n (?<Nickname>.+)$";
        String createUserRandom = "^user create -u (?<Username>.+) -p random" +
                " –email (?<Email>.+) -n (?<Nickname>.+)$";
        String pickQuestion = "^question pick -q (?<QNumber>.+) -a (?<Ans>.+) -c (?<Confirm>.+)$";
    }
}
