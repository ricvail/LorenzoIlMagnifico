package it.polimi.ingsw.pc42.DevelopmentCards;

import it.polimi.ingsw.pc42.Player;

public class FaithPointsImmediateBonus extends AbstractDecorator{
    int q;

    @Override
    public void applyDrawEffect(Player player) {
        try {
            player.faithPoints.add(q);
        } catch (Exception e){

        }
        super.applyDrawEffect(player);
    }

    public FaithPointsImmediateBonus(int quantity, iCard c) {
        super(c);
        q =quantity;
    }
}
