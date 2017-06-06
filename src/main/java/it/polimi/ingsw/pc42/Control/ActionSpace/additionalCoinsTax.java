package it.polimi.ingsw.pc42.Control.ActionSpace;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Model.FamilyMember;
import it.polimi.ingsw.pc42.Control.ResourceType;

/**
 * Created by RICVA on 21/05/2017.
 */
public class additionalCoinsTax extends AbstractDecorator {
    private int coins;
    public additionalCoinsTax(int coins, iActionSpace actionSpace) {
        super(actionSpace);
        this.coins=coins;
    }

    @Override
    public void placeFamilyMember(FamilyMember familyMember, JsonNode json) {
        if (doesTaxApply()){
            familyMember.owner.getResource(ResourceType.COIN).add(coins*-1);
        }
        super.placeFamilyMember(familyMember, json);

    }


    @Override
    public boolean canPlace(FamilyMember familyMember) {
        if (doesTaxApply()) {
            try {
                familyMember.owner.getResource(ResourceType.COIN).add(coins * -1);
            } catch (IllegalArgumentException e) {
                familyMember.owner.getResource(ResourceType.COIN).add(coins);
                return false;
            }
            boolean b = super.canPlace(familyMember);
            familyMember.owner.getResource(ResourceType.COIN).add(coins);
            return b;
        } else {
            return super.canPlace(familyMember);
        }
    }

    public boolean doesTaxApply(){
        return !ActionSpace.isFirstInArea(getBoard(), this.getArea());
    }
}
