package it.polimi.ingsw.pc42.ActionSpace;


import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Dice;
import it.polimi.ingsw.pc42.FamilyMember;

import java.util.ArrayList;
import java.util.Iterator;

public class ActionSpace implements iActionSpace {
    private ArrayList<FamilyMember> familyMembers;
    private Area area;
    private int ID;

    public ActionSpace(Area area, int ID){
        this.area = area;
        this.ID=ID;
    }

    @Override
    public boolean canPlace(FamilyMember familyMember) {
        return true;
    }

    @Override
    public void placeFamilyMember(FamilyMember familyMember, JsonNode json) {

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

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public int getNumberOfVisibleFamilyMembers() {
        int i=0;
        Iterator<FamilyMember> iterator = familyMembers.iterator();
        while (iterator.hasNext()){
            FamilyMember fm = iterator.next();
            if (fm.diceColor.visible) {
                i++;
            }

        }
        return i;
    }
}
