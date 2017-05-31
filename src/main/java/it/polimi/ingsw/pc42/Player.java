package it.polimi.ingsw.pc42;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

    public JsonNode generateJsonDescription(){
        JsonNodeFactory factory=JsonNodeFactory.instance;
        ObjectNode root= factory.objectNode();
        root.put("color", this.getColor().getPlayerColorString());
        for (iResourceWrapper rw:resources){
            root.put(rw.getResourceType().getString(), rw.get());
        }
        ArrayNode listOfTerritoriesCards=factory.arrayNode();
        ArrayNode listOfCharacterCards=factory.arrayNode();
        ArrayNode listOfBuildingsCards=factory.arrayNode();
        ArrayNode listOfVenturesCards=factory.arrayNode();
        for (iCard card: cardsOwned) {
            if (card.getCardType().getString().equalsIgnoreCase("territories")) {
                listOfTerritoriesCards.add(card.getJSONDescriptionOfCards());
            }
            if (card.getCardType().getString().equalsIgnoreCase("characters")) {
                listOfCharacterCards.add(card.getJSONDescriptionOfCards());
            }
            if (card.getCardType().getString().equalsIgnoreCase("buildings")) {
                listOfBuildingsCards.add(card.getJSONDescriptionOfCards());
            }
            if (card.getCardType().getString().equalsIgnoreCase("ventures")) {
                listOfVenturesCards.add(card.getJSONDescriptionOfCards());
            }
        }
        root.set("territories", listOfTerritoriesCards);
        root.set("characters", listOfCharacterCards);
        root.set("buildings", listOfBuildingsCards);
        root.set("ventures", listOfVenturesCards);
        return root;
    }
    
    public ArrayList<FamilyMember> getFamilyMembers() {
        return familyMembers;
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
        RED("red"), GREEN("green"), BLUE("blue"), YELLOW("yellow");

        private String playerColor;

        PlayerColor(String playerColor){
            this.playerColor=playerColor;
        }

        public String getPlayerColorString(){return playerColor;}
    }
}
