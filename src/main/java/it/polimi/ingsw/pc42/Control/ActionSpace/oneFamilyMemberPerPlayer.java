package it.polimi.ingsw.pc42.Control.ActionSpace;


import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Model.Dice;
import it.polimi.ingsw.pc42.Model.FamilyMember;

import java.util.Iterator;

public class oneFamilyMemberPerPlayer extends AbstractDecorator{

    /**
     * Class constructor.  Decorates an action space if it belongs to an area in which you can't put two family
     * member of the same player, except neutral.
     *
     * @param actionSpace action space to be decorated
     */
    public oneFamilyMemberPerPlayer(iActionSpace actionSpace) {
        super(actionSpace);
    }

    @Override
    public void performAction(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        if (!canPlace(fm)){
            throw new ActionAbortedException(false, "This Area is already occupied by one of your non-neutral Family Member");
        }
        super.performAction(move, fm);
    }

    /**
     *Execute the controls to decide if a family member can be placed in that area.
     *
     * @param familyMember family member that has to be placed
     * @return <code>true</code> if can be placed
     */
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
                            fm.diceColor!= Dice.DiceColor.NEUTRAL&&
                            fm.diceColor!=familyMember.diceColor){
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
