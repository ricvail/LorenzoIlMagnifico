package it.polimi.ingsw.pc42.DevelopmentCards;

import it.polimi.ingsw.pc42.Player;

public interface iCard {

    boolean drawRequirementCheck(Player player);

    void applyDrawEffect(Player player);

    void applyEndgameEffect(Player player);

    int getEra();

    String getName();

    Card.CardType getCardType();

}
