package it.polimi.ingsw.pc42.ActionSpace;

import it.polimi.ingsw.pc42.FamilyMember;
import it.polimi.ingsw.pc42.Player;

public abstract class AbstractActionSpace {

    abstract boolean canPlace(FamilyMember familyMember);
    abstract void applyDrawEffect(Player player);
}
