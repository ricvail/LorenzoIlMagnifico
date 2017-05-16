package it.polimi.ingsw.pc42;


public class FamilyMember {
    public final Player owner;
    private int value;
    public final Dice.DiceColor diceColor;

    public FamilyMember(Player owner, Dice.DiceColor diceColor){
        this.owner = owner;
        this.value = 0;
        this.diceColor = diceColor;
    }

    public void setValue(int diceValue){
        // refresh dice value
    }

}
