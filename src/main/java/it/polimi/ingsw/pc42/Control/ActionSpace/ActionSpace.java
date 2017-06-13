package it.polimi.ingsw.pc42.Control.ActionSpace;

import com.fasterxml.jackson.databind.JsonNode;
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
    //Board board;
    BoardProvider boardProvider;

    public ActionSpace(BoardProvider boardProvider, Area area, int ID, int actionValue){
        this.area = area;
        this.ID=ID;
        this.actionValue=actionValue;
        this.boardProvider=boardProvider;
        this.familyMembers=new ArrayList<>();
    }

    @Override
    public void performAction(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        if (fm.getValue()<actionValue){
            throw new ActionAbortedException(false);
        }
        fm.setUsed(true);
        this.familyMembers.add(fm);
    }

    @Override
    public void undoAction(JsonNode move, FamilyMember fm) {
        fm.setUsed(false);
        this.familyMembers.remove(fm);
    }

    /*
    @Override
    public boolean canPlace(FamilyMember familyMember) {
        if (familyMember.getValue()<actionValue){
            if (familyMember.owner.getResource(ResourceType.SERVANT).get()>=(actionValue-familyMember.getValue())){
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public void placeFamilyMember(FamilyMember familyMember, JsonNode json) {
        familyMember.setUsed(true);
        this.familyMembers.add(familyMember);
    }
*/

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
        return 0;
    }

    public static boolean isFirstInArea(Board board, Area area){
        Iterator<iActionSpace> iterator = board.getActionSpaces().iterator();
        while (iterator.hasNext()){
            iActionSpace actionSpace = iterator.next();
            if (actionSpace.getArea()==area){
                Iterator<FamilyMember> fmIterator= actionSpace.getFamilyMembers().iterator();
                while (fmIterator.hasNext()){
                    FamilyMember fm = fmIterator.next();
                    if (fm.diceColor.visible){
                        return false;
                    }
                }
            }
        }
        return true;
    }

}