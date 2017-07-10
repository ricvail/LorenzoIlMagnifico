package it.polimi.ingsw.pc42.Control.DevelopmentCards;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Model.FamilyMember;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PrivilegeImmediateBonus extends AbstractDecorator {

    private int quantity;
    private Logger logger;

    /**
     * Class constructor. Decorates a card that has an effect that gives one or more privileges bonus.
     *
     * @param quantity quantity of privileges that gives
     * @param c card to be decorated
     */
    public PrivilegeImmediateBonus(int quantity, iCard c) {
        super(c);
        this.quantity=quantity;
        logger= LogManager.getLogger();
    }

    @Override
    public void drawCard(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        if (!move.has("immediateEffect")){
            ArrayNode choices = JsonNodeFactory.instance.arrayNode();
            throw new ActionAbortedException("immediateEffect", choices);
        }
        try {
            getBoard().getPrivilegeManager().applyPrivileges(fm.owner, move.get("immediateEffect"),quantity); //automatically throws exception if something goes wrong
        } catch (ActionAbortedException e){
            e.setLevel(e.getLevel()+1);
            throw e;
        }
        try {
            super.drawCard(move, fm);
        }catch (ActionAbortedException e){
            try {
                getBoard().getPrivilegeManager().undoPrivileges(fm.owner, move.get("immediateEffect"));
            } catch (Exception e1) {
                logger.error(e1);
            }
            throw e;
        }
    }

    @Override
    public void undoDrawCard(JsonNode move, FamilyMember fm) {
        try {
            getBoard().getPrivilegeManager().undoPrivileges(fm.owner, move.get("immediateEffect"));
        } catch (Exception e1) {
            logger.error(e1);
        }
        super.undoDrawCard(move, fm);
    }
}
