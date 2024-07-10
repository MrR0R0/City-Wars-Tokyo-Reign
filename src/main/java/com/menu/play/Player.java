package com.menu.play;

import com.app.Card;
import com.app.User;

import java.util.*;

import static java.lang.Math.max;

public class Player extends User {
    private Card.Characters character;
    private ArrayList<Cell> durationLine;
    private ArrayList<Card> hand;
    private final LinkedHashMap<Integer, Card> deck;
    private Integer roundAttack, totalAttack, obtainedCoins = 0;
    private final Integer durationLineSize, handSize;
    private String consequence;

    Player(User user, int durationLineSize, int handSize) {
        super(user.getUsername(), "", user.getNickname(), "", "", "", "", user.getWallet(), user.getLevel(), user.getId(), user.getXP());
        hand = new ArrayList<>();
        this.durationLineSize = durationLineSize;
        this.handSize = handSize;
        setXP(user.getXP());
        roundAttack = 0;
        totalAttack = 0;

        //setHP(4 * (90 + 10 * getLevel()));
        setHP(50);

        deck = user.getCards();
    }

    public void initNewRound() {
        //A new duration line
        durationLine = new ArrayList<>();
        for (int i = 0; i < durationLineSize; i++) {
            durationLine.add(new Cell());
        }

        roundAttack = 0;

        //A new hand
        fillHand();

        //Set random cell to hollow
        setRandomCellToHollow();
    }

    public Card.Characters getCharacter() {
        return character;
    }

