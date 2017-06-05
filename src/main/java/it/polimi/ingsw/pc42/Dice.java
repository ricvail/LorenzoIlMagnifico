package it.polimi.ingsw.pc42;


public class Dice {
    private int value;

    public DiceColor getColor() {
        return color;
    }

    private DiceColor color;

    public Dice(DiceColor color){
        this.color = color;
    }

    public void rollDice(){
        value = (int) (Math.floor(Math.random()*6)+1);
    }
    public int getValue(){
        return value;
    }

    public enum DiceColor{
        WHITE (true, "white"), BLACK(true, "black"), ORANGE(true, "orange"), NEUTRAL(true, "neutral");
        public final boolean visible;
        private String diceColor;
        DiceColor (boolean v, String diceColor){
            visible = v;
            this.diceColor = diceColor;
        }
        public String getDiceColorString(){return diceColor;}
        public static DiceColor fromString(String dc) throws Exception {
            for (DiceColor color : DiceColor.values()) {
                if (color.diceColor.equalsIgnoreCase(dc)) {
                    return color;
                }
            }
            throw new Exception(dc+" is not a valid dice color");
        }
    }
}
