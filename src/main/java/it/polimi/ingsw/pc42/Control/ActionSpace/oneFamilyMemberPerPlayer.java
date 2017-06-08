package it.polimi.ingsw.pc42.Control.ActionSpace;


import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Model.Board;
import it.polimi.ingsw.pc42.Model.Dice;
import it.polimi.ingsw.pc42.Model.FamilyMember;

import java.util.Iterator;

public class oneFamilyMemberPerPlayer extends AbstractDecorator{


    public oneFamilyMemberPerPlayer(iActionSpace actionSpace) {
        super(actionSpace);
    }

    @Override
    public void performAction(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        if (!canPlace(fm)){
            throw new ActionAbortedException(false);
        }
        super.performAction(move, fm);
    }

    private boolean canPlace(FamilyMember familyMember) {
        if (!familyMember.diceColor.visible||familyMember.diceColor== Dice.DiceColor.NEUTRAL){
            return true;
        }
        Iterator<iActionSpace> iterator = getBoard().getActionSpaces().iterator();
        while (iterator.hasNext()){
            iActionSpace actionSpace = iterator.next();
            if (actionSpace.getArea()==this.getArea()){
                Iterator<FamilyMember> fmIterator= actionSpace.getFamilyMembers().iterator();
                while (fmIterator.hasNext()){
                    FamilyMember fm = fmIterator.next();
                    if (fm.owner==familyMember.owner &&
                            fm.diceColor.visible&&
                            fm.diceColor!= Dice.DiceColor.NEUTRAL){
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
