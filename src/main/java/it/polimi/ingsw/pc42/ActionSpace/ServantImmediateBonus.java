package it.polimi.ingsw.pc42.ActionSpace;

import it.polimi.ingsw.pc42.FamilyMember;

public class ServantImmediateBonus extends AbstractDecorator{
    private int q;

    public ServantImmediateBonus(int quantity, AbstractActionSpace actionSpace){
        super(actionSpace);
        q = quantity;
    }

    @Override
    void placeFamilyMember(FamilyMember familyMember) {
        super.placeFamilyMember(familyMember);
        familyMember.owner.servant.add(q);
    }
}
