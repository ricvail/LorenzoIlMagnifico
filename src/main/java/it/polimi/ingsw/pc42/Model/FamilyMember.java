package it.polimi.ingsw.pc42.Model;


import it.polimi.ingsw.pc42.Control.ActionSpace.Area;

import java.util.ArrayList;

public class FamilyMember {
    public final Player owner;
    private int value;
    public final Dice.DiceColor diceColor;
    private ArrayList<Area> allowedAreas;
    private boolean used;

    /**
     * Class constructor that sets the owner, the dice color and a list of allowed areas.
     *
     * @param owner player that own the family member
     * @param diceColor dice color to which the family member is tied
     * @param allowedAreas list of the allowed areas for thee family member
     */
    public FamilyMember(Player owner, Dice.DiceColor diceColor, ArrayList<Area> allowedAreas){
        this.owner = owner;
        this.value = 0;
        this.diceColor = diceColor;
        this.allowedAreas = allowedAreas;
    }

    /**
     *Class constructor that takes the owner and the dice color and calls the overloaded constructor passing
     * and initializing a new list.
     *
     * @param owner player to which the family member belongs
     * @param diceColor dice color to which the family member is tied
     */
    public FamilyMember(Player owner, Dice.DiceColor diceColor){
        this (owner, diceColor, new ArrayList<>());
    }

    public void setValue(int diceValue){
        value=diceValue;
    }

    public  int getValue(){
        return value;
    }

    public Dice.DiceColor getDiceColor() {
        return diceColor;
    }

    /**
     * Returns the <code>boolean</code> attribute used, that represent the usage of a family member.
     *
     * @return <code>true</code> if the family member is used
     */
    public boolean isUsed() {
        return used;
    }

    /**
     *Sets the attribute used, that represent the usage of a family member, to <code>true</code> or <code>false</code>.
     *
     * @param used  usage of the family member that has to be set
     */
    public void setUsed(boolean used) {
        this.used = used;
    }

    /**
     *It takes an Area, or null, as a parameter and checks if it is present in the allowed areas or the family member
     *  has a visible dice color or the parameter is null.
     *
     * @param area area in which it would be placed
     * @return <code>true</code> if dice color is visible or param is null or is contained in allowed areas
     */
    public boolean canBePlacedInArea(Area area){
        if (diceColor.visible) {
            return true;
        }
        if (area==Area.NULL){
            return true;
        }
        return (allowedAreas.contains(area));
    }
}
