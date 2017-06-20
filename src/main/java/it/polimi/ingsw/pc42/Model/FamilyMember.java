package it.polimi.ingsw.pc42.Model;


import it.polimi.ingsw.pc42.Control.ActionSpace.Area;

import java.util.ArrayList;

public class FamilyMember {
    public final Player owner;
    private int value;
    public final Dice.DiceColor diceColor;
    private ArrayList<Area> allowedAreas;

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    private boolean used;

    public FamilyMember(Player owner, Dice.DiceColor diceColor, ArrayList<Area> allowedAreas){
        this.owner = owner;
        this.value = 0;
        this.diceColor = diceColor;
        this.allowedAreas = allowedAreas;
    }
    public FamilyMember(Player owner, Dice.DiceColor diceColor){
        this (owner, diceColor, new ArrayList<>());
    }

    public Dice.DiceColor getDiceColor() {
        return diceColor;
    }

    public boolean canBePlacedInArea(Area area){
        if (diceColor.visible) {
            return true;
        }
        if (area==Area.NULL){
            return true;
        }
        return (allowedAreas.contains(area));
    }
    public void setValue(int diceValue){
        value=diceValue;
    }

    public  int getValue(){
        return value;
    }

}
