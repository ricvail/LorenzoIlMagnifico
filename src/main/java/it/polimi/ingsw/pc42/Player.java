package it.polimi.ingsw.pc42;

import it.polimi.ingsw.pc42.DevelopmentCards.iCard;
import it.polimi.ingsw.pc42.Utilities.ResourceWrapper;
import it.polimi.ingsw.pc42.Utilities.iResourceWrapper;

import java.util.ArrayList;

public class Player {
    private PlayerColor color;
    private ArrayList<iCard> cardsTakenArrayList;
    private ArrayList<FamilyMember> familyMemberArrayList;
    //private final AbstractClient client;

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
        resources.add(new ResourceWrapper(ResourceType.FAITH));
        resources.add(new ResourceWrapper(ResourceType.MILITARY));
        resources.add(new ResourceWrapper(ResourceType.VICTORY));

        cardsTakenArrayList = new ArrayList<>();
        familyMemberArrayList = new ArrayList<>();
    }

    public iResourceWrapper getResource(ResourceType rt){
        for (iResourceWrapper res: resources) {
            if (res.getResourceType()==rt) return res;
        }
        return null;
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
