package it.polimi.ingsw.pc42.Control.DevelopmentCards;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Model.FamilyMember;
import it.polimi.ingsw.pc42.Control.ResourceType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourceImmediateBonus extends AbstractDecorator {

    private ResourceType resourceType;
    private int q;
    private Logger logger;

    /**
     * Class constructor. Decorates a card that has an effect that gives a resource bonus of some quantity.
     *
     * @param rt resource type to add
     * @param quantity quantity of resource to add
     * @param c card to be decorated
     */
    public ResourceImmediateBonus(ResourceType rt, int quantity, iCard c) {
        super(c);
        q= quantity;
        resourceType=rt;
        logger= LogManager.getLogger();
    }

    @Override
    public void drawCard(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        try{
            fm.owner.getResource(resourceType).addUsingBonus(q);
        }
        catch (IllegalArgumentException e){
            logger.info(e);
            fm.owner.getResource(resourceType).abortAddUsingBonus(q);
            throw new ActionAbortedException(false, "Not enough "+resourceType.getString()+" to draw the card");
        }
        try {
            super.drawCard(move, fm);
        } catch (ActionAbortedException e){
            fm.owner.getResource(resourceType).abortAddUsingBonus(q);
            throw e;
        }
    }

    @Override
    public void undoDrawCard(JsonNode move, FamilyMember fm) {
        try {
            fm.owner.getResource(resourceType).undoAddUsingBonus(q);
        }catch (IllegalArgumentException e){
            logger.error(e);
        }
        super.undoDrawCard(move, fm);
    }

}
