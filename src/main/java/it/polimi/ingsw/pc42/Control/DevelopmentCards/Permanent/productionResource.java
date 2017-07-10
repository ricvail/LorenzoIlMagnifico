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
public class productionResource extends AbstractDecorator {
    private ResourceType resourceType;
    private int q;
    private Logger logger;

    public productionResource(ResourceType rt, int quantity, iCard c) {
        super(c);
        q= quantity;
        resourceType=rt;
        logger= LogManager.getLogger();
    }

    @Override
    public void onProduction (JsonNode move, FamilyMember fm) throws ActionAbortedException {
        if (fm.getValue()>=getActionValue()) {
            try {
                fm.owner.getResource(resourceType).add(q);
            } catch (IllegalArgumentException e) {
                logger.info(e);
                try {
                    fm.owner.getResource(resourceType).add(q * -1);
                } catch (Exception e1){
                    logger.error(e1);
                }
                throw new ActionAbortedException(false, "Not enough resources");
            }
            try {
                super.onProduction(move, fm);
            } catch (ActionAbortedException e) {
                try {
                    fm.owner.getResource(resourceType).add(q * -1);
                } catch (Exception ex){
                    logger.info(ex);
                }
                throw e;
            }
        }
    }

    @Override
    public void undoOnProduction(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        if (fm.getValue()>=getActionValue()) {
            try {
                fm.owner.getResource(resourceType).add(q * -1);
            } catch (Exception e) {
                logger.error(e);
            }
            super.undoOnProduction(move, fm);
        }
    }
}
