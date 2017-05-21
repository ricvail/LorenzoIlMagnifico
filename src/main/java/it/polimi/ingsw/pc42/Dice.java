package it.polimi.ingsw.pc42;


public class Dice {
    private int value;
    private DiceColor color;

    public Dice(DiceColor color){
        this.value = 0;
        this.color = color;
    }

    public int rollDice(){
        //random number
        return 1;
    }

    public enum DiceColor{
        WHITE (true), BLACK(true), ORANGE(true), NEUTRAL(true);
        public final boolean visible;
        DiceColor (boolean v){
            visible = v;
        }
    }
}
