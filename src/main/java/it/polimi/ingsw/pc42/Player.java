package it.polimi.ingsw.pc42;

import it.polimi.ingsw.pc42.DevelopmentCards.Card;
import it.polimi.ingsw.pc42.DevelopmentCards.iCard;
import it.polimi.ingsw.pc42.Utilities.ResourceWrapper;
import it.polimi.ingsw.pc42.Utilities.iResourceWrapper;

import java.util.ArrayList;

public class Player {
    private PlayerColor color;
    private ArrayList<iCard> cardsOwned;
    private ArrayList<FamilyMember> familyMembers;

    public PlayerColor getColor() {
        return color;
    }

    public void setColor(PlayerColor color) {
        this.color = color;
    }

    private ArrayList<iResourceWrapper> resources;

    public Player() {
        resources=new ArrayList<>();
        resources.add(new ResourceWrapper(ResourceType.COIN));
        resources.add(new ResourceWrapper(ResourceType.SERVANT));
        resources.add(new ResourceWrapper(ResourceType.STONE));
        resources.add(new ResourceWrapper(ResourceType.WOOD));
        resources.add(new ResourceWrapper(ResourceType.FAITHPOINTS));
        resources.add(new ResourceWrapper(ResourceType.MILITARYPOINTS));
        resources.add(new ResourceWrapper(ResourceType.VICTORYPOINTS));

        cardsOwned = new ArrayList<>();
        familyMembers = new ArrayList<>();
    }

    public iResourceWrapper getResource(ResourceType rt){
        for (iResourceWrapper res: resources) {
            if (res.getResourceType()==rt) return res;
        }
        return null;
    }

    public int getNumberOfCards(Card.CardType type){
        int i=0;
        for (iCard c:cardsOwned) {
            if (c.getCardType()==type) i++;
        }
        return i;
    }


    public void addCard(iCard card){
        cardsOwned.add(card);
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
