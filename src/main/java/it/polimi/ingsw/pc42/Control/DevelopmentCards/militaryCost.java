package it.polimi.ingsw.pc42.Control.DevelopmentCards;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Control.ResourceType;
import it.polimi.ingsw.pc42.Model.FamilyMember;

/**
 * Created by RICVA on 15/06/2017.
 */
public class militaryCost extends AbstractDecorator {

    private int required, subtracted;

    public militaryCost(iCard c, int required, int subtracted) {
        super(c);
        this.required=required;
        this.subtracted=subtracted;
    }

    @Override
    public void drawCard(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        int owned =  fm.owner.getResource(ResourceType.MILITARYPOINTS).get();
        if (owned<required){
            throw new ActionAbortedException(false);
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
