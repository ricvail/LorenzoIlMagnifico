package it.polimi.ingsw.pc42.Control.ActionSpace;


import it.polimi.ingsw.pc42.Model.Board;
import it.polimi.ingsw.pc42.Model.Dice;
import it.polimi.ingsw.pc42.Model.FamilyMember;

import java.util.Iterator;

public class oneFamilyMemberPerPlayer extends AbstractDecorator{

    private Board board;

    public oneFamilyMemberPerPlayer(iActionSpace actionSpace, Board b) {
        super(actionSpace);
        board=b;
    }

    @Override
    public boolean canPlace(FamilyMember familyMember) {
        if (!familyMember.diceColor.visible||familyMember.diceColor== Dice.DiceColor.NEUTRAL){
            return super.canPlace(familyMember);
        }

        Iterator<iActionSpace> iterator = board.getActionSpaces().iterator();
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

        return super.canPlace(familyMember);
    }
}
