package it.polimi.ingsw.pc42.Control.ActionSpace;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Model.FamilyMember;

public class singleFamilyMemberDecorator extends AbstractDecorator {

    /**
     * Class constructor. Decorates an action space if you can't put two family member on it.
     *
     * @param actionSpace action space to be decorated
     */
    public singleFamilyMemberDecorator(iActionSpace actionSpace) {
        super(actionSpace);
    }

    @Override
    public void performAction(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        if (fm.diceColor.visible && getNumberOfVisibleFamilyMembers(fm)>=1){
            throw new ActionAbortedException(false, "Action Space occupied, you can't place a second Family Member");
        }
        super.performAction(move, fm);
    }

}
