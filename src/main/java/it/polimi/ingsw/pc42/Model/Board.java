package it.polimi.ingsw.pc42.Model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Control.ActionSpace.MoveManager;
import it.polimi.ingsw.pc42.Control.ActionSpace.iActionSpace;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.Card;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.iCard;
import it.polimi.ingsw.pc42.Control.PrivilegeManager;
import it.polimi.ingsw.pc42.Control.ResourceType;

import java.util.ArrayList;
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

    public PrivilegeManager getPrivilegeManager(){
        return privilegesManager;
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
                 ArrayList<iActionSpace> spaces, boolean random, JsonNode privileges){
        //Initialization
        actionSpaces= spaces;
        playerArrayList=players;
        this.cards=cards;
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
        currentPlayer=playerArrayList.get(0);
        era = 1;
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
        throw new Exception("Could not find an actionspace with ID "+id);
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

    public void endPlayerTurn(){
        try {
            if (!isEndOfRound()) {
                currentPlayer = getNextPlayer();
            } else {
                round++;
                if (isEndOfEra()){
                    //TODO vatican phase
                    era ++;
                    if (era >=4){
                        endGame();
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
        int counter;
        ArrayList<Player> playerArrayList=new ArrayList<>();
        int militaryPoints;
        for (int i=0; i<=playerArrayList.size(); i++){
            Player player = playerIterator.next();
            for (int k=0; k<=player.getNumberOfCards(Card.CardType.CHARACTER); k++){
                player.getResource(ResourceType.VICTORYPOINTS).add(k);
            }
            counter=0;
            for (int j=2; j<=player.getNumberOfCards(Card.CardType.TERRITORY); j++){
                counter=counter+j-2;
                player.getResource(ResourceType.VICTORYPOINTS).add(counter);
            }
            counter=0;
            for (int x=1; x<=player.getResource(ResourceType.FAITHPOINTS).get(); x++ ){
                if (x<=5){
                    counter=1;
                }
                if (x>5 && x<=12){
                    counter=2;
                }
                if (x>12 && x<=14){
                    counter=3;
                }
                if (x==15){
                    counter=5;
                }
                player.getResource(ResourceType.VICTORYPOINTS).add(counter);
            }
            counter=player.getResource(ResourceType.WOOD).get()+player.getResource(ResourceType.SERVANT).get()
                    +player.getResource(ResourceType.STONE).get()+player.getResource(ResourceType.COIN).get();
            player.getResource(ResourceType.VICTORYPOINTS).add(counter/5);
            militaryPoints=player.getResource(ResourceType.MILITARYPOINTS).get();
            for (int e=0; e<=playerArrayList.size();e++){
                if (militaryPoints>playerArrayList.get(e).getResource(ResourceType.MILITARYPOINTS).get()){
                    playerArrayList.add(e, player);
                    break;
                }
            }
        }
        playerArrayList.get(0).getResource(ResourceType.VICTORYPOINTS).add(5);
        playerArrayList.get(1).getResource(ResourceType.VICTORYPOINTS).add(2);
    }

}