package it.polimi.ingsw.pc42.Control.ActionSpace;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Model.Board;
import it.polimi.ingsw.pc42.Model.FamilyMember;

import java.util.ArrayList;

public interface iActionSpace {

    void undoAction (JsonNode move, FamilyMember fm);
    void performAction(JsonNode move, FamilyMember fm) throws ActionAbortedException;
    void cleanup();
    ArrayList<FamilyMember> getFamilyMembers();
    Area getArea();
    int getID();
    int getNumberOfVisibleFamilyMembers(FamilyMember fm);
    Board getBoard();
    int getMinimumActionValue(FamilyMember fm);
    int getMinimumNumberOfPlayers();

}
