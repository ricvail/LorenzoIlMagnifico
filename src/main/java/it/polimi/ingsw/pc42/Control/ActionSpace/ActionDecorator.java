package it.polimi.ingsw.pc42.Control.ActionSpace;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Model.FamilyMember;
import it.polimi.ingsw.pc42.Model.PersonalBonusTile;

/**
 * Created by RICVA on 22/05/2017.
 */
public class ActionDecorator extends AbstractDecorator {

    private ActionType actionType;


    public ActionDecorator(ActionType actionType, iActionSpace actionSpace) {
        super(actionSpace);
        this.actionType = actionType;
    }

    @Override
    public void performAction(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        if (actionType==ActionType.HARVEST){
            PersonalBonusTile.applyBonuses(fm.owner.bonusTile.harvestBonuses, fm.owner);
        } else if (actionType == ActionType.PRODUCTION){
            PersonalBonusTile.applyBonuses(fm.owner.bonusTile.productionBonuses, fm.owner);
        }
        super.performAction(move, fm);
    }

    @Override
    public void undoAction(JsonNode move, FamilyMember fm) {
        if (actionType==ActionType.HARVEST){
            PersonalBonusTile.undoBonuses(fm.owner.bonusTile.harvestBonuses, fm.owner);
        } else if (actionType == ActionType.PRODUCTION){
            PersonalBonusTile.undoBonuses(fm.owner.bonusTile.productionBonuses, fm.owner);
        }
        super.undoAction(move, fm);
    }

    public enum ActionType{
        HARVEST, PRODUCTION
    }
}
