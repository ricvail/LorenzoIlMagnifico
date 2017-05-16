package it.polimi.ingsw.pc42.ActionSpace;


import it.polimi.ingsw.pc42.FamilyMember;

import java.util.ArrayList;

public class ActionSpace extends AbstractActionSpace {
    private ArrayList<FamilyMember> familyMembers;
    private Area area;

    public ActionSpace(Area area){
        this.area = area;
    }

    @Override
    boolean canPlace(FamilyMember familyMember) {
        return true;
    }

    @Override
    void placeFamilyMember(FamilyMember familyMember) {

    }

    @Override
    void cleanup() {

    }

    @Override
    ArrayList<FamilyMember> getFamilyMembers() {
        return familyMembers;
    }

    @Override
    Area getArea() {
        return area;
    }
}
