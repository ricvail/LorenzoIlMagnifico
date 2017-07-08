package it.polimi.ingsw.pc42.Control.DevelopmentCards.Permanent;

import it.polimi.ingsw.pc42.Control.DevelopmentCards.AbstractDecorator;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.iCard;

/**
 * Created by RICVA on 07/07/2017.
 */
public class harvestPrivileges extends AbstractDecorator {
    private int quantity;
    public harvestPrivileges(int quantity, iCard c) {
        super(c);
        this.quantity=quantity;
    }


}
