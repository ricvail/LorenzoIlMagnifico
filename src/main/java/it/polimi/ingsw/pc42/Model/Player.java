package it.polimi.ingsw.pc42.Model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Control.ActionSpace.ActionDecorator;
import it.polimi.ingsw.pc42.Control.ActionSpace.iActionSpace;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.Card;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.iCard;
import it.polimi.ingsw.pc42.Control.ResourceType;
import it.polimi.ingsw.pc42.Control.ResourceWrapper;
import it.polimi.ingsw.pc42.Control.iResourceWrapper;
import it.polimi.ingsw.pc42.Utilities.GameInitializer;
import it.polimi.ingsw.pc42.Utilities.PersonalBonusTileParser;
import it.polimi.ingsw.pc42.Utilities.myException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;

public class Player {


    private PlayerColor color;
    private ArrayList<iCard> cardsOwned;
    private ArrayList<FamilyMember> familyMembers;

    public ArrayList<ResourceWrapper> getResources() {
        return resources;
    }

    private ArrayList<ResourceWrapper> resources;
    private PersonalBonusTile bonusTile;
    private static Logger logger=LogManager.getLogger();
    private boolean isAdvanced;


    public void enableAdvanced(){
        isAdvanced=true;
    }

    public PlayerColor getColor() {
        return color;
    }

    public void setColor(PlayerColor color) {
        this.color = color;
    }

    public ArrayList<FamilyMember> getFamilyMembers() {
        return familyMembers;
    }

    public void applyEndGameEffects (){
        for (iCard c : cardsOwned){
            c.applyEndgameEffect(this);
        }
    }

    /**
     * Factory method that delegates the initialization of a player and returns it, given his color string,
     * if it matches a PlayerColor Enum value.
     *
     * @param color string that represent a player color
     * @return new player
     */
    public static  Player createPlayer(String color){
        PlayerColor col= null;
        try {
            col= PlayerColor.fromString(color);
        } catch (Exception e) {
            logger.error(e);
        }
        return createPlayer(col);
    }

    /**
     * Returns a reference to a player, given a player color, after parsing the bonuses of the personal tile.
     *
     * @param color player's color
     * @return reference to a new player
     */
    public static  Player createPlayer(PlayerColor color){
        PersonalBonusTile tile = PersonalBonusTileParser.parse(GameInitializer.getDefaultBonusTileJson());
        return new Player(color, tile);
    }

    /**
     * Private class constructor. It sets the player color and bonus tile.
     *
     * @param color PlayerColor that identifies the player
     * @param bonuses values, tied to a player, of the bonus that the base harvest and production actions activate
     */
    private Player (PlayerColor color, PersonalBonusTile bonuses){
        this();
        setColor(color);
        bonusTile=bonuses;
        logger= LogManager.getLogger();
    }

