package it.polimi.ingsw.pc42.Control.DevelopmentCards;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Control.ActionSpace.Area;
import it.polimi.ingsw.pc42.Control.MoveManager;
import it.polimi.ingsw.pc42.Control.ResourceType;
import it.polimi.ingsw.pc42.Control.ResourceWrapper;
import it.polimi.ingsw.pc42.Model.Dice;
import it.polimi.ingsw.pc42.Model.FamilyMember;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class ExtraCard extends AbstractDecorator{

    /*
     * first complete this card's actions (super)
     * apply bonuses
     * place family member
     */

    int extraActionValue;
    ArrayList<bonus> bonuses;
    FamilyMember ghost;
    ArrayList<Area> allowedAreas;
    private Logger logger;

    /**
     * Class constructor. Decorates a card for the effect that allows you to draw an extra card, specifying from what
     * action value you start, from which areas you can draw the card and the discount of certain resources for
     * the cost of the extra card.
     *
     * @param c card to be decorated
     * @param allowedAreas allowed areas from which draw the extra card
     * @param bonuses given discount of certain resources for the cost of the extra card
     * @param actionValue action value from which you start trying to draw the new card (can be incremented w/ servants)
     */
    public ExtraCard(iCard c, ArrayList<Area> allowedAreas, ArrayList<bonus>bonuses, int actionValue) {
        super(c);
        this.allowedAreas = allowedAreas;
        this.extraActionValue=actionValue;
        this.bonuses=bonuses;
        logger= LogManager.getLogger();
    }

    /**
     * Checks for existing bonuses of a certain type and returns it or sets a new one to zero, if it doesn't exist.
     *
     * @param res resource type that requests
     * @return a bonus if already exists or a new one, saved, set to zero
     */
    public bonus findBonusByType(ResourceType res){
        for (bonus b : bonuses) {
            if (b.res == res)return b;
        }
        bonus b = new bonus(res, 0);
        bonuses.add(b);
        return b;
    }




    @Override
    public void drawCard(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        if (!(move.has("immediateEffect"))){
            throw new ActionAbortedException("immediateEffect", null);
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
            e.setLevel(e.getLevel()+1);
            throw e;
        }
    }

    /**
     * Iterates through resource types and set the active bonuses equal to the previous ones.
     *
     * @param fm family member of the player to be accessed
     */
    public void setOldBonuses(FamilyMember fm){
        for (ResourceWrapper rw:fm.owner.getResources()){
            bonus b=findBonusByType(rw.getResourceType());
            rw .setActiveBonus(b.previousBonus);
        }
    }

    /**
     * Iterates through resource types and creates new bonuses.
     *
     * @param fm family member of the player to be accessed
     */
    public void setNewBonuses(FamilyMember fm){
        for (ResourceWrapper rw:fm.owner.getResources()){
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
            MoveManager.undoGetActionSpaceFromJson(getBoard(), move.get("immediateEffect"), ghost);
        } catch (ActionAbortedException e) {
            logger.error(e);
        }
        setOldBonuses(fm);
    }

    /**
     * Class that make a bonus composed.
     */
    public static class bonus {

        private ResourceType res;
        private int bonusToAdd;
        private ResourceWrapper.CostBonus previousBonus;

        /**
         * Class constructor. Takes a resource type and a value of a bonus discount to add and tied them together.
         *
         * @param res resource type of the discount
         * @param bonusToAdd value to add as bonus discount
         */
        public bonus(ResourceType res, int bonusToAdd) {
            this.res = res;
            this.bonusToAdd = bonusToAdd;
        }
    }
}

