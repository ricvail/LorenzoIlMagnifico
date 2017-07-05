package it.polimi.ingsw.pc42.Control.DevelopmentCards;


import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.AbstractDecorator;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.iCard;
import it.polimi.ingsw.pc42.Model.FamilyMember;

public class PrivilegeImmediateBonus extends AbstractDecorator {
    private int quantity;
    public PrivilegeImmediateBonus(int quantity, iCard c) {
        super(c);
        this.quantity=quantity;
    }

    @Override
    public void drawCard(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        if (!move.has("immediateEffect")){
            throw new ActionAbortedException(false, "Missing privileges choices");
        }
        try {
            getBoard().getPrivilegeManager().applyPrivileges(fm.owner, move.get("immediateEffect"),quantity); //automatically throws exception if something goes wrong
        } catch (ActionAbortedException e){
            e.level++;
            throw e;
        }
        try {
            super.drawCard(move, fm);
        }catch (ActionAbortedException e){
            try {
                getBoard().getPrivilegeManager().undoPrivileges(fm.owner, move.get("immediateEffect"));
            } catch (Exception e1) {
                //this should NOT happen.
                e1.printStackTrace();
            }
            throw e;
        }
    }

    @Override
    public void undoDrawCard(JsonNode move, FamilyMember fm) {
        try {
            getBoard().getPrivilegeManager().undoPrivileges(fm.owner, move.get("immediateEffect"));
        } catch (Exception e1) {
            //this should NOT happen.
            e1.printStackTrace();
        }
        super.undoDrawCard(move, fm);
    }
}
