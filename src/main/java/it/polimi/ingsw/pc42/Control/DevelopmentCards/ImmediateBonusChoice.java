package it.polimi.ingsw.pc42.Control.DevelopmentCards;

import com.fasterxml.jackson.databind.JsonNode;
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
    public void applyDrawEffect(Player player, JsonNode json) {
        choices.get(json.get("paymentChoice").asInt()).applyDrawEffect(player, json);

    }

}
