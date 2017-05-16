package it.polimi.ingsw.pc42.ActionSpace;

import it.polimi.ingsw.pc42.FamilyMember;

import java.util.ArrayList;

public abstract class AbstractActionSpace {

    abstract boolean canPlace(FamilyMember familyMember);
    abstract void placeFamilyMember(FamilyMember familyMember);
    abstract void cleanup();
    abstract ArrayList<FamilyMember> getFamilyMembers();
    abstract Area getArea();
}
