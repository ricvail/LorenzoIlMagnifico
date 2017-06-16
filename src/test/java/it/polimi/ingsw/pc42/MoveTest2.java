package it.polimi.ingsw.pc42;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.Card;
import it.polimi.ingsw.pc42.Control.ResourceType;
import it.polimi.ingsw.pc42.Model.Board;
import it.polimi.ingsw.pc42.Model.FamilyMember;
import it.polimi.ingsw.pc42.Model.Player;
import it.polimi.ingsw.pc42.Utilities.GameInitializer;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;

/**
 * Unit test for simple App.
 */
public class MoveTest2
        extends TestCase
{
    public MoveTest2(String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( MoveTest2.class );
    }

    public void testMove2() {
        /*
        JsonNode mosse = GameInitializer.readFile("src/res/mosse_per_moveTest2.json").get("moves");
        Board b = GameInitializer.initBaseGame(false);
        //RED and BLUE playing; servants=3, wood=stone=2, coins=5+i
        //first move--------------------------------------------------------------------------------------------------
        boolean exception = false;
        try{
            b.makeMove(mosse.get(0)); //Rosso pesca carta territorio slot 1 con fm white
        } catch (Exception e){
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        FamilyMember fm= null;
        try {
            fm = b.getPlayerByColor(Player.PlayerColor.RED).getFamilyMemberFromColor("white");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, fm.isUsed());
        assertEquals(1, fm.owner.getNumberOfCards(Card.CardType.TERRITORY));
        //no cost, 1 coin immediate effect
        assertEquals(5, fm.owner.getResource(ResourceType.COIN).get());
        //end first move-----------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(1));//Blue piazza fm orange in slot 12, exception
        } catch (ActionAbortedException ae){
            exception = true;
            ae.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, exception);
        //re-try-------------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(2));//Blue piazza fm in slot 12 con fm black e 1 servant, legal -> 2 mpoints e card
        } catch (Exception e){
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        fm= null;
        try {
            fm = b.getPlayerByColor(Player.PlayerColor.BLUE).getFamilyMemberFromColor("black");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, fm.isUsed());
        assertEquals(1, fm.owner.getNumberOfCards(Card.CardType.BUILDING));
        //+2 immediate actionspace bonus
        assertEquals(2, fm.owner.getResource(ResourceType.MILITARYPOINTS).get());
        //2-1 cost
        assertEquals(1, fm.owner.getResource(ResourceType.WOOD).get());
        // 6 - 4 cost
        assertEquals(2, fm.owner.getResource(ResourceType.COIN).get());
        //+5 immediate effect bonus
        assertEquals(2, fm.owner.getResource(ResourceType.VICTORYPOINTS).get());
        //end second move----------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(3));//Rosso mette fm nero(=6) nello slotID 7, +1 stone e card, legal
        } catch (Exception e){
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        fm= null;
        try {
            fm = b.getPlayerByColor(Player.PlayerColor.RED).getFamilyMemberFromColor("black");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, fm.isUsed());
        assertEquals(1, fm.owner.getNumberOfCards(Card.CardType.CHARACTER));
        //+1 stone immediate action space bonus
        assertEquals(3, fm.owner.getResource(ResourceType.STONE).get());
        // cost 4 coins, 5-4
        assertEquals(1, fm.owner.getResource(ResourceType.COIN).get());
        //end third move-----------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(4));//Blue slotID 13, orange, cost carta > resources -> exception
        }catch (ActionAbortedException ae){
            exception = true;
            ae.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, exception);
        //re-try-------------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(5));//Blue slotID 14, cost ok -> legale, fm white + 2 servants
        } catch (Exception e){
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        fm= null;
        try {
            fm = b.getPlayerByColor(Player.PlayerColor.BLUE).getFamilyMemberFromColor("white");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, fm.isUsed());
        // -2 servants
        assertEquals(1, fm.owner.getResource(ResourceType.SERVANT).get());
        assertEquals(1, fm.owner.getNumberOfCards(Card.CardType.VENTURE));
        //wood 1-1
        assertEquals(0, fm.owner.getResource(ResourceType.WOOD).get());
        //stone 2-1
        assertEquals(1, fm.owner.getResource(ResourceType.STONE).get());
        //coins 2-1
        assertEquals(1, fm.owner.getResource(ResourceType.COIN).get());
        //faithpoints +1
        assertEquals(1, fm.owner.getResource(ResourceType.FAITHPOINTS).get());
        //end fourth move----------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(5));//Rosso mette fm neutral con 1 servant nello slotID 9, coinTax=3 -> fallisce (=1)
        } catch (ActionAbortedException ae){
            exception = true;
            ae.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, exception);
        //check if servant is unused again
        assertEquals(3, fm.owner.getResource(ResourceType.SERVANT).get());
        //CHEAT MODE player RED----------------------------------------------------------------------------------------
        b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.COIN).add(10);
        b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.STONE).add(3);
        //re-try-------------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(6));//Rosso ritenta mossa precedente
        } catch (Exception e){
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        fm= null;
        try {
            fm = b.getPlayerByColor(Player.PlayerColor.RED).getFamilyMemberFromColor("neutral");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, fm.isUsed());
        // 1 servant used
        assertEquals(2, fm.owner.getResource(ResourceType.SERVANT).get());
        assertEquals(1, fm.owner.getNumberOfCards(Card.CardType.BUILDING));
        //coins 11 -3
        assertEquals(8, fm.owner.getResource(ResourceType.COIN).get());
        // stone 5-3
        assertEquals(2, fm.owner.getResource(ResourceType.STONE).get());
        // wood 2-1
        assertEquals(1, fm.owner.getResource(ResourceType.WOOD).get());
        // victorypoints +5
        assertEquals(5, fm.owner.getResource(ResourceType.VICTORYPOINTS).get());
        //CHEAT MODE Player blue---------------------------------------------------------------------------------------
        b.getPlayerByColor(Player.PlayerColor.BLUE).getResource(ResourceType.COIN).add(1);
        // per testare se viene effettivamente prima il bonus dell'actionspace, coin=2
        b.getPlayerByColor(Player.PlayerColor.BLUE).getResource(ResourceType.STONE).add(9);
        b.getPlayerByColor(Player.PlayerColor.BLUE).getResource(ResourceType.SERVANT).add(9);
        //end fifth move-----------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(7));//Blue slotID 15 con fm orange +2 servants, +1 coins actionspace, -3 cointax
        } catch (Exception e){
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        fm= null;
        try {
            fm = b.getPlayerByColor(Player.PlayerColor.BLUE).getFamilyMemberFromColor("orange");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, fm.isUsed());
        //-2 servants
        assertEquals(8, fm.owner.getResource(ResourceType.SERVANT).get());
        assertEquals(2, fm.owner.getNumberOfCards(Card.CardType.VENTURE));
        // coins (2+1)-3 teoricamente
        assertEquals(0, fm.owner.getResource(ResourceType.COIN).get());
        //10-3 stone
        assertEquals(7, fm.owner.getResource(ResourceType.STONE).get());
        //2 military points bonus +2 privileges
        assertEquals(6, fm.owner.getResource(ResourceType.MILITARYPOINTS).get());
        //end sixth move-----------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(8));//Rosso tenta di mettere fm orange in slotID 2, già fm red -> exception
        } catch (ActionAbortedException ae){
            exception = true;
            ae.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, exception);
        //CHEAT MODE player RED----------------------------------------------------------------------------------------
        b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.WOOD).add(1);
        b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.SERVANT).add(2);
        //re-try-------------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(9));//Rosso ritenta fm orange in slot 16, 2 privileges
        } catch (Exception e){
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        fm= null;
        try {
            fm = b.getPlayerByColor(Player.PlayerColor.RED).getFamilyMemberFromColor("orange");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, fm.isUsed());
        //+2 coin bonus, -3 cointax
        assertEquals(1, fm.owner.getNumberOfCards(Card.CardType.VENTURE));
        assertEquals(7, fm.owner.getResource(ResourceType.COIN).get());
        //2 privileges: 1 faithpoint, 1 stone/wood
        assertEquals(1, fm.owner.getResource(ResourceType.FAITHPOINTS).get());
        //cost 2 stone +1
        assertEquals(1, fm.owner.getResource(ResourceType.STONE).get());
        //cost 2 wood +1
        assertEquals(1, fm.owner.getResource(ResourceType.WOOD).get());
        //end seventh move---------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(10));// Blue fm neutral slotID 17, council, + 1 servant e privileges 2 coins
        } catch (Exception e){
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        fm= null;
        try {
            fm = b.getPlayerByColor(Player.PlayerColor.BLUE).getFamilyMemberFromColor("neutral");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, fm.isUsed());
        //privileges
        assertEquals(2, fm.owner.getResource(ResourceType.COIN).get());
        //END OF FIRST ROUND-------------------------------------------------------------------------------------------
        //Clean-Up test
        boolean blueTurn = b.isPlayerTurn(Player.fromColorString("blue"));
        assertEquals(true, blueTurn);
        //check if all the family members are unused
        boolean blueFMUsed = checkFamilyMemberUsed(b.getPlayerByColor(Player.PlayerColor.BLUE).getFamilyMembers());
        assertEquals(false, blueFMUsed);
        boolean redFMUsed = checkFamilyMemberUsed(b.getPlayerByColor(Player.PlayerColor.RED).getFamilyMembers());
        assertEquals(false, redFMUsed);
        //TODO check cards cleanup
        */
    }

    private boolean checkFamilyMemberUsed(ArrayList<FamilyMember> familyMembers){
        boolean used = false;
        for (FamilyMember fm : familyMembers){
            if (fm.isUsed()){
                used = true;
            }
        }
        return used;
    }
}