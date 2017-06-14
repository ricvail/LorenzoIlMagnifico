package it.polimi.ingsw.pc42.Control.DevelopmentCards;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Model.FamilyMember;
import it.polimi.ingsw.pc42.Model.Player;
import it.polimi.ingsw.pc42.Control.ResourceType;

public class ResourceImmediateBonus extends AbstractDecorator {
    private ResourceType resourceType;
    private int q;

    public ResourceImmediateBonus(ResourceType rt, int quantity, iCard c) {
        super(c);
        q= quantity;
        resourceType=rt;
    }

    @Override
    public void drawCard(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        try{
            fm.owner.getResource(resourceType).add(q);
        }
        catch (IllegalArgumentException e){
            fm.owner.getResource(resourceType).add(q*-1);
            throw new ActionAbortedException(false);
        }
        try {
            super.drawCard(move, fm);
        } catch (ActionAbortedException e){
            fm.owner.getResource(resourceType).add(q*-1);
            throw e;
        }
    }

    @Override
    public void undoDrawCard(JsonNode move, FamilyMember fm) {
        fm.owner.getResource(resourceType).add(q*-1);
        super.undoDrawCard(move, fm);
    }

}
