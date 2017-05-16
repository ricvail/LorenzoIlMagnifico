package it.polimi.ingsw.pc42.ActionSpace;

import it.polimi.ingsw.pc42.FamilyMember;

/**
 * Created by Paolo on 16/05/2017.
 */
public class MilitaryPointsImmediateBonus extends AbstractDecorator {
    private int q;

    public MilitaryPointsImmediateBonus(int quantity, AbstractActionSpace actionSpace){
        super(actionSpace);
        q = quantity;
    }

    @Override
    void placeFamilyMember(FamilyMember familyMember) {
        super.placeFamilyMember(familyMember);
        familyMember.owner.militaryPoints.add(q);
    }
}
