package it.polimi.ingsw.pc42.DevelopmentCards;

import it.polimi.ingsw.pc42.Player;


public class StoneImmediateBonus extends AbstractDecorator {

    int q;

    @Override
    public void applyDrawEffect(Player player) {
        try {
            player.stone.add(q);
        } catch (Exception e){

        }
        super.applyDrawEffect(player);
    }

    public StoneImmediateBonus(int quantity, iCard c) {
        super(c);
        q =quantity;
    }
}
