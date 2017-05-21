package it.polimi.ingsw.pc42.DevelopmentCards;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Player;

public abstract class AbstractDecorator implements iCard{

    protected iCard card;

    public AbstractDecorator(iCard c){
        card=c;
    }

    public boolean drawRequirementCheck(Player player) {
        return card.drawRequirementCheck(player);
    }

    public void applyDrawEffect(Player player, JsonNode json) {
        card.applyDrawEffect(player, json);
    }

    public void applyEndgameEffect(Player player) {
        card.applyEndgameEffect(player);
    }

    public int getEra() {
        return card.getEra();
    }

    public String getName() {
        return card.getName();
    }

    public Card.CardType getCardType() {
        return card.getCardType();
    }

}
