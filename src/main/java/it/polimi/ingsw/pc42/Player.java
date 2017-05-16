package it.polimi.ingsw.pc42;

import it.polimi.ingsw.pc42.DevelopmentCards.iCard;
import it.polimi.ingsw.pc42.Utilities.IntWrapper;
import it.polimi.ingsw.pc42.Utilities.iIntWrapper;

import java.util.ArrayList;

public class Player {
    public final iIntWrapper stone, wood, servant, coin;
    public final iIntWrapper victoryPoints, militaryPoints, faithPoints;
    private PlayerColor color;
    private ArrayList<iCard> cardTakenArrayList;
    private ArrayList<FamilyMember> familyMemberArrayList;
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
        cardTakenArrayList = new ArrayList<>();
        familyMemberArrayList = new ArrayList<>();
    }

    public void addCard( iCard card){
        // add card to cardTaken
    }

    public void addFamilyMember(FamilyMember familyMember){
        // add family
    }

    public void removeFamilyMember(FamilyMember familyMember){
        //remove fm
    }


    public enum PlayerColor {
        RED, GREEN, BLUE, YELLOW
    }
}
