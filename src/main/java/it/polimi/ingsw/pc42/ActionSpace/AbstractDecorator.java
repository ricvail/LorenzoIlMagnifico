package it.polimi.ingsw.pc42.ActionSpace;


import it.polimi.ingsw.pc42.FamilyMember;
import it.polimi.ingsw.pc42.Player;

public abstract class AbstractDecorator extends AbstractActionSpace{
    private AbstractActionSpace actionSpace;
    private int quantity;

    public AbstractDecorator(int quantity, AbstractActionSpace actionSpace){
        this.quantity = quantity;
        this.actionSpace = actionSpace;
    }

    abstract boolean canPlace(FamilyMember familyMember);
    abstract void applyDrawEffect(Player player);
}
