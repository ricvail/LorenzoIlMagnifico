package it.polimi.ingsw.pc42.Control.DevelopmentCards.Permanent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.polimi.ingsw.pc42.Control.ActionSpace.Area;
import it.polimi.ingsw.pc42.Control.ActionSpace.iActionSpace;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.AbstractDecorator;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.iCard;
import it.polimi.ingsw.pc42.Model.FamilyMember;

/**
 * Created by RICVA on 10/07/2017.
 */
public class DisableImmediateBonus extends AbstractDecorator{
    /**
     * Class constructor. It is needed to initialize the card field, from a subclass, before every other decoration.
     *
     * @param c card to be decorated
     */
    public DisableImmediateBonus(iCard c) {
        super(c);
    }

    @Override
    public void onAction(JsonNode move, FamilyMember fm, iActionSpace space) {
        if ((space.getArea()== Area.BUILDING||space.getArea()== Area.TERRITORY||
                space.getArea()== Area.VENTURE||space.getArea()== Area.CHARACTER)){
            ((ObjectNode)move).put("disableImmediateSlotBonus", true);
        }
        super.onAction(move, fm, space);
    }

    @Override
    public void undoOnAction(JsonNode move, FamilyMember fm, iActionSpace space) {
        if ((space.getArea()== Area.BUILDING||space.getArea()== Area.TERRITORY||
                space.getArea()== Area.VENTURE||space.getArea()== Area.CHARACTER)){
            ((ObjectNode)move).put("disableImmediateSlotBonus", true);
        }
        super.undoOnAction(move, fm, space);
    }
}
