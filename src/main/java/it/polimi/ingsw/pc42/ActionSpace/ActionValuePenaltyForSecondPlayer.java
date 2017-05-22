package it.polimi.ingsw.pc42.ActionSpace;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Board;
import it.polimi.ingsw.pc42.FamilyMember;
import it.polimi.ingsw.pc42.ResourceType;

import java.util.Iterator;

/**
 * Created by RICVA on 21/05/2017.
 */
public class ActionValuePenaltyForSecondPlayer extends AbstractDecorator {

    Board board;
    int penalty;


    public ActionValuePenaltyForSecondPlayer(int penalty, iActionSpace actionSpace) {
        super(actionSpace);
        this.penalty=penalty;
    }

    @Override
    public void placeFamilyMember(FamilyMember familyMember, JsonNode json) {
        if (doesPenaltyApply(familyMember)){
            familyMember.setValue(familyMember.getValue()-penalty);
        }
        super.placeFamilyMember(familyMember, json);

    }

    @Override
    public boolean canPlace(FamilyMember familyMember) {
        if (doesPenaltyApply(familyMember)) {
            familyMember.setValue(familyMember.getValue()-penalty);
            boolean b = super.canPlace(familyMember);
            familyMember.setValue(familyMember.getValue()+penalty);
            return b;
        } else {
            return super.canPlace(familyMember);
        }
    }

    public boolean doesPenaltyApply(FamilyMember familyMember){
        if (!familyMember.diceColor.visible){
            return false;
        }
        return !ActionSpace.isFirstInArea(board, this.getArea());
    }
}
