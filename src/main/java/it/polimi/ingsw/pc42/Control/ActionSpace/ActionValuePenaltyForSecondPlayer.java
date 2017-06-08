package it.polimi.ingsw.pc42.Control.ActionSpace;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Model.Board;
import it.polimi.ingsw.pc42.Model.FamilyMember;

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
    public void performAction(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        if (doesPenaltyApply(fm)){
            fm.setValue(fm.getValue()-penalty);
            if (fm.getValue()<1){
                fm.setValue(fm.getValue()+penalty);
                throw new ActionAbortedException(false);
            }
        }
        try {
            super.performAction(move, fm);
        }catch (ActionAbortedException e){
            if (doesPenaltyApply(fm)){
                fm.setValue(fm.getValue()+penalty);
            }
            throw e;
        }
    }

    @Override
    public int getMinimumActionValue(FamilyMember fm) {
        if (doesPenaltyApply(fm)){
            return super.getMinimumActionValue(fm)+penalty;
        }else {
            return super.getMinimumActionValue(fm);
        }
    }
    /*    @Override
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
    }*/

    public boolean doesPenaltyApply(FamilyMember familyMember){
        if (!familyMember.diceColor.visible){
            return false;
        }
        return !ActionSpace.isFirstInArea(board, this.getArea());
    }
}
