package it.polimi.ingsw.pc42.DevelopmentCards.ToDo;


import it.polimi.ingsw.pc42.DevelopmentCards.AbstractDecorator;
import it.polimi.ingsw.pc42.DevelopmentCards.iCard;

public class ExtraMoveImmediateBonus extends AbstractDecorator {
    public ExtraMoveImmediateBonus(int quantity, iCard c) {
        super(c);
    }
}
