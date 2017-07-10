package it.polimi.ingsw.pc42.Control.DevelopmentCards;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Control.ResourceType;
import it.polimi.ingsw.pc42.Model.FamilyMember;

public class militaryCost extends AbstractDecorator {

    private int required, subtracted;

    /**
     * Class contructor. Decorates a card of it has a cost type that requires a check on military points: first the
     * required ones and then the effective subtraction of another amount.
     *
     * @param c card to be decorated
     * @param required required military points to draw the card
     * @param subtracted effective military points that are subtracted
     */
    public militaryCost(iCard c, int required, int subtracted) {
        super(c);
        this.required=required;
        this.subtracted=subtracted;
    }

    @Override
    public void drawCard(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        int owned =  fm.owner.getResource(ResourceType.MILITARYPOINTS).get();
        if (owned<required){
            throw new ActionAbortedException(false, "You don't have enough Military Points");
        }
        fm.owner.getResource(ResourceType.MILITARYPOINTS).add(subtracted*-1);
        try {
            super.drawCard(move, fm);
        } catch (ActionAbortedException e){
            fm.owner.getResource(ResourceType.MILITARYPOINTS).add(subtracted);
            throw e;
        }
    }

    @Override
    public void undoDrawCard(JsonNode move, FamilyMember fm) {
        fm.owner.getResource(ResourceType.MILITARYPOINTS).add(subtracted);
        super.undoDrawCard(move, fm);
    }
}
