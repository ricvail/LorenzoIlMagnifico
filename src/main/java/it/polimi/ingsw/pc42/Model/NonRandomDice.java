package it.polimi.ingsw.pc42.Model;

/**
 * Created by RICVA on 05/06/2017.
 */
public class NonRandomDice extends Dice{

    int fixedValue;

    @Override
    public int getValue() {
        return fixedValue;
    }

    /**
     * Class constructor. Extends <code>Dice</code>, sets a non-random fixed value. Mostly for testing purpose.
     *
     * @param color color of the dice to be created
     * @param value permanent value to be assigned to the dice
     */
    public NonRandomDice(DiceColor color, int value) {
        super(color);
        fixedValue=value;
    }
}
