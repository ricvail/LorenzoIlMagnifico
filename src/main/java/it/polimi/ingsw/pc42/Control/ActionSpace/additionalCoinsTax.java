package it.polimi.ingsw.pc42.Control.ActionSpace;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Model.FamilyMember;
import it.polimi.ingsw.pc42.Control.ResourceType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class additionalCoinsTax extends AbstractDecorator {

    private int coins;
    private Logger logger;

    /**
     * Class constructor. Decorates the additional coins tax that is applied to a player if there is already a family
     * member in a specific tower.
     *
     * @param coins value of the tax
     * @param actionSpace action space that decorates
     */
    public additionalCoinsTax(int coins, iActionSpace actionSpace) {
        super(actionSpace);
        this.coins=coins;
        logger= LogManager.getLogger();
    }

    @Override
    public void performAction(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        if (doesTaxApply(fm)) {
            try {
                fm.owner.getResource(ResourceType.COIN).add(coins * -1);
            } catch (IllegalArgumentException e) {
                logger.info(e);
                fm.owner.getResource(ResourceType.COIN).add(coins);
                throw new ActionAbortedException(false, "You don't have enough coins to pay the Tax");
            }
        }
        try {
            super.performAction(move, fm);
        }catch (ActionAbortedException e){
            if (doesTaxApply(fm)){
                fm.owner.getResource(ResourceType.COIN).add(coins);
            }
            throw e;
        }
    }

    @Override
    public void undoAction(JsonNode move, FamilyMember fm) {
        if (doesTaxApply(fm)){
            fm.owner.getResource(ResourceType.COIN).add(coins);
        }
        super.undoAction(move, fm);
    }

    /**
     * Checks if the tax applies to the owner of the family member.
     *
     * @param fm family member to be placed
     * @return <code>true</code> if the tax applies
     */
    public boolean doesTaxApply(FamilyMember fm){
        return !ActionSpace.isFirstInArea(getBoard(), this.getArea(), fm);
    }
}
