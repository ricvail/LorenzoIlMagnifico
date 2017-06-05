package it.polimi.ingsw.pc42.ActionSpace;

import it.polimi.ingsw.pc42.Dice;

/**
 * Created by RICVA on 05/06/2017.
 */
public class NonRandomDice extends Dice{
    int fixedValue;
    public NonRandomDice(DiceColor color, int value) {
        super(color);
        fixedValue=value;
    }

    @Override
    public int getValue() {
        return fixedValue;
    }
}
