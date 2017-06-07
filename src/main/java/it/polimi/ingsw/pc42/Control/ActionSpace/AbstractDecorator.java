package it.polimi.ingsw.pc42.Control.ActionSpace;


import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Model.Board;
import it.polimi.ingsw.pc42.Model.FamilyMember;

import java.util.ArrayList;

public abstract class AbstractDecorator implements iActionSpace {
    private iActionSpace actionSpace;

    @Override
    public int getMinimumActionValue() {
        return actionSpace.getMinimumActionValue();
    }

    @Override
    public int getMinimumNumberOfPlayers() {
        return actionSpace.getMinimumNumberOfPlayers();
    }

    public AbstractDecorator(iActionSpace actionSpace){
        this.actionSpace = actionSpace;
    }

    public boolean canPlace(FamilyMember familyMember){
        return actionSpace.canPlace(familyMember);
    }

    public void placeFamilyMember(FamilyMember familyMember, JsonNode json){
        actionSpace.placeFamilyMember(familyMember, json);
    }
    public void cleanup(){
        actionSpace.cleanup();
    }
    public ArrayList<FamilyMember> getFamilyMembers(){
        return actionSpace.getFamilyMembers();
    }
    public Area getArea(){
        return actionSpace.getArea();
    }

    @Override
    public int getID() {
        return actionSpace.getID();
    }

    @Override
    public int getNumberOfVisibleFamilyMembers() {
        return actionSpace.getNumberOfVisibleFamilyMembers();
    }

    @Override
    public Board getBoard() {
        return actionSpace.getBoard();
    }
}