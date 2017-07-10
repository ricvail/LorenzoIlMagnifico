package it.polimi.ingsw.pc42.Control.DevelopmentCards.Permanent;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionSpace.Area;
import it.polimi.ingsw.pc42.Control.ActionSpace.iActionSpace;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.AbstractDecorator;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.iCard;
import it.polimi.ingsw.pc42.Control.ResourceType;
import it.polimi.ingsw.pc42.Control.ResourceWrapper;
import it.polimi.ingsw.pc42.Model.FamilyMember;

import java.util.ArrayList;

/**
 * Created by RICVA on 10/07/2017.
 */
public class DiceBonus extends AbstractDecorator {

    ArrayList<ResourceWrapper> bonuses;
    int diceBonus;
    Area area;

    public DiceBonus(ArrayList<ResourceWrapper> bonuses, int diceBonus, Area area, iCard c) {
        super(c);
        this.bonuses=bonuses;
        this.diceBonus=diceBonus;
        this.area=area;
    }

    @Override
    public void onAction(JsonNode move, FamilyMember fm, iActionSpace space) {
        if (space.getArea()==area){
            fm.setValue(fm.getValue()+diceBonus);
            for(ResourceWrapper rw : bonuses){
                fm.owner.getResource(rw.getResourceType()).addBonus(rw.get());
            }
        }
        super.onAction(move, fm, space);
    }
    @Override
    public void undoOnAction(JsonNode move, FamilyMember fm, iActionSpace space) {
        if (space.getArea()==area){
            fm.setValue(fm.getValue()+diceBonus);
            //Undoing resource bonuses is done by Player.undoApplyPermanentEffects
        }
        super.onAction(move, fm, space);
    }
}
