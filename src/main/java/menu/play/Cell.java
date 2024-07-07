package menu.play;

import app.Card;
import javafx.util.Pair;

public class Cell {
    private boolean hollow = false;
    private boolean shattered = false;
    //id-card pair
    private Pair<Integer, Card> cardPair = new Pair<>(null, null);

    Cell() {
    }

    public void makeSolid() {
        hollow = false;
    }

    public void makeHollow() {
        hollow = true;
    }

    public void shatter() {
        shattered = true;
    }

    public boolean isHollow() {
        return hollow;
    }

    public boolean isEmpty() {
        return getCard() == null;
    }

    public boolean isShattered() {
        return shattered;
    }

    public void setCardPair(Pair<Integer, Card> cardPair) {
        this.cardPair = cardPair;
    }

    public Card getCard() {
        return cardPair.getValue();
    }
}
