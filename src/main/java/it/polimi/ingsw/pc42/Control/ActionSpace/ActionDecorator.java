package it.polimi.ingsw.pc42.Control.ActionSpace;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Model.FamilyMember;
import it.polimi.ingsw.pc42.Model.PersonalBonusTile;

public class ActionDecorator extends AbstractDecorator {

    private ActionType actionType;

    /**
     * Class constructor. Decorates an action space with the harvest or production action.
     *
     * @param actionType specify if harvest or production
     * @param actionSpace action space to be decorated
     */
    public ActionDecorator(ActionType actionType, iActionSpace actionSpace) {
        super(actionSpace);
        this.actionType = actionType;
    }

    @Override
    public void performAction(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        if (actionType==ActionType.HARVEST){
            fm.owner.performHarvest(move, fm);
        } else if (actionType == ActionType.PRODUCTION){
            fm.owner.performProduction(move, fm);
        }
        super.performAction(move, fm);
    }

    @Override
    public void undoAction(JsonNode move, FamilyMember fm) {
        if (actionType==ActionType.HARVEST){
            fm.owner.undoHarvest(move, fm);
        } else if (actionType == ActionType.PRODUCTION){
            fm.owner.undoProduction(move, fm);
        }
        super.undoAction(move, fm);
    }

    public enum ActionType{
        HARVEST, PRODUCTION
    }
}
