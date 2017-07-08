package it.polimi.ingsw.pc42.Control.DevelopmentCards;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Model.Board;
import it.polimi.ingsw.pc42.Model.FamilyMember;
import it.polimi.ingsw.pc42.Model.Player;

public abstract class AbstractDecorator implements iCard{

    protected iCard card;

    /**
     * Class constructor. It is needed to initialize the card field, from a subclass, before every other decoration.
     *
     * @param c card to be decorated
     */
    public AbstractDecorator(iCard c){
        card=c;
    }

    @Override
    public void drawCard(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        card.drawCard(move, fm);
    }

    @Override
    public void undoDrawCard(JsonNode move, FamilyMember fm) {
        card.undoDrawCard(move, fm);
    }

    @Override
    public void onHarvest(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        card.onHarvest(move, fm);
    }

    @Override
    public int getActionValue() {
        return card.getActionValue();
    }

    @Override
    public void undoOnHarvest(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        card.undoOnHarvest(move, fm);
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
    @Override
    public void applyEndgameEffect(Player player) {
        card.applyEndgameEffect(player);
    }

    @Override
    public int getEra() {
        return card.getEra();
    }

    @Override
    public String getName() {
        return card.getName();
    }

    @Override
    public Card.CardType getCardType() {
        return card.getCardType();
    }

}
