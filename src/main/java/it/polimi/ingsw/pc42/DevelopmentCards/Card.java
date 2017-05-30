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
    public final  JsonNode json;

    public Card(int era, String name, CardType cardType, JsonNode j){
        this.json=j;
        this.era = era;
        this.name = name;
        this.cardType = cardType;
    }

    public boolean drawRequirementCheck(Player player) {
        return true;
    }

    public void applyDrawEffect(Player player, JsonNode json) {
    }

    public void applyEndgameEffect(Player player) {
    }

    @Override
    public JsonNode getJSONDescriptionOfCards() {
        return null;
    }

    public enum CardType {
        TERRITORY("territories"), CHARACTER("characters"), BUILDING("buildings"), VENTURE("ventures");

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
