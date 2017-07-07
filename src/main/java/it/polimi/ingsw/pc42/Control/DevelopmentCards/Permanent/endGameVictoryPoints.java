package it.polimi.ingsw.pc42.Control.DevelopmentCards.Permanent;

import it.polimi.ingsw.pc42.Control.DevelopmentCards.AbstractDecorator;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.iCard;
import it.polimi.ingsw.pc42.Control.ResourceType;
import it.polimi.ingsw.pc42.Model.Player;

/**
 * Created by RICVA on 07/07/2017.
 */
public class endGameVictoryPoints extends AbstractDecorator {

    int q;

    public endGameVictoryPoints(int q, iCard c) {
        super(c);
        this.q=q;
    }

    @Override
    public void applyEndgameEffect(Player player) {
        player.getResource(ResourceType.VICTORYPOINTS).add(q);
        super.applyEndgameEffect(player);
    }
}
