package it.polimi.ingsw.pc42.Control.DevelopmentCards;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Model.Board;
import it.polimi.ingsw.pc42.Model.Player;
import it.polimi.ingsw.pc42.Utilities.BoardProvider;

public class Card implements iCard{

    public final int era;
    public final String name;
    public final CardType cardType;
    public final  JsonNode json;
    private BoardProvider bp;

    public int getEra() {
        return era;
    }

    public String getName() {
        return name;
    }

    public CardType getCardType() {
        return cardType;
    }

    @Override
    public Board getBoard() {
        return bp.getBoard();
    }


    public Card(int era, String name, CardType cardType, JsonNode j, BoardProvider bp){
        this.json=j;
        this.era = era;
        this.name = name;
        this.cardType = cardType;
        this.bp=bp;
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
        return json;
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

        public static CardType fromString(String s) throws Exception {
            for (CardType cardType : CardType.values()) {
                if (cardType.getString().equalsIgnoreCase(s)) {
                    return cardType;
                }
            }
            throw new Exception("Invalid card type: "+ s); //TODO more specific exception
        }

    }
}
