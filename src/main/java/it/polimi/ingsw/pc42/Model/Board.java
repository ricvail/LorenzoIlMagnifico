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

    public boolean isVatican() {
        return vatican;
    }
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
                    //--------------------
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return board;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCouncilID(int councilID) throws Exception {
        if (!councilHasBeenSet) {
            this.councilID = councilID;
            //cleanUp();
        } else{
            throw new Exception("Council ID has already been set");
        }
    }

    public void firstCleanup() throws Exception {
        if (!councilHasBeenSet) {
            councilHasBeenSet=true;
            cleanUp();
        } else{
            throw new Exception("Council ID has already been set");
        }
    }



    /**
     * Player and cards list must be already shuffled
     * @param players
     * @param cards
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

    public int getDiceValue (Dice.DiceColor color){
        for (Dice d:dices) {
            if (d.getColor()==color){
                return d.getValue();
            }
        }
        return 0;//for neutral and ghost
    }

    public void makeMove(JsonNode move) throws Exception {
        MoveManager.makeMove(this, move);
    }


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


    public boolean isPlayerTurn(Player player){
        return currentPlayer.getColor()==player.getColor();
    }

    public int getNumberOfPlayers(){
        return playerArrayList.size();
    }

    public ArrayList<iActionSpace> getActionSpaces(){
        return this.actionSpaces;
    }

    public int getEra() {
        return era;
    }

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

    private int getPlayerIndex(Player p) throws Exception {
        for (int i =0; i<playerArrayList.size(); i++){
            if (playerArrayList.get(i).getColor()==p.getColor()) return  i;
        }
        throw new Exception("No player with such color "+ p.getColor().getPlayerColorString());
    }

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

    private boolean isEndOfEra(){
        if (round%2==0&&round!=0){
            return true;
        }
        return false;
    }

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

    public Player getPlayerByColor(Player.PlayerColor color){
        for (Player p:playerArrayList) {
            if (p.getColor()==color) return p;
        }
        return null;
    }

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
