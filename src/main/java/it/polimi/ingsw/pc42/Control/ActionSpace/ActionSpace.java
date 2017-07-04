package it.polimi.ingsw.pc42.Control.ActionSpace;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Model.FamilyMember;
import it.polimi.ingsw.pc42.Model.Board;
import it.polimi.ingsw.pc42.Utilities.BoardProvider;

import java.util.ArrayList;
import java.util.Iterator;

public class ActionSpace implements iActionSpace {
    private ArrayList<FamilyMember> familyMembers;
    private Area area;
    private int ID;
    private int actionValue;
    BoardProvider boardProvider;
    private int minPlayers;

    public ActionSpace(BoardProvider boardProvider, Area area, int ID, int actionValue, int minPlayers){
        this.area = area;
        this.ID=ID;
        this.actionValue=actionValue;
        this.boardProvider=boardProvider;
        this.familyMembers=new ArrayList<>();
        this.minPlayers=minPlayers;
    }

    @Override
    public void performAction(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        if (fm.getValue()<actionValue){
            throw new ActionAbortedException(false, "Family Member's Action Value lower than required");
        }
    }

    @Override
    public void undoAction(JsonNode move, FamilyMember fm) {
    }


    @Override
    public void cleanup() {
        while (familyMembers.size()>0){
            familyMembers.get(0).setUsed(false);
            familyMembers.remove(0);
        }

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
    public int getNumberOfVisibleFamilyMembers(FamilyMember fmToAdd) {
        int i=0;
        Iterator<FamilyMember> iterator = familyMembers.iterator();
        while (iterator.hasNext()){
            FamilyMember fm = iterator.next();
            if (fm.diceColor.visible&&
                    (fm.getDiceColor()!=fmToAdd.getDiceColor()||fm.owner.getColor()!=fmToAdd.owner.getColor())) {
                i++;
            }

        }
        return i;
    }
    

    @Override
    public Board getBoard() {
        return boardProvider.getBoard();
    }

    @Override
    public int getMinimumActionValue(FamilyMember fm) {
        return actionValue;
    }

    @Override
    public int getMinimumNumberOfPlayers() {
        return minPlayers;
    }

    @Override
    public ObjectNode updateDescription(ObjectNode node) {
        //TODO
        //node.put();

        return node;
    }


    public static boolean isFirstInArea(Board board, Area area, FamilyMember fmToAdd){
        Iterator<iActionSpace> iterator = board.getActionSpaces().iterator();
        while (iterator.hasNext()){
            iActionSpace actionSpace = iterator.next();
            if (actionSpace.getArea()==area){
                Iterator<FamilyMember> fmIterator= actionSpace.getFamilyMembers().iterator();
                while (fmIterator.hasNext()){
                    FamilyMember fm = fmIterator.next();
                    if (fm.diceColor.visible&&
                            (fm.getDiceColor()!=fmToAdd.getDiceColor()||fm.owner.getColor()!=fmToAdd.owner.getColor())){
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
