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
            fm.owner.getResource(resourceType).addUsingBonus(q);
        }
        catch (IllegalArgumentException e){
            fm.owner.getResource(resourceType).abortAddUsingBonus(q);
            throw new ActionAbortedException(false);
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
            //nada
        }
        super.undoDrawCard(move, fm);
    }

}
