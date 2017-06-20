package it.polimi.ingsw.pc42.Control.DevelopmentCards;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Control.ActionSpace.Area;
import it.polimi.ingsw.pc42.Control.MoveManager;
import it.polimi.ingsw.pc42.Model.Dice;
import it.polimi.ingsw.pc42.Model.FamilyMember;

import java.util.ArrayList;

/**
 * Created by RICVA on 17/06/2017.
 */
public class ExtraCard extends AbstractDecorator{

    /**
     * TODO
     * first complete this card's actions (super)
     * apply bonuses
     * place family member
     * @param c
     */

    JsonNode bonuses;

    ArrayList<Area> allowedAreas;

    public ExtraCard(iCard c, JsonNode bonuses) {
        super(c);
        this.bonuses=bonuses;
    }




    @Override
    public void drawCard(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        if (move.has("immediateEffect")){
            throw new ActionAbortedException("immediateEffect", null);//TODO
        }

        super.drawCard(move, fm);
        try {
            FamilyMember ghost= new FamilyMember(fm.owner, Dice.DiceColor.GHOST, allowedAreas);
            MoveManager.getActionSpaceFromJson(getBoard(), move.get("immediateEffect"), ghost);
        } catch (ActionAbortedException e){
            super.undoDrawCard(move, fm);
            throw e;
        }
    }
}
