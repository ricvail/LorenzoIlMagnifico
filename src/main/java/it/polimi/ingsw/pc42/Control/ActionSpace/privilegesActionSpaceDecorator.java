package it.polimi.ingsw.pc42.Control.ActionSpace;

import com.fasterxml.jackson.databind.JsonNode;

import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Model.FamilyMember;



public class privilegesActionSpaceDecorator extends AbstractDecorator {

    private int quantity;

    /**
     * Class constructor.  Decorates an action space (council) with a privileges bonus.
     *
     * @param q quantity of privileges that the action space gives
     * @param actionSpace action space to be decorated
     */
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
                new RuntimeException(e);
            }
            throw e;
        }
    }

    @Override
    public void undoAction(JsonNode move, FamilyMember fm) {
        try {
            getBoard().getPrivilegeManager().undoPrivileges(fm.owner, move);
        } catch (Exception e1) {
            new RuntimeException(e1);
        }
        super.undoAction(move, fm);
    }
}
