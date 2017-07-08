package it.polimi.ingsw.pc42.Control.DevelopmentCards.Permanent;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.AbstractDecorator;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.iCard;
import it.polimi.ingsw.pc42.Control.ResourceType;
import it.polimi.ingsw.pc42.Model.FamilyMember;

/**
 * Created by RICVA on 07/07/2017.
 */
public class harvestResource extends AbstractDecorator {
    private ResourceType resourceType;
    private int q;

    public harvestResource(ResourceType rt, int quantity, iCard c) {
        super(c);
        q= quantity;
        resourceType=rt;
    }

    @Override
    public void onHarvest(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        try {
            fm.owner.getResource(resourceType).add(q);
        } catch (IllegalArgumentException e){
            fm.owner.getResource(resourceType).add(q * -1);
            throw new ActionAbortedException(false, "Not enough resources");
        }
        try {
            super.onHarvest(move, fm);
        } catch (ActionAbortedException e){
            fm.owner.getResource(resourceType).add(q * -1);
            throw e;
        }
    }

    @Override
    public void undoOnHarvest(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        try {
            fm.owner.getResource(resourceType).add(q * -1);
        } catch (Exception e){
            e.printStackTrace();
        }
        super.undoOnHarvest(move, fm);
    }
}
