package it.polimi.ingsw.pc42.DevelopmentCards;

import com.fasterxml.jackson.databind.JsonNode;
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

    public Card(CardType cardType){
        this.cardType=cardType;
    }

    public boolean drawRequirementCheck(Player player) {
        return true;
    }

    public void applyDrawEffect(Player player, JsonNode json) {
    }

    public void applyEndgameEffect(Player player) {
    }


    public enum CardType {
        TERRITORY("territory"), CHARACTER("character"), BUILDING("building"), VENTURE("venture");

        private String cardType;
        CardType(String cardType){
            this.cardType = cardType;
        }

        public String getString(){
            return cardType;
        }

        public static CardType fromString(String s){
            for (CardType cardType : CardType.values()) {
                if (cardType.getString().equalsIgnoreCase(s)) {
                    return cardType;
                }
            }
            throw new IllegalArgumentException(); //TODO more specific exception
        }

    }
}
