package it.polimi.ingsw.pc42.Control.ActionSpace;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Model.Board;
import it.polimi.ingsw.pc42.Model.Dice;
import it.polimi.ingsw.pc42.Model.FamilyMember;
import it.polimi.ingsw.pc42.Model.Player;

public class ActionValuePenaltyForSecondPlayer extends AbstractDecorator {
    Board board;
    int penalty;
    FamilyMember dummyOne, dummyTwo;

    /**
     * Class constructor. Decorates an action space with an action value penalty for the second family member placed.
     * TODO dummy one e two?
     * @param penalty value of the penalty
     * @param actionSpace action space on which this penalty will be added
     */
    public ActionValuePenaltyForSecondPlayer(int penalty, iActionSpace actionSpace) {
        super(actionSpace);
        this.penalty=penalty;
        dummyOne= new FamilyMember(Player.createPlayer(Player.PlayerColor.RED), Dice.DiceColor.ORANGE);
        dummyTwo= new FamilyMember(Player.createPlayer(Player.PlayerColor.GREEN), Dice.DiceColor.WHITE);
    }

    @Override
    public void performAction(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        if (doesPenaltyApply(fm)){
            if (getBoard().getPlayerArrayList().size()<3){
                throw new ActionAbortedException(false, "Unable to perform this move if there are less than 3 players.");
            }
            fm.setValue(fm.getValue()-penalty);
            if (fm.getValue()<1){
                fm.setValue(fm.getValue()+penalty);
                throw new ActionAbortedException(false, "Family Member action value, with the penalty applied, is less than one");
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
    public void undoAction(JsonNode move, FamilyMember fm) {
        if (doesPenaltyApply(fm)){
            fm.setValue(fm.getValue()+penalty);
        }
        super.undoAction(move, fm);
    }

    @Override
    public int getMinimumActionValue(FamilyMember fm) {
        if (doesPenaltyApply(fm)){
            return super.getMinimumActionValue(fm)+penalty;
        }else {
            return super.getMinimumActionValue(fm);
        }
    }

    @Override
    public ObjectNode updateDescription(ObjectNode node) {
        if (doesPenaltyApply(dummyOne)||doesPenaltyApply(dummyTwo)){
            node.put("locked", true);
        } else {
            node.put("locked", false);
        }
        return super.updateDescription(node);
    }

    //TODO cancellare?
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

    /**
     * Checks if the penalty has to be applied to the family member that will be placed.
     *
     * @param familyMember family member to be placed
     * @return <code>true</code> if the penalty applies
     */
    public boolean doesPenaltyApply(FamilyMember familyMember){
        if (!familyMember.diceColor.visible){
            return false;
        }
        return !ActionSpace.isFirstInArea(getBoard(), this.getArea(), familyMember);
    }
}
