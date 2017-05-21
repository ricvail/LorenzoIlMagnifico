package it.polimi.ingsw.pc42.ActionSpace;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Board;
import it.polimi.ingsw.pc42.Dice;
import it.polimi.ingsw.pc42.FamilyMember;
import it.polimi.ingsw.pc42.ResourceType;

import java.util.Iterator;

/**
 * Created by RICVA on 21/05/2017.
 */
public class additionalCoinsTax extends AbstractDecorator {
    private int coins;
    private Board board;
    public additionalCoinsTax(int coins, Board b, iActionSpace actionSpace) {
        super(actionSpace);
        this.coins=coins;
        board=b;
    }

    @Override
    public void placeFamilyMember(FamilyMember familyMember, JsonNode json) {
        if (doesTaxApply(familyMember)){
            familyMember.owner.getResource(ResourceType.COIN).add(coins*-1);
        }
        super.placeFamilyMember(familyMember, json);

    }


    @Override
    public boolean canPlace(FamilyMember familyMember) {
        if (doesTaxApply(familyMember)) {
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

    public boolean doesTaxApply(FamilyMember familyMember){
        Iterator<iActionSpace> iterator = board.getActionSpaces().iterator();
        while (iterator.hasNext()){
            iActionSpace actionSpace = iterator.next();
            if (actionSpace.getArea()==this.getArea()){
                Iterator<FamilyMember> fmIterator= actionSpace.getFamilyMembers().iterator();
                while (fmIterator.hasNext()){
                    FamilyMember fm = fmIterator.next();
                    if (fm.diceColor.visible){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
