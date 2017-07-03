package it.polimi.ingsw.pc42.Control.ActionSpace;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Control.ActionSpace.AbstractDecorator;
import it.polimi.ingsw.pc42.Control.ActionSpace.iActionSpace;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.Card;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.iCard;
import it.polimi.ingsw.pc42.Model.FamilyMember;

/**
 * Created by RICVA on 22/05/2017.
 */
public class CardDecorator extends AbstractDecorator {


    Card.CardType type;

    private iCard card;

    private boolean empty;

    public CardDecorator(Card.CardType type, iActionSpace actionSpace) {
        super(actionSpace);
        this.type=type;
        empty=true;
    }

    @Override
    public void performAction(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        if (empty || fm.owner.getNumberOfCards(type)>=6){
            throw new ActionAbortedException(false);
        }
        if (card.getCardType()== Card.CardType.TERRITORY&&
                fm.owner.getNumberOfCards(Card.CardType.TERRITORY)>=fm.owner.getMaxNumberOfTerritories()){
            throw new ActionAbortedException(false);
        }
        empty=true;
        fm.owner.addCard(card);
        try {
            super.performAction(move, fm);
        } catch (ActionAbortedException e){
            empty=false;
            fm.owner.removeCard(card);
            throw e;
        }
        try {
            card.drawCard(move, fm);
        } catch (ActionAbortedException e){
            empty=false;
            fm.owner.removeCard(card);
            super.undoAction(move, fm);
            throw e;
        }
    }

    @Override
    public void undoAction(JsonNode move, FamilyMember fm) {
        empty=false;
        fm.owner.removeCard(card);
        card.undoDrawCard(move, fm);
        super.undoAction(move, fm);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        empty=true;
        try {
            this.card=getBoard().getCard(type);
            empty=false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
