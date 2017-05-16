package it.polimi.ingsw.pc42.DevelopmentCards;

import it.polimi.ingsw.pc42.Player;

public class MilitaryPointsImmediateBonus extends AbstractDecorator {
    int q;

    @Override
    public void applyDrawEffect(Player player) {
        try {
            player.militaryPoints.add(q);
        } catch (Exception e){

        }
        super.applyDrawEffect(player);
    }

    public MilitaryPointsImmediateBonus(int quantity, iCard c) {
        super(c);
        q =quantity;
    }

}
