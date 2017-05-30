package it.polimi.ingsw.pc42.ActionSpace;

import it.polimi.ingsw.pc42.Board;
import it.polimi.ingsw.pc42.DevelopmentCards.Card;
import it.polimi.ingsw.pc42.DevelopmentCards.iCard;
import it.polimi.ingsw.pc42.FamilyMember;
import it.polimi.ingsw.pc42.iBoard;

import java.util.Iterator;

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
        int era = board.getEra();
        Iterator<iCard> cardIterator = board.getCards().iterator();
        while (empty&&cardIterator.hasNext()){
            iCard card= cardIterator.next();
            if (card.getEra()==era && card.getCardType()==type){
                this.card=card;
                cardIterator.remove();
                empty=false;
            }
        }
        //if empty throw exception
    }
}
