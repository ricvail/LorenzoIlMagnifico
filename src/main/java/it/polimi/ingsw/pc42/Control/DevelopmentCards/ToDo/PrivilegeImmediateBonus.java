package it.polimi.ingsw.pc42.Control.DevelopmentCards.ToDo;


import it.polimi.ingsw.pc42.Control.DevelopmentCards.AbstractDecorator;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.iCard;

public class PrivilegeImmediateBonus extends AbstractDecorator {
    public PrivilegeImmediateBonus(int quantity, iCard c) {
        super(c);
    }
}