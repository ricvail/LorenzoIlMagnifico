package it.polimi.ingsw.pc42.Control.ActionSpace;


import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Model.Board;
import it.polimi.ingsw.pc42.Model.FamilyMember;

import java.util.ArrayList;

public abstract class AbstractDecorator implements iActionSpace {
    private iActionSpace actionSpace;

    @Override
    public void undoAction(JsonNode move, FamilyMember fm) {
        actionSpace.undoAction(move, fm);
    }

    @Override
    public int getMinimumActionValue(FamilyMember fm) {
        return actionSpace.getMinimumActionValue(fm);
    }

    @Override
    public int getMinimumNumberOfPlayers() {
        return actionSpace.getMinimumNumberOfPlayers();
    }

    @Override
    public void performAction(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        actionSpace.performAction(move, fm);
    }

    public AbstractDecorator(iActionSpace actionSpace){
        this.actionSpace = actionSpace;
    }
    @Override
    public void cleanup(){
        actionSpace.cleanup();
    }
    @Override
    public ArrayList<FamilyMember> getFamilyMembers(){
        return actionSpace.getFamilyMembers();
    }
    @Override
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