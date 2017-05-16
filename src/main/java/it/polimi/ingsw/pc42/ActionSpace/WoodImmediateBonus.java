package it.polimi.ingsw.pc42.ActionSpace;


import it.polimi.ingsw.pc42.FamilyMember;
import it.polimi.ingsw.pc42.Player;

public class WoodImmediateBonus extends AbstractDecorator {

    public WoodImmediateBonus(int quantity, AbstractActionSpace actionSpace){
        super(quantity, actionSpace);
    }

    @Override
    boolean canPlace(FamilyMember familyMember) {
        return false;
    }

    @Override
    void applyDrawEffect(Player player) {

    }

}
