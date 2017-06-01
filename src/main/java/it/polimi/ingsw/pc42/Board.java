package it.polimi.ingsw.pc42;

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
    private int currentTurn;
    private ArrayList<Player> playerArrayList;
    private ArrayList<iActionSpace> actionSpaces;
    private ArrayList<iCard> cards;


    public Board(ArrayList<Player> players,ArrayList<iCard> cards){
        actionSpaces= new ArrayList<>();
        round=1;
        playerArrayList=players;
        this.cards=cards;
    }

    public boolean isPlayerTurn(Player player){
        if (playerArrayList.get(currentTurn).getColor()==player.getColor()){
            return true;
        }
        return false;
    }

    public int getNumberOfPlayers(){
        return playerArrayList.size();
    }

    public boolean isEndOfRound(){
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

    public Player getNextPlayer(){
        int indexOfCurrentPlayer=playerArrayList.indexOf(currentPlayer);
        for (int i =1; i<=playerArrayList.size(); i++){
            Player nextPlayer=playerArrayList.get((indexOfCurrentPlayer+i)%playerArrayList.size());
            Iterator<FamilyMember> familyMemberIterator = nextPlayer.getFamilyMembers().iterator();
            while (familyMemberIterator.hasNext()){
                FamilyMember fm = familyMemberIterator.next();
                if (!fm.isUsed()) {return nextPlayer;}
            }

        }
        return null;
    }

    public void advanceTurn(){
        if (currentTurn < playerArrayList.size()){
            currentTurn++;
            currentPlayer= playerArrayList.get(currentTurn);
        }
        else{
            boolean flag=false;
            for (FamilyMember fm: currentPlayer.getFamilyMembers()){
                if (!fm.isUsed()){
                    //if there's an unused family member, it's not the end of round
                    flag=true;
                }
                break;
            }
            if (flag){
                currentTurn=1;
                currentPlayer= playerArrayList.get(currentTurn);
            } else {
                nextRound();
            }
        }
    }

    private void nextRound(){
        currentTurn=1;
        if (round<=2){
            round ++;
        }
        else {
            round=1;
            era++;
        }
        cleanUp();
        this.currentPlayer=playerArrayList.get(currentTurn);
    }


    public void changeTurnOrder(ActionSpace council){
        for (int i=council.getFamilyMembers().size()-1; i>=0; i--){
            Player player=council.getFamilyMembers().get(i).owner;
            int j=playerArrayList.indexOf(player);
            playerArrayList.add(0,playerArrayList.remove(j));
        }
    }

    public void vaticanPhase(){
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

    public boolean cleanUp(){
        return true;
    }
}
