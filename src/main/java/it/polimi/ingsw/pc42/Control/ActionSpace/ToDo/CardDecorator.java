package it.polimi.ingsw.pc42.Control.ActionSpace.ToDo;

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

/*    @Override
    public boolean canPlace(FamilyMember familyMember) {
        boolean b= super.canPlace(familyMember);
        if (familyMember.owner.getNumberOfCards(type)>6){
            return false;
        }
        if (empty){
            return false;
        }
        if (!card.drawRequirementCheck(familyMember.owner)){
            return false;
        }
        return b;
    }*/

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
