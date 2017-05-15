package it.polimi.ingsw.pc42.DevelopmentCards;

import it.polimi.ingsw.pc42.Player;

/**
 * Created by RICVA on 15/05/2017.
 */
public class ServantImmediateBonus extends AbstractDecorator{
    private int q;
    public ServantImmediateBonus(int quantity, iCard c) {
        super(c);
        q=quantity;
    }

    @Override
    public void applyDrawEffect(Player player) {
        try {
            player.servant.add(q);
        } catch (Exception e){

        }
        super.applyDrawEffect(player);
    }
}
