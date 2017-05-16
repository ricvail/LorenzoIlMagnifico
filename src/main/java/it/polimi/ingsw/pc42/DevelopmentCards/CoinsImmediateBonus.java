package it.polimi.ingsw.pc42.DevelopmentCards;

import it.polimi.ingsw.pc42.Player;

/**
 * Created by Paolo on 16/05/2017.
 */
public class CoinsImmediateBonus extends AbstractDecorator {
    int q;

    @Override
    public void applyDrawEffect(Player player) {
        try {
            player.coin.add(q);
        } catch (Exception e){

        }
        super.applyDrawEffect(player);
    }

    public CoinsImmediateBonus(int quantity, iCard c) {
        super(c);
        q =quantity;
    }
}
