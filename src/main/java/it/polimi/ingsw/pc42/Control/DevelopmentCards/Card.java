package it.polimi.ingsw.pc42.Control.DevelopmentCards;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Control.ActionSpace.iActionSpace;
import it.polimi.ingsw.pc42.Model.Board;
import it.polimi.ingsw.pc42.Model.FamilyMember;
import it.polimi.ingsw.pc42.Model.Player;
import it.polimi.ingsw.pc42.Utilities.BoardProvider;

public class Card implements iCard{

    public final int era;
    public final String name;
    public final CardType cardType;
    public final  JsonNode json;
    private BoardProvider bp;
    private int actionValue;

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

    @Override
    public int getActionValue() {
        return actionValue;
    }
    public void setActionValue(int i){
        actionValue=i;
    }

    /**
     * Class constructor. Initializes the base card that needs to be decorated.
     *
     * @param era card era
     * @param name card name
     * @param cardType type of the card (Enum value)
     * @param j higher node of the single card object in the JSON file
     * @param bp board provider, a wrapper for a board object
     */
    public Card(int era, String name, CardType cardType, JsonNode j, BoardProvider bp){
        this.json=j;
        this.era = era;
        this.name = name;
        this.cardType = cardType;
        this.bp=bp;
        actionValue=0;
    }


    @Override
    public void undoDrawCard(JsonNode move, FamilyMember fm) {

    }

    @Override
    public void drawCard(JsonNode move, FamilyMember fm) throws ActionAbortedException {

    }

    @Override
    public void applyEndgameEffect(Player player) {
    }

    @Override
    public JsonNode getJSONDescriptionOfCards() {
        return json;
    }

    @Override
    public void onHarvest(JsonNode move, FamilyMember fm) throws ActionAbortedException {

    }

    @Override
    public void undoOnHarvest(JsonNode move, FamilyMember fm) throws ActionAbortedException {

    }

    @Override
    public void onProduction(JsonNode move, FamilyMember fm) throws ActionAbortedException {

    }

    @Override
    public void undoOnProduction(JsonNode move, FamilyMember fm) throws ActionAbortedException {

    }

    @Override
    public void onAction(JsonNode move, FamilyMember fm, iActionSpace space){

    }

    @Override
    public void undoOnAction(JsonNode move, FamilyMember fm, iActionSpace space) {

    }

    public enum CardType {

        TERRITORY("territories"), CHARACTER("characters"), BUILDING("buildings"), VENTURE("ventures");

        private String cardType;

        public String getString(){
            return cardType;
        }

        /**
         *  Enum constructor. Set the string attribute according to the parameter.
         *
         * @param cardType card type string that needs the "check"
         */
        CardType(String cardType){
            this.cardType = cardType;
        }

        /**
         * Returns a card type, if it finds a match for the string passed as parameter,
         * iterating over the Enum values, else throws exception.
         *
         * @param s card type string that needs the "check"
         * @return a card type value, if matches the parameter
         * @throws Exception if the string passed represent an invalid card type
         */
        public static CardType fromString(String s) throws Exception {
            for (CardType cardType : CardType.values()) {
                if (cardType.getString().equalsIgnoreCase(s)) {
                    return cardType;
                }
            }
            throw new Exception("Invalid card type: "+ s);
        }

    }
}
