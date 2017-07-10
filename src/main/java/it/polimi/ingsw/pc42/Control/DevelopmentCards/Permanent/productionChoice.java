package it.polimi.ingsw.pc42.Control.DevelopmentCards.Permanent;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.AbstractDecorator;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.iCard;
import it.polimi.ingsw.pc42.Model.FamilyMember;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class productionChoice extends AbstractDecorator{
    public final ArrayList<iCard> choices;
    private Logger logger;
    public final String fieldName="optionToActivate";
    public productionChoice(iCard c) {
        super(c);
        choices=new ArrayList<>();
        logger= LogManager.getLogger();
    }

    public void addChoice(){
        choices.add(card);
    }

    @Override
    public void onProduction(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        if (move.has(fieldName)&&move.get(fieldName).isInt()&&
                move.get(fieldName).asInt()<choices.size()){
            choices.get(move.get(fieldName).asInt()).onProduction(move, fm);
        } else{
            JsonNodeFactory factory=JsonNodeFactory.instance;
            ArrayNode list=factory.arrayNode();
            list.add(choices.size());
            throw new ActionAbortedException(fieldName, list);
        }

    }

    @Override
    public void undoOnProduction(JsonNode move, FamilyMember fm) {
        try {
            choices.get(move.get(fieldName).asInt()).undoOnProduction(move, fm);
        } catch (ActionAbortedException e) {
            logger.error(e);
        }
    }
}
