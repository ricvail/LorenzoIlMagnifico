package it.polimi.ingsw.pc42.Control.ActionSpace;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.Card;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.iCard;
import it.polimi.ingsw.pc42.Model.FamilyMember;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class CardDecorator extends AbstractDecorator {


    Card.CardType type;
    private iCard card;
    private boolean empty;
    private Logger logger;
    /**
     * Class constructor. Decorates an action space with card that can be drew.
     *
     * @param type type of the card
     * @param actionSpace action space to be decorated
     */
    public CardDecorator(Card.CardType type, iActionSpace actionSpace) {
        super(actionSpace);
        this.type=type;
        empty=true;
        logger= LogManager.getLogger();
    }

    @Override
    public ObjectNode updateDescription(ObjectNode node) {
        if(!empty){
            node.set("card", card.getJSONDescriptionOfCards());
        } else {
            node.put("card", "None");
        }
        return super.updateDescription(node);
    }

    @Override
    public void performAction(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        if (empty || fm.owner.getNumberOfCards(type)>=6){
            throw new ActionAbortedException(false, "You already have 6 "+type.getString()+" cards or the tower's Action Space is empty");
        }
        if (card.getCardType()== Card.CardType.TERRITORY&&
                fm.owner.getNumberOfCards(Card.CardType.TERRITORY)>=fm.owner.getMaxNumberOfTerritories()){
            throw new ActionAbortedException(false, "Not enough Military Points to draw this card");
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
        card.undoDrawCard(move, fm);
        fm.owner.removeCard(card);
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
            logger.error("string",e);
        }
    }
}
