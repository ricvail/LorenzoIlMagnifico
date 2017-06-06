package it.polimi.ingsw.pc42.Control.ActionSpace;

import it.polimi.ingsw.pc42.Model.FamilyMember;

public class singleFamilyMemberDecorator extends AbstractDecorator {
    public singleFamilyMemberDecorator(iActionSpace actionSpace) {
        super(actionSpace);
    }

    @Override
    public boolean canPlace(FamilyMember familyMember) {

        if (familyMember.diceColor.visible && getNumberOfVisibleFamilyMembers()>=1){
            return  false;
        }
        return super.canPlace(familyMember);
    }
}
