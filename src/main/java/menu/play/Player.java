package menu.play;

import app.Card;
import app.User;

import java.util.*;

public class Player extends User {
    private Card.Characters character;
    private ArrayList<Cell> durationLine;
    private ArrayList<Card> hand;
    private LinkedHashMap<Integer, Card> deck;
    private Integer totalAttack, durationLineSize, handSize;

    Player(User user, int durationLineSize, int handSize) {
        super(user.getUsername(), "", user.getNickname(), "", "", "", "",
                user.getWallet(), user.getLevel(), user.getId(), user.getXP(), user.getHP());
        hand = new ArrayList<>();
        this.durationLineSize = durationLineSize;
        this.handSize = handSize;
        totalAttack = 0;

        //Just for now
        //deck = user.getDeck();
        deck = user.getCards();
    }

    public void initNewRound() {
        //A new duration line
        durationLine = new ArrayList<>();
        for (int i = 0; i < durationLineSize; i++) {
            durationLine.add(new Cell());
        }

        //A new attacking score
        totalAttack = 0;

        //A new hand
        fillHand();

        //Set random cell to hollow
        setRandomCellToHollow();
    }

    public Card.Characters getCharacter() {
        return character;
    }

    public Integer getTotalAttack(){
        return totalAttack;
    }

    public void setCharacter(Card.Characters character) {
        this.character = character;
    }

    public void setRandomCellToHollow() {
        Random random = new Random();
        durationLine.get(random.nextInt(durationLineSize)).makeHollow();
    }

    private void fillHand() {
        HashSet<Integer> repeatedIds = new HashSet<>();
        hand = new ArrayList<>();
        while (hand.size() < handSize) {
            int randomId = getRandomKey(deck);
            if (!repeatedIds.contains(randomId)) {
                Card card = deck.get(randomId).clone();
                if (card.getCharacter().equals(character.name()))
                    card.boostAttackDefense(1.5);
                hand.add(card);
                repeatedIds.add(randomId);
            }
        }
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public ArrayList<Cell> getDurationLine() {
        return durationLine;
    }

    public void replaceCardInHand(int index) {
        HashSet<Integer> repeatedIds = new HashSet<>();
        for (Card card : hand) {
            repeatedIds.add(card.getId());
        }
        for (Map.Entry<Integer, Card> entry : deck.entrySet()) {
            if (!repeatedIds.contains(entry.getKey())) {
                hand.set(index, entry.getValue());
            }
        }
    }

    public void setTotalAttack(int number){
        totalAttack = number;
    }

    public void increaseTotalAttack(int number){
        totalAttack += number;
    }

    public static <K, V> K getRandomKey(HashMap<K, V> map) {
        // Convert the keys to a List
        List<K> keys = new ArrayList<>(map.keySet());

        // Generate a random index
        Random random = new Random();
        int randomIndex = random.nextInt(keys.size());

        // Retrieve the key at the random index
        return keys.get(randomIndex);
    }

    public void showDurationLine(int rightPad){
        for (int i = 0; i < durationLineSize; i++) {
            if(checkNullForPrinting(durationLine.get(i))){
                System.out.print("%-" + rightPad + durationLine.get(i).getCard().getId());
            }
            System.out.print("|");
        }
        System.out.println();
        for (int i = 0; i < durationLineSize; i++) {
            if(checkNullForPrinting(durationLine.get(i))) {
                System.out.print("%-" + rightPad + durationLine.get(i).getCard().getAcc());
            }
            System.out.print("|");
        }
        System.out.println();
        for (int i = 0; i < durationLineSize; i++) {
            if(checkNullForPrinting(durationLine.get(i))) {
                System.out.print("%-" + rightPad + durationLine.get(i).getCard().getGamingAttackOrDefense());
            }
            System.out.print("|");
        }
        System.out.println();
    }

    public void showResultPrompt(){
        System.out.println("obtained coin: " + 0.2 * getHP());
        System.out.println("obtained XP: " + 0.1 * getHP());
        System.out.println("total XP: " + getXP());
        System.out.println("required XP to next level: " + User.nextLevelXP(getLevel()));
    }

    public void checkForLevelUpgrade(){
        if (User.nextLevelXP(getLevel()) < getXP()) {
            setLevel(getLevel() + 1);
            System.out.println(getNickname() + "'s level is upgraded to " + getLevel());
        }
    }

    public void applyResults(User user){
        user.setWallet(getWallet());
        user.setXP(getXP());
        user.setLevel(getLevel());
    }

    private boolean checkNullForPrinting(Cell cell){
        if(cell.isHollow()){
            System.out.print("Hol");
            return false;
        }
        if(cell.isEmpty()){
            System.out.print("Emp");
            return false;
        }
        if(cell.getCard() == null){
            System.out.print("nul");
            return false;
        }
        return true;
    }
}
