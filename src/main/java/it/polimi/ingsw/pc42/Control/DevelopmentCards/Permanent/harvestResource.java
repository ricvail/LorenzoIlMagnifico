package it.polimi.ingsw.pc42.Control.DevelopmentCards.Permanent;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.AbstractDecorator;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.iCard;
import it.polimi.ingsw.pc42.Control.ResourceType;
import it.polimi.ingsw.pc42.Model.FamilyMember;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by RICVA on 07/07/2017.
 */
public class harvestResource extends AbstractDecorator {
    private ResourceType resourceType;
    private int q;
    private Logger logger;

    public harvestResource(ResourceType rt, int quantity, iCard c) {
        super(c);
        q= quantity;
        resourceType=rt;
        logger= LogManager.getLogger();
    }

    @Override
    public void onHarvest(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        if (fm.getValue()>=getActionValue()) {
            try {
                fm.owner.getResource(resourceType).add(q);
            } catch (IllegalArgumentException e) {
                logger.info(e);
                fm.owner.getResource(resourceType).add(q * -1);
                throw new ActionAbortedException(false, "Not enough resources");
            }
            try {
                super.onHarvest(move, fm);
            } catch (ActionAbortedException e) {
                fm.owner.getResource(resourceType).add(q * -1);
                throw e;
            }
        }
    }

    @Override
    public void undoOnHarvest(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        if (fm.getValue()>=getActionValue()) {
            try {
                fm.owner.getResource(resourceType).add(q * -1);
            } catch (Exception e) {
                logger.error(e);
            }
            super.undoOnHarvest(move, fm);
        }
    }
}