    public Integer getRoundAttack() {
        return roundAttack;
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
        int specialCardCounter = 0;
        hand = new ArrayList<>();
        while (hand.size() < handSize) {
            int randomId = getRandomKey(deck);
            if (!repeatedIds.contains(randomId)) {
                if(specialCardCounter < handSize/2 || deck.get(randomId).getDuration() != 0){
                    Card card = deck.get(randomId).clone();
                    if (card.getCharacter().equals(character.name())) {
                        card.boostAttackDefense(1.5);
                    }
                    hand.add(card);
                    repeatedIds.add(randomId);
                    if(card.getDuration() == 0){
                        specialCardCounter++;
                    }
                }
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
        if(hand.size() > handSize){
            hand.remove(index);
            return;
        }
        HashSet<Integer> repeatedIds = new HashSet<>();
        int specialCardCounter = 0;
        for (Card card : hand) {
            repeatedIds.add(card.getId());
            if (card.getDuration() == 0) {
                specialCardCounter++;
            }
        }
        List<Map.Entry<Integer, Card>> shuffledEntries = new ArrayList<>(deck.entrySet());
        Collections.shuffle(shuffledEntries);
        for (Map.Entry<Integer, Card> entry : shuffledEntries) {
            if (!repeatedIds.contains(entry.getKey())) {
                if (entry.getValue().getDuration() != 0 || specialCardCounter < 3) {
                    hand.set(index, entry.getValue());
                }
            }
        }
    }

    public void increaseRoundAttack(int number) {
        roundAttack += number;
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

    public void showDurationLine(int rightPad) {
        String format = "%1$-" + rightPad + "s";
        for (int i = 0; i < durationLineSize; i++) {
            if (checkNullForPrinting(durationLine.get(i))) {
                System.out.printf(format, durationLine.get(i).getCard().getId());
            }
            System.out.print("|");
        }
        System.out.println();
        for (int i = 0; i < durationLineSize; i++) {
            if (checkNullForPrinting(durationLine.get(i))) {
                if(durationLine.get(i).getCard().isBreakable()) {
                    System.out.printf(format, durationLine.get(i).getCard().getAcc());
                }
                else{
                    System.out.printf(format, "inf");
                }
            }
            System.out.print("|");
        }
        System.out.println();
        for (int i = 0; i < durationLineSize; i++) {
            if (checkNullForPrinting(durationLine.get(i))) {
                System.out.printf(format, durationLine.get(i).getCard().getGamingAttackOrDefense());
            }
            System.out.print("|");
        }
        System.out.println();
    }

    public void updateTotalAttack(){
        totalAttack += roundAttack;
    }

    public void applyPostMatchUpdates(Play.Mode mode, boolean isWinner, int pot) {
        switch (mode){
            case Normal -> obtainedCoins += (int) (max(0.1 * getHP(), 0) + totalAttack * 0.1);
            case Betting -> obtainedCoins += isWinner ? pot : 0;
        }
        int obtainedXP = (int) (max(0.2 * getHP(), 0) + totalAttack * 0.2);
        increaseXP(obtainedXP);
        increaseMoney(obtainedCoins);
        consequence = "XP: +" + obtainedXP + " Coins: +" + obtainedCoins;
        System.out.println("obtained coin: " + obtainedCoins);
        System.out.println("Total dealt damage: " + totalAttack);
        System.out.println("obtained XP: " + obtainedXP);
        System.out.println("total XP: " + getXP());
        System.out.println("required XP to next level: " + User.nextLevelXP(getLevel()));
    }

    public String getConsequence() {
        return consequence;
    }

    public void checkForLevelUpgrade() {
        while (User.nextLevelXP(getLevel()) < getXP()) {
            setLevel(getLevel() + 1);
            System.out.println(getNickname() + "'s level is upgraded to " + getLevel());
        }
    }

    public void applyResults(User user) {
        user.setWallet(getWallet());
        user.setXP(getXP());
        user.setLevel(getLevel());
    }

    private boolean checkNullForPrinting(Cell cell) {
        if (cell.isShattered()) {
            System.out.print("***");
            return false;
        }
        if (cell.isHollow()) {
            System.out.print("HOL");
            return false;
        }
        if (cell.isEmpty()) {
            System.out.print("   ");
            return false;
        }
        if (cell.getCard() == null) {
            System.out.print("nul");
            return false;
        }
        return true;
    }

    public void showHand() {
        System.out.println(formatCards(hand, "name"));
        System.out.println(formatCards(hand, "duration"));
        System.out.println(formatCards(hand, "acc"));
        System.out.println(formatCards(hand, "attackOrDefense"));
    }

    public boolean checkCompleteShatter(Cell cell) {
        for (int i = cell.getCardInitialIndex(); i < cell.getCardInitialIndex() + cell.getCard().getDuration(); i++) {
            if (!durationLine.get(i).isShattered()) {
                return false;
            }
        }
        return true;
    }

    // Rewarding players with coins equal to half the ACC of the shattered card
    public void rewardCompleteShatter(Cell cell) {
        obtainedCoins += cell.getCard().getAcc() / 2;
        System.out.println("Rewarded with coins for shattering!");
    }

    public static String formatCards(List<Card> cards, String attributeType) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            String attributeValue = switch (attributeType) {
                case "name" -> card.getName();
                case "duration" -> "Duration: " + card.getDuration();
                case "acc" -> "ACC: " + card.getAcc();
                case "attackOrDefense" -> "Att/Def: " + card.getAttackOrDefense();
                default -> "";
            };
            sb.append(String.format("%-20s", attributeValue));
            if (i < cards.size() - 1) {
                sb.append("|");
            }
        }
        return sb.toString();
    }

    public void changeHole() {
        ArrayList<Integer> validCells = new ArrayList<>();
        boolean hasFilledHole = false;
        for (Cell cell : durationLine) {
            if (cell.isHollow() && !hasFilledHole) {
                cell.makeSolid();
                hasFilledHole = true;
            }
            if (cell.isEmpty() && !cell.isHollow()) {
                validCells.add(durationLine.indexOf(cell));
            }
        }
        if (hasFilledHole) {
            Random rand = new Random();
            int randomValidIndex = validCells.get(rand.nextInt(validCells.size()));
            durationLine.get(randomValidIndex).makeHollow();
        }
    }

    public int getInitialIndexRandomCard() {
        ArrayList<Integer> validIndices = new ArrayList<>();
        for (Cell cell : durationLine) {
            if (!cell.isEmpty() && cell.getCard().isBreakable()) {
                validIndices.add(durationLine.indexOf(cell));
            }
        }
        Random rand = new Random();
        if (!validIndices.isEmpty()) {
            int validIndex = validIndices.get(rand.nextInt(validIndices.size()));
            return durationLine.get(validIndex).getCardInitialIndex();
        }
        return -1;
    }
}
