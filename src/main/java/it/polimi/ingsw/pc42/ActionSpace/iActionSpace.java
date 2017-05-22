package it.polimi.ingsw.pc42.ActionSpace;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Board;
import it.polimi.ingsw.pc42.FamilyMember;

import java.util.ArrayList;

public interface iActionSpace {

    boolean canPlace(FamilyMember familyMember);
    void placeFamilyMember(FamilyMember familyMember, JsonNode json);
    void cleanup();
    ArrayList<FamilyMember> getFamilyMembers();
    Area getArea();
    int getID();
    int getNumberOfVisibleFamilyMembers();
    Board getBoard();
}
