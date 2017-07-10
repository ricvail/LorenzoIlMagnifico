package it.polimi.ingsw.pc42.Control.DevelopmentCards.Permanent;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.AbstractDecorator;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.iCard;
import it.polimi.ingsw.pc42.Model.FamilyMember;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by RICVA on 07/07/2017.
 */
public class productionPrivileges extends AbstractDecorator {
    private int quantity;
    private Logger logger;
    public productionPrivileges(int quantity, iCard c) {
        super(c);
        this.quantity=quantity;
        logger= LogManager.getLogger();
    }

    @Override
    public void onProduction(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        if (fm.getValue()>=getActionValue()) {
            try {
                getBoard().getPrivilegeManager().applyPrivileges(fm.owner, move, quantity); //automatically throws exception if something goes wrong
            } catch (ActionAbortedException e) {
                throw e;
            }
            try {
                super.onProduction(move, fm);
            } catch (ActionAbortedException e) {
                try {
                    getBoard().getPrivilegeManager().undoPrivileges(fm.owner, move);
                } catch (Exception e1) {
                    logger.error(e1);
                }
                throw e;
            }
        }
    }

    @Override
    public void undoOnProduction(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        if (fm.getValue()>=getActionValue()) {
            try {
                getBoard().getPrivilegeManager().undoPrivileges(fm.owner, move);
            } catch (Exception e1) {
                logger.error(e1);
            }
            super.undoOnProduction(move, fm);
        }
    }


}
