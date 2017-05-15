package it.polimi.ingsw.pc42;

import it.polimi.ingsw.pc42.Utilities.IntWrapper;
import it.polimi.ingsw.pc42.Utilities.iIntWrapper;

public class Player {
    public final iIntWrapper stone, wood, servant, coin;
    public final iIntWrapper victoryPoints, militaryPoints, faithPoints;
    private PlayerColor color;
    //private final AbstractClient client;

    public PlayerColor getColor() {
        return color;
    }

    public void setColor(PlayerColor color) {
        this.color = color;
    }


    public Player() {
        stone = new IntWrapper();
        wood = new IntWrapper();
        servant = new IntWrapper();
        coin = new IntWrapper();
        militaryPoints = new IntWrapper();
        victoryPoints = new IntWrapper();
        faithPoints = new IntWrapper();
    }


    public enum PlayerColor {
        RED, GREEN, BLUE, YELLOW
    }
}
