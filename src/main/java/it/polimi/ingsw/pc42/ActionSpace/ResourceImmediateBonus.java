package it.polimi.ingsw.pc42.ActionSpace;

import it.polimi.ingsw.pc42.FamilyMember;
import it.polimi.ingsw.pc42.ResourceType;

public class ResourceImmediateBonus extends AbstractDecorator {
    private ResourceType resourceType;
    private int q;

    public ResourceImmediateBonus(ResourceType rt, int quantity, AbstractActionSpace actionSpace){
        super(actionSpace);
        q= quantity;
        resourceType=rt;
    }

    @Override
    void placeFamilyMember(FamilyMember familyMember) {
        super.placeFamilyMember(familyMember);
        familyMember.owner.getResource(resourceType).add(q);
    }
}
