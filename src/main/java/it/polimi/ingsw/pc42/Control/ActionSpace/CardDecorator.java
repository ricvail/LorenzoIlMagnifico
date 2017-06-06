package it.polimi.ingsw.pc42.Control.ActionSpace;

import it.polimi.ingsw.pc42.Model.Board;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.Card;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.iCard;
import it.polimi.ingsw.pc42.Model.FamilyMember;

/**
 * Created by RICVA on 22/05/2017.
 */
public class CardDecorator extends AbstractDecorator{

    Card.CardType type;
    Board board;

    private iCard card;

    private boolean empty;

    public CardDecorator(Card.CardType type, Board board, iActionSpace actionSpace) {
        super(actionSpace);
        this.type=type;
        this.board=board;
        empty=true;
    }

    @Override
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
    }

    @Override
    public void cleanup() {
        super.cleanup();
        empty=true;
        try {
            this.card=board.getCard(type);
            empty=false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
