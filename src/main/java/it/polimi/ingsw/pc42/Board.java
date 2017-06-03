package it.polimi.ingsw.pc42;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.ActionSpace.ActionSpace;
import it.polimi.ingsw.pc42.ActionSpace.iActionSpace;
import it.polimi.ingsw.pc42.DevelopmentCards.Card;
import it.polimi.ingsw.pc42.DevelopmentCards.iCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by diego on 28/05/2017.
 */
public class Board implements iBoard {
    private int era;
    private int round;
    private Player currentPlayer;
    //private int currentTurn;
    private ArrayList<Player> playerArrayList;
    private ArrayList<iActionSpace> actionSpaces;
    private ArrayList<iCard> cards;
    private int councilID;//TODO set this (in constructor?)


    /**
     * Player and cards list must be already shuffled
     * @param players
     * @param cards
     */
    public Board(ArrayList<Player> players,ArrayList<iCard> cards){
        //Initialization
        actionSpaces= new ArrayList<>();
        playerArrayList=players;
        this.cards=cards;
        //Turn management
        round=0;
        currentPlayer=playerArrayList.get(0);
        era = 1;
    }

    public void makeMove(Player p, JsonNode move) throws Exception {
        if (!isPlayerTurn(p)){
            throw new Exception("it's not this player's turn");
        }
        //TODO check if move is complete and valid

        FamilyMember fm = p.getFamilyMemberFromColor(move.get("familyMember").asText());
        iActionSpace space = getActionSpaceByID(move.get("slotID").asInt());
        if (space.canPlace(fm)){
            space.placeFamilyMember(fm, move);
        }else {
            throw new Exception("Illegal move of player "+p.getColor().name());
        }
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

    @Override
    public ArrayList<iActionSpace> getActionSpaces(){
        return this.actionSpaces;
    }

    @Override
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
        //TODO roll dices
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

    private void endGame(){
        /*
        Iterate i giocatori
        aggiungete punti vittoria in base a
            numero di carte blu possedute
            numero di territori posseduti
            punti fede
            risorse divise per 5
        poi cercate il giocatore con più punti militari e dategli 5 punti vittoria
        e il secondo giocatore con più punti militari e dategli 2 punti vittoria

        le carte viola le gestiamo più avanti, per ora lasciatele perdere
        */
    }

}
