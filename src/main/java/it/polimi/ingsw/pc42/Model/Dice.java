package it.polimi.ingsw.pc42.Model;


public class Dice {
    private int value;
    private DiceColor color;

    public DiceColor getColor() {
        return color;
    }

    public int getValue(){
        return value;
    }

    /**
     *Class constructor. Sets the dice color.
     *
     * @param color dice color to assign
     */
    public Dice(DiceColor color){
        this.color = color;
    }

    /**
     * Generates a random value for the dice, between 1 and 6 included.
     */
    public void rollDice(){
        value = (int) (Math.floor(Math.random()*6)+1);
    }

    public enum DiceColor{
        WHITE (true, "white"), BLACK(true, "black"), ORANGE(true, "orange"), NEUTRAL(true, "neutral"), GHOST (false, "ghost");

        public final boolean visible;
        private String diceColor;

        public String getDiceColorString(){return diceColor;}

        /**
         *Class constructor. Sets the visibility and the dice color.
         *
         * @param v <code>true</code> if visible
         * @param diceColor  dice color desired
         */
        DiceColor (boolean v, String diceColor){
            visible = v;
            this.diceColor = diceColor;
        }

        /**
         *Iterates through dice colors until finds a match for the string passed as parameter. Then returns
         * a dice color or throws an exception if it doesn't find a match.
         *
         * @param dc dice color string
         * @return a dice color value of the Enum
         * @throws Exception if doesn't find a match between parameter and Enum values
         */
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
