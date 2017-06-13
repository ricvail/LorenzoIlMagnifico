package it.polimi.ingsw.pc42.Control.ActionSpace;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Model.FamilyMember;
import it.polimi.ingsw.pc42.Control.ResourceType;

public class ResourceImmediateBonus extends AbstractDecorator {
    private ResourceType resourceType;
    private int q;

    public ResourceImmediateBonus(ResourceType rt, int quantity, iActionSpace actionSpace){
        super(actionSpace);
        q= quantity;
        resourceType=rt;
    }

    @Override
    public void performAction(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        try {
            fm.owner.getResource(resourceType).add(q);
        } catch (IllegalArgumentException e){
            fm.owner.getResource(resourceType).add(q * -1);
            throw new ActionAbortedException(false);
        }
        try {
            super.performAction(move, fm);
        } catch (ActionAbortedException e){
            fm.owner.getResource(resourceType).add(q * -1);
            throw e;
        }
    }

    @Override
    public void undoAction(JsonNode move, FamilyMember fm) {
        fm.owner.getResource(resourceType).add(q * -1);
        super.undoAction(move, fm);
    }

    /*@Override
    public void placeFamilyMember(FamilyMember familyMember, JsonNode json) {
        super.placeFamilyMember(familyMember, json);
        familyMember.owner.getResource(resourceType).add(q);
    }

    @Override
    public boolean canPlace(FamilyMember familyMember) {
        try {
            familyMember.owner.getResource(resourceType).add(q);
        } catch (IllegalArgumentException e){
            familyMember.owner.getResource(resourceType).add(q * -1);
            return false;
        }
        boolean b=  super.canPlace(familyMember);
        familyMember.owner.getResource(resourceType).add(q * -1);
        return b;
    }*/
}