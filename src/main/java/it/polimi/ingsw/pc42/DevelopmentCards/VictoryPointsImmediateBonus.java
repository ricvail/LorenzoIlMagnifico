package it.polimi.ingsw.pc42.DevelopmentCards;

import it.polimi.ingsw.pc42.Player;

public class VictoryPointsImmediateBonus extends AbstractDecorator {
    int q;

    @Override
    public void applyDrawEffect(Player player) {
        try {
            player.victoryPoints.add(q);
        } catch (Exception e){

        }
        super.applyDrawEffect(player);
    }

    public VictoryPointsImmediateBonus(int quantity, iCard c) {
        super(c);
        q =quantity;
    }
}
