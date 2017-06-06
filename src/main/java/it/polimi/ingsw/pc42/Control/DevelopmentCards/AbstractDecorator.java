package it.polimi.ingsw.pc42.Control.DevelopmentCards;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Model.Board;
import it.polimi.ingsw.pc42.Model.Player;

public abstract class AbstractDecorator implements iCard{

    protected iCard card;

    public AbstractDecorator(iCard c){
        card=c;
    }

    public boolean drawRequirementCheck(Player player) {
        return card.drawRequirementCheck(player);
    }

    @Override
    public JsonNode getJSONDescriptionOfCards() {
        JsonNode json = card.getJSONDescriptionOfCards();
        return json;
    }

    @Override
    public Board getBoard() {
        return card.getBoard();
    }

    public void applyDrawEffect(Player player, JsonNode json) {
        card.applyDrawEffect(player, json);
    }

    public void applyEndgameEffect(Player player) {
        card.applyEndgameEffect(player);
    }

    public int getEra() {
        return card.getEra();
    }

    public String getName() {
        return card.getName();
    }

    public Card.CardType getCardType() {
        return card.getCardType();
    }

}
