package it.polimi.ingsw.pc42.Control.ActionSpace;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Control.ActionSpace.AbstractDecorator;
import it.polimi.ingsw.pc42.Control.ActionSpace.iActionSpace;
import it.polimi.ingsw.pc42.Model.FamilyMember;
import it.polimi.ingsw.pc42.Control.PrivilegeManager;



public class privilegesActionSpaceDecorator extends AbstractDecorator {

    private int quantity;

    public privilegesActionSpaceDecorator(int q, iActionSpace actionSpace) {
        super(actionSpace);
        this.quantity=q;
    }

    @Override
    public void performAction(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        getBoard().getPrivilegeManager().applyPrivileges(fm.owner, move,quantity); //automatically throws exception if something goes wrong
        try {
            super.performAction(move, fm);
        }catch (ActionAbortedException e){
            try {
                getBoard().getPrivilegeManager().undoPrivileges(fm.owner, move);
            } catch (Exception e1) {
                //this should NOT happen.
                e1.printStackTrace();
            }
            throw e;
        }
    }
}