    /**
     * Private class constructor. Create the class Player that will hold resources, family member and cards owned.
     */
    private Player() {
        isAdvanced=false;
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

    public void performHarvest (JsonNode move, FamilyMember fm)throws ActionAbortedException {
        PersonalBonusTile.applyBonuses(bonusTile.getHarvestBonuses(), this);
        if (!isAdvanced) return;
        int i = 0;
        for (int j = 0; j<cardsOwned.size(); j++){
            if (cardsOwned.get(j).getCardType()== Card.CardType.TERRITORY) {
                try {
                    if (!(move.has("cardChoices") && move.get("cardChoices").size() > i)) {
                        throw new ActionAbortedException("cardChoices", JsonNodeFactory.instance.arrayNode());
                    }
                    cardsOwned.get(j).onHarvest(move.get("cardChoices").get(i), fm);
                } catch (ActionAbortedException e) {
                    e.setCardChoice(true);
                    e.setCard(j);
                    PersonalBonusTile.undoBonuses(bonusTile.getHarvestBonuses(), this);
                    j--;
                    i--;
                    for (; j >= 0; j--) {
                        if (cardsOwned.get(j).getCardType() == Card.CardType.TERRITORY) {
                            try {
                                cardsOwned.get(j).undoOnHarvest(move.get("cardChoices").get(i), fm);
                            } catch (ActionAbortedException ex) {
                                logger.error(ex);
                            }
                            i--;
                        }
                    }
                    throw e;
                }
                i++;
            }
        }
    }

    public void undoHarvest(JsonNode move, FamilyMember fm) {
        PersonalBonusTile.undoBonuses(bonusTile.getHarvestBonuses(), this);
        if (!isAdvanced) return;
        int j = 0;
        for (int i = 0; i<cardsOwned.size(); i++){
            if (cardsOwned.get(i).getCardType()== Card.CardType.TERRITORY) {
                try {
                    cardsOwned.get(i).undoOnHarvest(move.get("cardChoices").get(j), fm);
                } catch (ActionAbortedException e) {
                    logger.error(e);
                }
                j++;
            }
        }
    }
    public void performProduction (JsonNode move, FamilyMember fm)throws ActionAbortedException {
        setAccumulate(true);
        PersonalBonusTile.applyBonuses(bonusTile.getProductionBonuses(), this);
        if (!isAdvanced){
            setAccumulate(false);
            return;
        }
        int i = 0;
        for (int j = 0; j<cardsOwned.size(); j++){
            if (cardsOwned.get(j).getCardType()== Card.CardType.BUILDING) {
                try {
                    if (!(move.has("cardChoices") && move.get("cardChoices").size() > i)) {
                        throw new ActionAbortedException("cardChoices", JsonNodeFactory.instance.arrayNode());
                    }
                    cardsOwned.get(j).onProduction(move.get("cardChoices").get(i), fm);
                } catch (ActionAbortedException e) {
                    setAccumulate(false);
                    e.setCardChoice(true);
                    e.setCard(j);
                    PersonalBonusTile.undoBonuses(bonusTile.getProductionBonuses(), this);
                    j--;
                    i--;
                    for (; j >= 0; j--) {
                        if (cardsOwned.get(j).getCardType() == Card.CardType.BUILDING) {
                            try {
                                cardsOwned.get(j).undoOnProduction(move.get("cardChoices").get(i), fm);
                            } catch (ActionAbortedException ex) {
                                logger.error(ex); //this is not expected to happen
                            }
                            i--;
                        }
                    }
                    throw e;
                }
                i++;
            }
        }
        setAccumulate(false);
    }

    public void setAccumulate(boolean acc){
        for (ResourceWrapper r:resources) {
            r.setAccumulatorEnabled(acc);
        }
    }

    public void undoProduction(JsonNode move, FamilyMember fm) {
        PersonalBonusTile.undoBonuses(bonusTile.getProductionBonuses(), this);
        if (!isAdvanced) return;
        int j = 0;
        for (int i = 0; i<cardsOwned.size(); i++){
            if (cardsOwned.get(i).getCardType()== Card.CardType.BUILDING) {
                try {
                    cardsOwned.get(i).undoOnProduction(move.get("cardChoices").get(j), fm);
                } catch (ActionAbortedException e) {
                    logger.error(e);
                }
                j++;
            }
        }
    }

    public void applyPermanentBonuses(JsonNode move, FamilyMember fm, iActionSpace space){
        for (int i = 0; i<cardsOwned.size(); i++){
            cardsOwned.get(i).onAction(move, fm, space);
        }
    }

    public void undoApplyPermanentBonuses(JsonNode move, FamilyMember fm, iActionSpace space){
        for (int i = 0; i<cardsOwned.size(); i++){
            cardsOwned.get(i).undoOnAction(move, fm, space);
        }
        for (ResourceWrapper w:resources) {
            w.resetBonus();
        }
    }
    /**
     * iterate through the resources, until it finds a match for the parameter and then returns a
     * <code>ResourceWrapper</code> or null, if the resource type does not exist.
     *
     * @param rt resource type
     * @return a <code>ResourceWrapper</code> of a specific resource or null
     */
    public ResourceWrapper getResource(ResourceType rt){
        for (ResourceWrapper res: resources) {
            if (res.getResourceType()==rt) return res;
        }
        return null;
    }

    /**
     * Returns the number of cards of a certain type, owned by the player.
     *
     * @param type type of the card
     * @return number of cards of a specific type
     */
    public int getNumberOfCards(Card.CardType type){
        int i=0;
        for (iCard c:cardsOwned) {
            if (c.getCardType()==type) i++;
        }
        return i;
    }

    /**
     * Returns an already mapped <code>JsonNode</code> that describe the state of the player:
     *  -the color
     *  -the resources
     *  -the cards owned
     *  -the family members and if they're used
     *
     * @return a description of the state of the player, at the given moment
     */
    public JsonNode generateJsonDescription(){
        JsonNodeFactory factory=JsonNodeFactory.instance;
        ObjectNode root= factory.objectNode();
        root.put("color", this.getColor().getPlayerColorString());
        for (iResourceWrapper rw:resources){
            root.put(rw.getResourceType().getString(), rw.get());
        }
        root.set("bonusTiles", bonusTile.getJson());
        ArrayNode listOfTerritoriesCards=factory.arrayNode();
        ArrayNode listOfCharacterCards=factory.arrayNode();
        ArrayNode listOfBuildingsCards=factory.arrayNode();
        ArrayNode listOfVenturesCards=factory.arrayNode();
        for (iCard card: cardsOwned) {
            if ("territories".equalsIgnoreCase(card.getCardType().getString())) {
                listOfTerritoriesCards.add(card.getJSONDescriptionOfCards());
            }
            if ("characters".equalsIgnoreCase(card.getCardType().getString())) {
                listOfCharacterCards.add(card.getJSONDescriptionOfCards());
            }
            if ("buildings".equalsIgnoreCase(card.getCardType().getString())) {
                listOfBuildingsCards.add(card.getJSONDescriptionOfCards());
            }
            if ("ventures".equalsIgnoreCase(card.getCardType().getString())) {
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
        root.set("familyMembers", listOfFamilyMembers);
        root.set("territories", listOfTerritoriesCards);
        root.set("characters", listOfCharacterCards);
        root.set("buildings", listOfBuildingsCards);
        root.set("ventures", listOfVenturesCards);
        root.set("bonusTiles", bonusTile.getJson());
        return root;
    }

    /**
     * Return a <code>JsonNode</code>  which has all the unused and visible family members as nodes.
     *
     * @return the unused and visible family members
     */
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

    /**
     * Iterates through the family members, until it finds a match for the color passed as a string and then returns
     * the tied family member or throws an Exception.
     *
     * @param s the string of the color of which is needed the family member reference
     * @return a reference to a family member
     * @throws Exception if it could not find a family member of the specific color
     */
    public FamilyMember getFamilyMemberFromColor(String s) throws myException {
        Dice.DiceColor color = Dice.DiceColor.fromString(s);
        Iterator<FamilyMember> iterator = familyMembers.iterator();
        while (iterator.hasNext()){
            FamilyMember fm = iterator.next();
            if (fm.diceColor== color){
                return fm;
            }
        }
        throw new myException("Could not find a family member with color "+s);
    }

    /**
     * Adds a card to the cards owned by the player.
     *
     * @param card card to add
     */
    public void addCard(iCard card){
        cardsOwned.add(card);
    }

    /**
     * Removes a card from the cards owned by the player.
     *
     * @param card card to remove
     */
    public void removeCard(iCard card){
        cardsOwned.remove(card);
    }

    /**
     * Returns the max number of cards of type territories that the player on which is called the method can own,
     * checking his military points.
     *
     * @return max number of cards of type territories that the player can own
     */
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

    /**
     * The permissible colors for the players.
     */
    public enum PlayerColor {
        RED("red"), GREEN("green"), BLUE("blue"), YELLOW("yellow");

        private String playerColor;

        public String getPlayerColorString(){return playerColor;}

        /**
         * Enum constructor. Set the string attribute according to the parameter.
         *
         * @param playerColor string of the player color
         */
        PlayerColor(String playerColor){
            this.playerColor=playerColor;
        }

        /**
         * Returns a player color, if it finds a match for the color string passed as parameter,
         * iterating over the Enum values.
         *
         * @param color player's color string
         * @return a player color if matches the parameter
         * @throws Exception if it doesn't find a match in the player color Enum values
         */
        public static PlayerColor fromString(String color) throws myException {
            for (PlayerColor pc : PlayerColor.values()) {
                if (pc.getPlayerColorString().equalsIgnoreCase(color)) {
                    return pc;
                }
            }
            throw new myException("Invalid player color: "+ color);
        }
    }
}
