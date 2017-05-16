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
    public boolean canPlace(FamilyMember familyMember) {
        return true;
    }

    @Override
    public void placeFamilyMember(FamilyMember familyMember) {

    }

    @Override
    public void cleanup() {

    }

    @Override
    public ArrayList<FamilyMember> getFamilyMembers() {
        return familyMembers;
    }

    @Override
    public Area getArea() {
        return area;
    }
}
