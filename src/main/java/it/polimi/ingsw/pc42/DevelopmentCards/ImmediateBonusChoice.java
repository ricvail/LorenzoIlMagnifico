package it.polimi.ingsw.pc42.DevelopmentCards;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Player;
import it.polimi.ingsw.pc42.ResourceType;

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
