package it.polimi.ingsw.pc42.Control.ActionSpace;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Model.FamilyMember;

public class singleFamilyMemberDecorator extends AbstractDecorator {
    public singleFamilyMemberDecorator(iActionSpace actionSpace) {
        super(actionSpace);
    }


    @Override
    public void performAction(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        if (fm.diceColor.visible && getNumberOfVisibleFamilyMembers()>=1){
            throw new ActionAbortedException(false);
        }
        super.performAction(move, fm);
    }

}
