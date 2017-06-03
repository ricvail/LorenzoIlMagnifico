package it.polimi.ingsw.pc42;


public class FamilyMember {
    public final Player owner;
    private int value;
    public final Dice.DiceColor diceColor;

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    private boolean used;

    public FamilyMember(Player owner, Dice.DiceColor diceColor){
        this.owner = owner;
        this.value = 0;
        this.diceColor = diceColor;
    }

    public void setValue(int diceValue){
        // refresh dice value
    }

    public  int getValue(){
        return value;//TODO get from board instead of using private var
    }

    public Dice.DiceColor getDiceColor() {
        return diceColor;
    }
}
