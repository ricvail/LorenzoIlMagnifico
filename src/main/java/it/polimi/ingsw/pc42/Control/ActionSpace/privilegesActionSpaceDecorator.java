package it.polimi.ingsw.pc42.Control.ActionSpace;

import com.fasterxml.jackson.databind.JsonNode;

import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Control.ActionSpace.AbstractDecorator;
import it.polimi.ingsw.pc42.Control.ActionSpace.iActionSpace;
import it.polimi.ingsw.pc42.Model.FamilyMember;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import it.polimi.ingsw.pc42.Control.PrivilegeManager;


public class privilegesActionSpaceDecorator extends AbstractDecorator {

    private int quantity;
    private Logger logger;

    /**
     * Class constructor.  Decorates an action space (council) with a privileges bonus.
     *
     * @param q quantity of privileges that the action space gives
     * @param actionSpace action space to be decorated
     */
    public privilegesActionSpaceDecorator(int q, iActionSpace actionSpace) {
        super(actionSpace);
        this.quantity=q;
        logger= LogManager.getLogger();
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
                logger.error(e1);
            }
            throw e;
        }
    }

    @Override
    public void undoAction(JsonNode move, FamilyMember fm) {
        try {
            getBoard().getPrivilegeManager().undoPrivileges(fm.owner, move);
        } catch (Exception e1) {
            logger.error("undoActionFailed",e1);
        }
        super.undoAction(move, fm);
    }
}
