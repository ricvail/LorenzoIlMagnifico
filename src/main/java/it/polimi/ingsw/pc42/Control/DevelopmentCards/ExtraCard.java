package it.polimi.ingsw.pc42.Control.DevelopmentCards;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Control.ActionSpace.Area;
import it.polimi.ingsw.pc42.Control.MoveManager;
import it.polimi.ingsw.pc42.Control.ResourceType;
import it.polimi.ingsw.pc42.Control.ResourceWrapper;
import it.polimi.ingsw.pc42.Model.Dice;
import it.polimi.ingsw.pc42.Model.FamilyMember;

import java.util.ArrayList;

public class ExtraCard extends AbstractDecorator{

    /**
     * TODO
     * first complete this card's actions (super)
     * apply bonuses
     * place family member
     * @param c
     */

    int extraActionValue;
    ArrayList<bonus> bonuses;
    FamilyMember ghost;
    public ExtraCard(iCard c, ArrayList<Area> allowedAreas, ArrayList<bonus>bonuses, int actionValue) {
        super(c);
        this.allowedAreas = allowedAreas;
        this.extraActionValue=actionValue;
        this.bonuses=bonuses;
    }

    ArrayList<Area> allowedAreas;

    public bonus findBonusByType(ResourceType res){
        for (bonus b:bonuses) {
            if (b.res==res)return b;
        }
        bonus b=new bonus(res, 0);
        bonuses.add(b);
        return b;
    }




    @Override
    public void drawCard(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        if (!(move.has("immediateEffect"))){
            throw new ActionAbortedException("immediateEffect", null);//TODO
        }
        super.drawCard(move, fm);
        setNewBonuses(fm);
        try {
            ghost= new FamilyMember(fm.owner, Dice.DiceColor.GHOST, allowedAreas);
            ghost.setValue(extraActionValue);
            MoveManager.getActionSpaceFromJson(getBoard(), move.get("immediateEffect"), ghost);
            setOldBonuses(fm);
        } catch (ActionAbortedException e){
            setOldBonuses(fm);
            super.undoDrawCard(move, fm);
            throw e;
        }
    }

    public void setOldBonuses(FamilyMember fm){
        for (ResourceWrapper rw:fm.owner.resources){
            bonus b=findBonusByType(rw.getResourceType());
            rw.activeBonus=b.previousBonus;
        }
    }

    public void setNewBonuses(FamilyMember fm){
        for (ResourceWrapper rw:fm.owner.resources){
            bonus b=findBonusByType(rw.getResourceType());
            b.previousBonus=rw.getBonus();
            rw.resetBonus();
            rw.addBonus(b.bonusToAdd);
        }
    }

    @Override
    public void undoDrawCard(JsonNode move, FamilyMember fm) {
        super.undoDrawCard(move, fm);
        setNewBonuses(fm);
        try {
            MoveManager.undoGetActionSpaceFromJson(getBoard(), move, ghost);
        } catch (ActionAbortedException e) {
            e.printStackTrace();
        }
        setOldBonuses(fm);
    }

    public static class bonus {
        public bonus(ResourceType res, int bonusToAdd) {
            this.res = res;
            this.bonusToAdd = bonusToAdd;
        }
        public ResourceType res;
        public int bonusToAdd;
        public ResourceWrapper.CostBonus previousBonus;
    }
}

