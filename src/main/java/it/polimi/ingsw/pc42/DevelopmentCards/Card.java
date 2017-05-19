package it.polimi.ingsw.pc42.DevelopmentCards;

import it.polimi.ingsw.pc42.Player;

public class Card implements iCard{
    public int getEra() {
        return era;
    }

    public String getName() {
        return name;
    }

    public CardType getCardType() {
        return cardType;
    }

    public final int era;
    public final String name;
    public final CardType cardType;

    public Card(int era, String name, CardType cardType){

        this.era = era;
        this.name = name;
        this.cardType = cardType;
    }

    public boolean drawRequirementCheck(Player player) {
        return true;
    }

    public void applyDrawEffect(Player player) {
    }

    public void applyEndgameEffect(Player player) {
    }


    public enum CardType {
        TERRITORY, CHARACTER, BUILDING, VENTURE
    }
}