package it.polimi.ingsw.pc42.ActionSpace;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.FamilyMember;
import it.polimi.ingsw.pc42.ResourceType;

public class ResourceImmediateBonus extends AbstractDecorator {
    private ResourceType resourceType;
    private int q;

    public ResourceImmediateBonus(ResourceType rt, int quantity, iActionSpace actionSpace){
        super(actionSpace);
        q= quantity;
        resourceType=rt;
    }

    @Override
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
    }
}
