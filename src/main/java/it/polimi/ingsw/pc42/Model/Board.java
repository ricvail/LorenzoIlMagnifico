package it.polimi.ingsw.pc42.Model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.polimi.ingsw.pc42.Control.MoveManager;
import it.polimi.ingsw.pc42.Control.ActionSpace.iActionSpace;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.Card;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.iCard;
import it.polimi.ingsw.pc42.Control.PrivilegeManager;
import it.polimi.ingsw.pc42.Control.ResourceType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Created by diego on 28/05/2017.
 */
public class Board {
    private int era;
    private int round;
    private Player currentPlayer;
//private int currentTurn;
    private ArrayList<Player> playerArrayList;
    private ArrayList<iActionSpace> actionSpaces;
    private ArrayList<iCard> cards;
    private int councilID;
    private boolean councilHasBeenSet;
    private ArrayList<Dice> dices;
    private PrivilegeManager privilegesManager;
    private ArrayNode spacesDescription;
    private boolean isGameOver;

    private boolean vatican;

    public PrivilegeManager getPrivilegeManager(){
        return privilegesManager;
    }


    public boolean isGameOver(){
        return isGameOver;
    }

    public JsonNode generateJsonDescription () {
        JsonNodeFactory factory= JsonNodeFactory.instance;
        ObjectNode board = factory.objectNode();
        Iterator<JsonNode> areas = spacesDescription.elements();
        while (areas.hasNext()){
            JsonNode areaJson = areas.next();
            Iterator<JsonNode> spaces= areaJson.get("actionSpaces").elements();
            while (spaces.hasNext()){
                ObjectNode spaceJson = (ObjectNode) spaces.next();
                try {
                    iActionSpace spaceDecorator = getActionSpaceByID(spaceJson.get("id").asInt());
                    spaceDecorator.updateDescription(spaceJson);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        board.set("spaces", spacesDescription);

        ArrayNode arrayOfDice= factory.arrayNode();
        for (Dice dice:dices){
            ObjectNode objectNode=new ObjectNode(factory);
            objectNode.put("color", dice.getColor().getDiceColorString());
            objectNode.put("value", dice.getValue());
            arrayOfDice.add(objectNode);
        }
        board.set("dices", arrayOfDice);

        ArrayNode arrayOfPlayers=factory.arrayNode();
        for (Player player:playerArrayList){
            arrayOfPlayers.add(player.generateJsonDescription());
        }
        board.set("players", arrayOfPlayers);

        board.put("era", era);
        board.put("round", round+1);
        return board;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }


    /**
     * Class constructor. It gets players, cards (must be already shuffled) and action spaces lists to start the
     * initialization of the game.  Then a <code>boolean</code> is passed to choose between random and non-random
     * dices and loads the privileges from a <code>JsonNode</code> delegating the management to <code>PrivilegeManager</code>.
     * Adds the dices, sets round zero, sets the first Era and pick the first player.
     *
     * @param players list of the players that want to start the new game
     * @param cards list of cards, already initialized, decorated and shuffled
     * @param spaces list of action spaces, already initialized and decorated
     * @param random <code>true</code> to set the random dice game-mode
     * @param privileges contains the data for the creation of the privileges
     */
    public Board(ArrayList<Player> players,ArrayList<iCard> cards,
                 ArrayList<iActionSpace> spaces, ArrayNode spacesDescription, boolean random, JsonNode privileges){
        //Initialization
        isGameOver=false;
        actionSpaces= spaces;
        playerArrayList=players;
        this.cards=cards;
        this.spacesDescription = spacesDescription;
        privilegesManager =new PrivilegeManager(privileges);
        if (random) {
            dices = new ArrayList<>();
            dices.add(new Dice(Dice.DiceColor.WHITE));
            dices.add(new Dice(Dice.DiceColor.ORANGE));
            dices.add(new Dice(Dice.DiceColor.BLACK));
        } else {
            dices = new ArrayList<>();
            dices.add(new NonRandomDice(Dice.DiceColor.WHITE, 1));
            dices.add(new NonRandomDice(Dice.DiceColor.ORANGE, 3));
            dices.add(new NonRandomDice(Dice.DiceColor.BLACK, 6));
        }
        //Turn management
        councilHasBeenSet =false;
        round=0;
        for (int i =0; i<playerArrayList.size(); i++){
            Player p = playerArrayList.get(i);
            p.getResource(ResourceType.COIN).set(5+i);
        }
        currentPlayer=playerArrayList.get(0);
        era = 1;
        vatican= false;
        //cleanUp();//Council will be empty, so nothing happens to turn order;
                    //cleanUp also rolls dices and causes tower action spaces to receive their first card
    }

    /**
     * Returns the <code>boolean</code> value of vatican attribute.
     *
     * @return <code>true</code> if it is the time of the Vatican phase
     */
    public boolean isVatican() {
        return vatican;
    }

    /**
     * Sets the Council at the value given as a parameter, that represent the Action Space index, if it is not
     * already set.
     *
     * @param councilID index of the slot to be set as Council
     * @throws Exception if the councilID attribute is already been set
     */
    public void setCouncilID(int councilID) throws Exception {
        if (!councilHasBeenSet) {
            this.councilID = councilID;
            //cleanUp();
        } else{
            throw new Exception("Council ID has already been set");
        }
    }

    /**
     * Calls <code>cleanUp</code> if council ID hasn't been set before, else throws an exception.
     *
     * @throws Exception if council ID has already been set
     */
    public void firstCleanup() throws Exception {
        if (!councilHasBeenSet) {
            councilHasBeenSet=true;
            cleanUp();
        } else{
            throw new Exception("Council ID has already been set");
        }
    }

    /**
     * Delegates the assignment of the value for each dice and set the value to the correspondent family member.
     */
    private void rollDices(){
        Iterator<Dice> iterator = dices.iterator();
        while (iterator.hasNext()){
            iterator.next().rollDice();
        }
        for (Player p: playerArrayList) {
            for (FamilyMember fm:p.getFamilyMembers()) {
                fm.setValue(getDiceValue(fm.diceColor));
            }
        }
    }

    /**
     *Iterates through the dices to find a match with the color passed and returns the dice value.
     *
     * @param color dice color
     * @return value of the dice, 0 for neutral and ghost
     */
    public int getDiceValue (Dice.DiceColor color){
        for (Dice d:dices) {
            if (d.getColor()==color){
                return d.getValue();
            }
        }
        return 0;//for neutral and ghost
    }

    /**
     * Takes the <code>JsonNode</code> of the move and passes it, with the board itself, to a controller class
     * to delegate the management of the move.
     *
     * @param move JSON of the move, already mapped
     * @throws Exception re-throws the exception if it occurs in the controller class
     */
    public void makeMove(JsonNode move) throws Exception {
        MoveManager.makeMove(this, move);
    }

    /**
     * Iterates through the action spaces, until finds a match with the given ID and returns the action space.
     *
     * @param id index of the action space on the board
     * @return  an object that implements the interface for the action space
     * @throws Exception if such ID doesn't exist on the board
     */
    public iActionSpace getActionSpaceByID(int id) throws Exception {
        Iterator<iActionSpace> iterator = actionSpaces.iterator();
        while (iterator.hasNext()){
            iActionSpace actionSpace = iterator.next();
            if (actionSpace.getID()==id){
                return actionSpace;
            }
        }
        throw new Exception("Could not find an actionSpace with ID "+id);
    }

    /**
     * Checks if the player passed is the current player in the game.
     *
     * @param player player who wants to make a move
     * @return <code>true</code> if is the current player
     */
    public boolean isPlayerTurn(Player player){
        return currentPlayer.getColor()==player.getColor();
    }

    /**
     * Returns the size of the list of players of the current game.
     *
     * @return number of players in the game
     */
    public int getNumberOfPlayers(){
        return playerArrayList.size();
    }

    /**
     * Returns the list of action spaces of the current game.
     *
     * @return a list of the action spaces
     */
    public ArrayList<iActionSpace> getActionSpaces(){
        return this.actionSpaces;
    }

    public int getEra() {
        return era;
    }
    /**
     * Iterates through the list of cards, removes and returns them based on current Era and type. If the list is empty
     * throws an exception.
     *
     * @param type type of the card to be removed
     * @return an object that implements the card interface
     * @throws Exception if the list is run out of cards of a certain type/Era
     */
    public iCard getCard(Card.CardType type) throws Exception {
        Iterator<iCard> cardIterator = cards.iterator();
        while (cardIterator.hasNext()){
            iCard card= cardIterator.next();
            if (card.getEra()==era && card.getCardType()==type){
                cardIterator.remove();
                return card;
            }
        }
        throw new Exception("Out of cards of type "+type.getString()+" and era "+era);
    }

    /**
     * Iterates through all the players in the game and through the respective owned  family members: to check if they
     * are all used, because it means that is the end of round.
     *
     * @return <code>true</code> if is the end of round (all family member used)
     */
    private boolean isEndOfRound(){
        Iterator <Player> playerIterator= playerArrayList.iterator();
        while (playerIterator.hasNext()){
            Player p= playerIterator.next();
            Iterator<FamilyMember> familyMemberIterator = p.getFamilyMembers().iterator();
            while (familyMemberIterator.hasNext()){
                FamilyMember fm = familyMemberIterator.next();
                if (!fm.isUsed()) {return false;}
            }
        }
        return true;
    }

    /**
     * Iterates through the players in the game and through the respective family members, if it finds a
     * family member unused the owner is returned as next player to make a move. Throws an exception if it finds no
     * next player, <code>isEndOfRound</code> must be called before.
     *
     * @return the player that will make the next move
     * @throws Exception if it doesn't find a possible next player in the list
     */
    private Player getNextPlayer() throws Exception {
        int currentTurn=getPlayerIndex(currentPlayer);
        for (int i = 1; i<=playerArrayList.size(); i++){
            Player nextPlayer=playerArrayList.get((currentTurn+i)%playerArrayList.size());
            Iterator<FamilyMember> familyMemberIterator = nextPlayer.getFamilyMembers().iterator();
            while (familyMemberIterator.hasNext()){
                FamilyMember fm = familyMemberIterator.next();
                if (!fm.isUsed()) {return nextPlayer;}
            }
        }
        throw new Exception("No next payer");
    }

    /**
     * Returns the index of a player passed, in the list of players in the game.
     *
     * @param p player to check the index
     * @return the index of the player, 0 is first in the turn order
     * @throws Exception if the player passed is not playing in the game or has an invalid color
     */
    private int getPlayerIndex(Player p) throws Exception {
        for (int i =0; i<playerArrayList.size(); i++){
            if (playerArrayList.get(i).getColor()==p.getColor()) return  i;
        }
        throw new Exception("No player with such color "+ p.getColor().getPlayerColorString());
    }

    /**
     *Sets the new current player, ended the Vatican phase.
     */
    public void endVaticanPlayerTurn(){
        int index = 0;
        try{
            index=getPlayerIndex(currentPlayer);
        }catch (Exception e){
            e.printStackTrace();
        }
        if (index==playerArrayList.size()-1) {
            vatican=false;
            currentPlayer=playerArrayList.get(0);
        }else {
            currentPlayer= playerArrayList.get(index+1);
        }
    }

    /**
     *If it is not the end of round gets the next player, else increments round counter and if the end of an era sets
     *  vatican to <code>true</code>, increments Era: if it is more than 4 delegates <code>endGame</code> management,
     *  else it gets the first player to play in the new round and delegates the cleanUps of the action spaces.
     */
    public void endPlayerTurn(){
        try {
            if (!isEndOfRound()) {
                currentPlayer = getNextPlayer();
            } else {
                round++;
                if (isEndOfEra()){
                    vatican=true;
                    currentPlayer=playerArrayList.get(0);
                    era ++;
                    if (era >=4){
                        endGame();
                        isGameOver=true;
                        return;
                    }
                }
                cleanUp();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if is the end of an Era: the even rounds, except round zero.
     *
     * @return <code>true</code> if is the end of an Era
     */
    private boolean isEndOfEra(){
        if (round%2==0&&round!=0){
            return true;
        }
        return false;
    }

    /**
     * Delegates the changing of turn order and the cleanUps of the action spaces, iterating through them.
     * Then it rolls the dices.
     */
    private void cleanUp(){
        try {
            changeTurnOrder(getActionSpaceByID(councilID)); //this must happen before council.cleanup is called
        } catch (Exception e) {
            e.printStackTrace();
        }
        Iterator<iActionSpace> iterator = actionSpaces.iterator();
        while (iterator.hasNext()){
            iterator.next().cleanup();
        }
        rollDices();
    }

    /**
     * Gets the list of family members in the council action space, passed as a parameter, and modifies the list of
     * players from which is checked the order of the players.
     *
     * @param council object that implements the action space interface, set as council
     */
    private void changeTurnOrder(iActionSpace council){
        for (int i=council.getFamilyMembers().size()-1; i>=0; i--){
            Player player=council.getFamilyMembers().get(i).owner;
            int j= 0;
            try {
                j = getPlayerIndex(player);
            } catch (Exception e) {
                e.printStackTrace();
            }
            playerArrayList.add(0,playerArrayList.remove(j));
        }
        currentPlayer=playerArrayList.get(0);
    }

    /**
     *Iterates through the list of players until finds a match for the color passed, that has to be in the Enum of player color.
     *
     * @param color player color
     * @return player if it finds a match or null
     */
    public Player getPlayerByColor(Player.PlayerColor color){
        for (Player p:playerArrayList) {
            if (p.getColor()==color) return p;
        }
        return null;
    }

    /**
     *Calculates the final scoring, refreshes the victory points for each player according to the game rules.
     */
    private void endGame(){
        Iterator <Player> playerIterator=playerArrayList.iterator();
        while (playerIterator.hasNext()){
            Player player = playerIterator.next();
            for (int k=0; k<=player.getNumberOfCards(Card.CardType.CHARACTER); k++){
                player.getResource(ResourceType.VICTORYPOINTS).add(k);
            }
            int counter=0;
            for (int j=2; j<=player.getNumberOfCards(Card.CardType.TERRITORY); j++){
                counter=counter+j-2;
                player.getResource(ResourceType.VICTORYPOINTS).add(counter);
            }
            player.getResource(ResourceType.VICTORYPOINTS).add(convertFaithToVictoryPoints(player));
            counter=player.getResource(ResourceType.WOOD).get()+player.getResource(ResourceType.SERVANT).get()
                    +player.getResource(ResourceType.STONE).get()+player.getResource(ResourceType.COIN).get();
            player.getResource(ResourceType.VICTORYPOINTS).add(counter/5);
        }
        playerArrayList.sort(Comparator.comparingInt(o -> o.getResource(ResourceType.MILITARYPOINTS).get()*-1));
        playerArrayList.get(0).getResource(ResourceType.VICTORYPOINTS).add(5);
        playerArrayList.get(1).getResource(ResourceType.VICTORYPOINTS).add(2);
    }

    /**
     *According to the game rules, when called, it does the conversion from faith points of the
     * player passed as a parameter to victory points and then returns the value to be add (as victory points).
     *
     * @param player player that has reached a point of the game when must happen the conversion
     * @return the value of the conversion that must be add to the (same, supposedly) player's victory points
     */
    public static int convertFaithToVictoryPoints(Player player){
        File faithPointsJson = new File("src/res/faithPoints.json");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode victoryPointsFromFaithPoints=null;
        try {
            victoryPointsFromFaithPoints = mapper.readTree(faithPointsJson);
        } catch (IOException e){
            e.printStackTrace();
        }
        int faith = player.getResource(ResourceType.FAITHPOINTS).get();
        try {
            return victoryPointsFromFaithPoints.get("faithPoints").get(faith).asInt();
        } catch (NullPointerException npe){
            return (player.getResource(ResourceType.FAITHPOINTS).get()-15)*5+30;
        }
    }

    /**
     * If a player has decided to support the church, his faith points are converted to victory points and then
     * are set to zero.
     *
     * @param p player that has chosen to support the church
     */
    public static void giveUpFaithPoints(Player p){
        int vict = convertFaithToVictoryPoints(p);
        p.getResource(ResourceType.FAITHPOINTS).set(0);
        p.getResource(ResourceType.VICTORYPOINTS).add(vict);
    }


    public ArrayList<Player> getPlayerArrayList() {
        return playerArrayList;
    }

    public int getRound() {
        return round;
    }
}
