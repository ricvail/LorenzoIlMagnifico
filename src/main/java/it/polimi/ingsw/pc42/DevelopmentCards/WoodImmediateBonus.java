package it.polimi.ingsw.pc42.DevelopmentCards;

import it.polimi.ingsw.pc42.Player;

public class WoodImmediateBonus extends AbstractDecorator {

    int q;

    @Override
    public void applyDrawEffect(Player player) {
        try {
            player.wood.add(q);
        } catch (Exception e){

        }
        super.applyDrawEffect(player);
    }

    public WoodImmediateBonus(int quantity, iCard c) {
        super(c);
        q =quantity;
    }
}
