package it.polimi.ingsw.pc42.DevelopmentCards;

import it.polimi.ingsw.pc42.Player;

public abstract class AbstractDecorator implements iCard{

    protected iCard card;

    public AbstractDecorator(iCard c){
        card=c;
    }

    public boolean drawRequirementCheck(Player player) {
        return card.drawRequirementCheck(player);
    }

    public void applyDrawEffect(Player player) {
        card.applyDrawEffect(player);
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
