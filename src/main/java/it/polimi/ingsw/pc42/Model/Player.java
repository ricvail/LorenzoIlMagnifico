package it.polimi.ingsw.pc42.Model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.Card;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.iCard;
import it.polimi.ingsw.pc42.Control.ResourceType;
import it.polimi.ingsw.pc42.Control.ResourceWrapper;
import it.polimi.ingsw.pc42.Control.iResourceWrapper;

import java.util.ArrayList;
import java.util.Iterator;

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

    public Player (PlayerColor color){
        this();
        setColor(color);
    }

    public static  Player fromColorString(String color){
        PlayerColor col= null;
        try {
            col= PlayerColor.fromString(color);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Player(col);
    }


    public Player() {
        resources=new ArrayList<>();
        resources.add(new ResourceWrapper(ResourceType.COIN, 0));
        resources.add(new ResourceWrapper(ResourceType.SERVANT,3));
        resources.add(new ResourceWrapper(ResourceType.STONE,2));
        resources.add(new ResourceWrapper(ResourceType.WOOD,2));
        resources.add(new ResourceWrapper(ResourceType.FAITHPOINTS,0));
        resources.add(new ResourceWrapper(ResourceType.MILITARYPOINTS,0));
        resources.add(new ResourceWrapper(ResourceType.VICTORYPOINTS,0));

        cardsOwned = new ArrayList<>();
        familyMembers = new ArrayList<>();
        familyMembers.add(new FamilyMember(this, Dice.DiceColor.ORANGE));
        familyMembers.add(new FamilyMember(this, Dice.DiceColor.WHITE));
        familyMembers.add(new FamilyMember(this, Dice.DiceColor.BLACK));
        familyMembers.add(new FamilyMember(this, Dice.DiceColor.NEUTRAL));
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
        ArrayNode listOfFamilyMembers=factory.arrayNode();
        for (FamilyMember fm: familyMembers){
            ObjectNode fmObjectNode=new ObjectNode(factory);
            fmObjectNode.put("ownerColor", fm.owner.getColor().getPlayerColorString());
            fmObjectNode.put("familyMemberColor", fm.getDiceColor().getDiceColorString());
            fmObjectNode.put("isUsed", fm.isUsed());
            listOfFamilyMembers.add(fmObjectNode);
        }
        root.set("territories", listOfTerritoriesCards);
        root.set("characters", listOfCharacterCards);
        root.set("buildings", listOfBuildingsCards);
        root.set("ventures", listOfVenturesCards);
        return root;
    }

    public JsonNode getUnusedFamilyMembersList(){
        JsonNodeFactory factory=JsonNodeFactory.instance;
        ArrayNode list=factory.arrayNode();
        for (FamilyMember fm : familyMembers){
            if (fm.diceColor.visible && !fm.isUsed()){
                list.add(fm.diceColor.getDiceColorString());
            }
        }
        return list;
    }


    public ArrayList<FamilyMember> getFamilyMembers() {
        return familyMembers;
    }

    public FamilyMember getFamilyMemberFromColor(String s) throws Exception {
        Dice.DiceColor color = Dice.DiceColor.fromString(s);
        Iterator<FamilyMember> iterator = familyMembers.iterator();
        while (iterator.hasNext()){
            FamilyMember fm = iterator.next();
            if (fm.diceColor== color){
                return fm;
            }
        }
        throw new Exception("Could not find a family member with color "+s);
    }


    public void addCard(iCard card){
        cardsOwned.add(card);
    }
    public void removeCard(iCard card){
        cardsOwned.remove(card);
    }



    public enum PlayerColor {
        RED("red"), GREEN("green"), BLUE("blue"), YELLOW("yellow");

        private String playerColor;

        PlayerColor(String playerColor){
            this.playerColor=playerColor;
        }

        public static PlayerColor fromString(String color) throws Exception {
            for (PlayerColor pc : PlayerColor.values()) {
                if (pc.getPlayerColorString().equalsIgnoreCase(color)) {
                    return pc;
                }
            }
            throw new Exception("Invalid player color: "+ color);
        }

        public String getPlayerColorString(){return playerColor;}
    }


    public int getMaxNumberOfTerritories(){
        int maxNumberOfTerritories=0;
        int counter=0;
        for (int i=2; i<=6; i++){
            if (this.getResource(ResourceType.MILITARYPOINTS).get()<counter+i+1){
                maxNumberOfTerritories=i;
                break;
            }
            counter=counter+i+1;
        }
        return maxNumberOfTerritories;
    }
}
