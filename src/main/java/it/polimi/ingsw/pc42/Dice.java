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
        WHITE (true, "white"), BLACK(true, "black"), ORANGE(true, "orange"), NEUTRAL(true, "neutral");
        public final boolean visible;
        private String diceColor;
        DiceColor (boolean v, String diceColor){
            visible = v;
        }
        public String getDiceColorString(){return diceColor;}
    }
}
