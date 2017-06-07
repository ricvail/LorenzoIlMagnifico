package it.polimi.ingsw.pc42.Control.ActionSpace;

/**
 * Created by RICVA on 22/05/2017.
 */
public class ActionDecorator extends AbstractDecorator{



    public ActionDecorator(ActionType actionType, iActionSpace actionSpace) {
        super(actionSpace);
    }

    public enum ActionType{
        HARVEST, PRODUCTION
    }
}
