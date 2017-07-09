package it.polimi.ingsw.pc42.Control.DevelopmentCards.Permanent;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.AbstractDecorator;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.iCard;
import it.polimi.ingsw.pc42.Control.ResourceType;
import it.polimi.ingsw.pc42.Model.FamilyMember;

import java.util.logging.Logger;

/**
 * Created by RICVA on 07/07/2017.
 */
public class productionResource extends AbstractDecorator {
    private ResourceType resourceType;
    private int q;

    public productionResource(ResourceType rt, int quantity, iCard c) {
        super(c);
        q= quantity;
        resourceType=rt;
    }

    @Override
    public void onProduction (JsonNode move, FamilyMember fm) throws ActionAbortedException {
        if (fm.getValue()>=getActionValue()) {
            try {
                fm.owner.getResource(resourceType).add(q);
            } catch (IllegalArgumentException e) {
                fm.owner.getResource(resourceType).add(q * -1);
                throw new ActionAbortedException(false, "Not enough resources");
            }
            try {
                super.onProduction(move, fm);
            } catch (ActionAbortedException e) {
                fm.owner.getResource(resourceType).add(q * -1);
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
                e.printStackTrace();
            }
            super.undoOnProduction(move, fm);
        }
    }
}
