package it.polimi.ingsw.pc42.Control.ActionSpace;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.org.apache.bcel.internal.generic.ANEWARRAY;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
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
    public void performAction(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        if (doesTaxApply()) {
            try {
                fm.owner.getResource(ResourceType.COIN).add(coins * -1);
            } catch (IllegalArgumentException e) {
                fm.owner.getResource(ResourceType.COIN).add(coins);
                throw new ActionAbortedException(false);
            }
        }
        try {
            super.performAction(move, fm);
        }catch (ActionAbortedException e){
            if (doesTaxApply()){
                fm.owner.getResource(ResourceType.COIN).add(coins);
            }
            throw e;
        }
    }

    /*    @Override
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
    }*/

    public boolean doesTaxApply(){
        return !ActionSpace.isFirstInArea(getBoard(), this.getArea());
    }
}
