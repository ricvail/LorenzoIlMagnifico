package it.polimi.ingsw.pc42.Control.DevelopmentCards.Permanent;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.AbstractDecorator;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.iCard;
import it.polimi.ingsw.pc42.Model.FamilyMember;

/**
 * Created by RICVA on 07/07/2017.
 */
public class harvestPrivileges extends AbstractDecorator {
    private int quantity;
    public harvestPrivileges(int quantity, iCard c) {
        super(c);
        this.quantity=quantity;
    }

    @Override
    public void onHarvest(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        if (fm.getValue()>=getActionValue()) {
            try {
                getBoard().getPrivilegeManager().applyPrivileges(fm.owner, move, quantity); //automatically throws exception if something goes wrong
            } catch (ActionAbortedException e) {
                throw e;
            }
            try {
                super.onHarvest(move, fm);
            } catch (ActionAbortedException e) {
                try {
                    getBoard().getPrivilegeManager().undoPrivileges(fm.owner, move);
                } catch (Exception e1) {
                    //this should NOT happen.
                    e1.printStackTrace();
                }
                throw e;
            }
        }
    }

    @Override
    public void undoOnHarvest(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        if (fm.getValue()>=getActionValue()) {
            try {
                getBoard().getPrivilegeManager().undoPrivileges(fm.owner, move);
            } catch (Exception e1) {
                //this should NOT happen.
                e1.printStackTrace();
            }
            super.undoDrawCard(move, fm);
        }
    }


}
