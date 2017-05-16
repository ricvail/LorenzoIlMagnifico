package it.polimi.ingsw.pc42.ActionSpace;


import it.polimi.ingsw.pc42.FamilyMember;
import it.polimi.ingsw.pc42.Player;

public class ActionSpace extends AbstractActionSpace {
    private FamilyMember familyMember;
    private Area area;

    public ActionSpace(Area area){
        this.area = area;
    }

    @Override
    boolean canPlace(FamilyMember familyMember) {
        return false;
    }

    @Override
    void applyDrawEffect(Player player) {

    }
}
