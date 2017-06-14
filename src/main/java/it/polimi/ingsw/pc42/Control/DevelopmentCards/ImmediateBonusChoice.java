package it.polimi.ingsw.pc42.Control.DevelopmentCards;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Model.FamilyMember;
import it.polimi.ingsw.pc42.Model.Player;

import java.util.ArrayList;

public class ImmediateBonusChoice extends AbstractDecorator {

    public final ArrayList<iCard> choices;

    public ImmediateBonusChoice(iCard c) {
        super(c);
        choices=new ArrayList<>();
    }

    public void addChoice(){
        choices.add(card);
    }

    @Override
    public void drawCard(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        if (move.has("paymentChoice")&&move.get("paymentChoice").isInt()&&
                move.get("paymentChoice").asInt()<choices.size()){
            choices.get(move.get("paymentChoice").asInt()).drawCard(move, fm);
        } else{
            JsonNodeFactory factory=JsonNodeFactory.instance;
            ArrayNode list=factory.arrayNode();
            //TODO list of available choices
            throw new ActionAbortedException("paymentChoice", list);
        }

    }

    @Override
    public void undoDrawCard(JsonNode move, FamilyMember fm) {
        choices.get(move.get("paymentChoice").asInt()).undoDrawCard(move, fm);
    }
}
