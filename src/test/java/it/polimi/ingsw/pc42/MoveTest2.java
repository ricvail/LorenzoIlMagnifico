package it.polimi.ingsw.pc42;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.org.apache.regexp.internal.RE;
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

    int blueServant = 3;  int blueStone = 2;  int blueWooD = 2;  int blueCoin = 6;
    int blueMilitaryPts = 0; int blueFaithPts = 0; int blueVictoryPts = 0;
    int redServant = 3;  int redStone = 2;  int redWood = 2;  int redCoin = 5;
    int redMilitaryPts = 0; int redFaithPts = 0; int redVictoryPts = 0;

    public void testMove2() {

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
        //no cost, no immediate effect
        //end first move-----------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(1));//Blue piazza fm orange in slot 12, exception
        } catch (ActionAbortedException ae){
            exception = true;
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
        //+2 immediate actionspace bonus, card cost: 1 wood, 4 coins, card bonus: 6 victory points
        blueServant-= 1; blueMilitaryPts+=2; blueWooD-=1; blueCoin-=4; blueVictoryPts+=6;
        //Resources Test
        assertEquals(blueServant, fm.owner.getResource(ResourceType.SERVANT).get());
        assertEquals(blueMilitaryPts, fm.owner.getResource(ResourceType.MILITARYPOINTS).get());
        assertEquals(blueWooD, fm.owner.getResource(ResourceType.WOOD).get());
        assertEquals(blueCoin, fm.owner.getResource(ResourceType.COIN).get());
        assertEquals(blueVictoryPts, fm.owner.getResource(ResourceType.VICTORYPOINTS).get());
        //end second move----------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(3));//Rosso mette fm black(=6) + 1 servant nello slotID 8, +2 stone e card, legal
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
        //+2 stone immediate action space bonus, card cost: 2 coins, card effect: 1 privileges -> 2 servant (-1)
        redStone+=2; redCoin-=2; redServant+=1;
        //Resources Test
        assertEquals(redServant, fm.owner.getResource(ResourceType.SERVANT).get());
        assertEquals(redStone, fm.owner.getResource(ResourceType.STONE).get());
        assertEquals(redCoin, fm.owner.getResource(ResourceType.COIN).get());
        //end third move-----------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(4));//Blue slotID 13, orange, cost carta > resources -> exception
        }catch (ActionAbortedException ae){
            exception = true;
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
        assertEquals(1, fm.owner.getNumberOfCards(Card.CardType.VENTURE));
        // -2 servants, card cost: stone=coin=wood=1, card effect: 1 faithpoint
        blueServant-=2; blueStone-=1; blueCoin-=1; blueWooD-=1; blueFaithPts+=1;
        // Resources Test
        assertEquals(blueServant, fm.owner.getResource(ResourceType.SERVANT).get());
        assertEquals(blueWooD, fm.owner.getResource(ResourceType.WOOD).get());
        assertEquals(blueStone, fm.owner.getResource(ResourceType.STONE).get());
        assertEquals(blueCoin, fm.owner.getResource(ResourceType.COIN).get());
        assertEquals(blueFaithPts, fm.owner.getResource(ResourceType.FAITHPOINTS).get());
        //end fourth move----------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(6));//Rosso mette fm neutral con 1 servant nello slotID 10-> non legale
        } catch (ActionAbortedException ae){
            exception = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, exception);
        //check if servant is unused again
        FamilyMember redNeutral = null;
        try{
            redNeutral = b.getPlayerByColor(Player.PlayerColor.RED).getFamilyMemberFromColor("neutral");
        }catch (Exception e){
            e.printStackTrace();
        }
        assertEquals(0, redNeutral.getValue());
        assertEquals(redServant,  b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.SERVANT).get());
        //re-try-------------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(7));//Rosso ritenta mossa precedente in slotID 9
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
        assertEquals(1, fm.owner.getNumberOfCards(Card.CardType.BUILDING));
        // -1 servant, cointax: 3, card cost: 1 wood 3 stone, card effect: 5 victory points
        redServant-=1; redCoin-=3; redWood-=1; redStone-=3; redVictoryPts+=5;
        //Resources Test
        assertEquals(redServant, fm.owner.getResource(ResourceType.SERVANT).get());
        assertEquals(redCoin, fm.owner.getResource(ResourceType.COIN).get());
        assertEquals(redStone, fm.owner.getResource(ResourceType.STONE).get());
        assertEquals(redWood, fm.owner.getResource(ResourceType.WOOD).get());
        assertEquals(redVictoryPts, fm.owner.getResource(ResourceType.VICTORYPOINTS).get());
        //CHEAT MODE Player blue---------------------------------------------------------------------------------------
        b.getPlayerByColor(Player.PlayerColor.BLUE).getResource(ResourceType.COIN).add(3); blueCoin+=3;
        b.getPlayerByColor(Player.PlayerColor.BLUE).getResource(ResourceType.STONE).add(2); blueStone+=2;
        b.getPlayerByColor(Player.PlayerColor.BLUE).getResource(ResourceType.SERVANT).add(6); blueServant+=6;
        //end fifth move-----------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(8));//Blue slotID 15 con fm neutral +5 servants, +1 coins actionspace, -3 cointax
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
        assertEquals(2, fm.owner.getNumberOfCards(Card.CardType.VENTURE));
        //-5 servant, +1-3 coin bonus e tax, card cost: 3 stone, card effect: 2 military points, 1 privilege -> 2 mlpts
        blueServant-=5; blueCoin-=2; blueStone-=3; blueMilitaryPts+=4;
        //Resources Test
        assertEquals(blueServant, fm.owner.getResource(ResourceType.SERVANT).get());
        assertEquals(blueCoin, fm.owner.getResource(ResourceType.COIN).get());
        assertEquals(blueStone, fm.owner.getResource(ResourceType.STONE).get());
        assertEquals(blueMilitaryPts, fm.owner.getResource(ResourceType.MILITARYPOINTS).get());
        //end sixth move-----------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(9));//Rosso tenta di mettere fm orange in slotID 2, già fm red -> exception
        } catch (ActionAbortedException ae){
            exception = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, exception);
        //CHEAT MODE player RED----------------------------------------------------------------------------------------
        b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.WOOD).add(1); redWood+=1;
        b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.STONE).add(1); redStone+=1;
        b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.SERVANT).add(3); redServant+=3;
        b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.COIN).add(3); redCoin+=3;
        //re-try-------------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(10));//Rosso ritenta fm orange in slot 16, 2 privileges-> exception 2 privileges uguali
        } catch (ActionAbortedException ae){
            exception = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, exception);
        //check if servant is unused again
        redNeutral = null;
        try{
            redNeutral = b.getPlayerByColor(Player.PlayerColor.RED).getFamilyMemberFromColor("orange");
        }catch (Exception e){
            e.printStackTrace();
        }
        assertEquals(3, redNeutral.getValue());
        assertEquals(redServant,  b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.SERVANT).get());

        //re-try-------------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(11));//Rosso ritenta fm orange in slot 16, 3 cointax, 2 privileges -> legale
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
        assertEquals(1, fm.owner.getNumberOfCards(Card.CardType.VENTURE));
        //-4 servants, +2-3 coins bonus, card cost: wood=stone=2, card effect: 2 privileges-> 1 wood=stone, 1 faithpoint
        redServant-=4; redCoin-=1; redStone-=1; redWood-=1; redFaithPts+=1;
        //Resources Test
        assertEquals(redServant, fm.owner.getResource(ResourceType.SERVANT).get());
        assertEquals(redCoin, fm.owner.getResource(ResourceType.COIN).get());
        assertEquals(redFaithPts, fm.owner.getResource(ResourceType.FAITHPOINTS).get());
        assertEquals(redStone, fm.owner.getResource(ResourceType.STONE).get());
        assertEquals(redWood, fm.owner.getResource(ResourceType.WOOD).get());
        //end seventh move---------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(12));// Blue fm neutral slotID 17, council
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
        //1 servant e privilege-> 2 coins
        blueServant-=1; blueCoin+=3;
        //Resources Test
        assertEquals(blueServant, fm.owner.getResource(ResourceType.SERVANT).get());
        assertEquals(blueCoin, fm.owner.getResource(ResourceType.COIN).get());
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
        /*
        //CHEAT MODE player Blue---------------------------------------------------------------------------------------
        b.getPlayerByColor(Player.PlayerColor.BLUE).getResource(ResourceType.SERVANT).add(1); blueServant+=1;
        //START SECOND ROUND-------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(13));//Blue fm black +1 servant, + 2coins, payment 1 -> fallisce per le resources
        } catch (ActionAbortedException ae){
            exception = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, exception);
        //undo of immediate bonus
        assertEquals(blueCoin, b.getPlayerByColor(Player.PlayerColor.BLUE).getResource(ResourceType.COIN).get());
        //re-try-------------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(14));//Blue fm black +1 servant, + 2coins, payment 0-> legale
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
        assertEquals(3, fm.owner.getNumberOfCards(Card.CardType.VENTURE));
        // +2 immediate bonus coins, -1 servant, card cost: mpts 4 needed -2, effect: 3 fpts
        blueServant-=1; blueCoin+=2; blueMilitaryPts-=2; blueFaithPts+=3;
        //Resources Test
        assertEquals(blueServant, fm.owner.getResource(ResourceType.SERVANT).get());
        assertEquals(blueCoin, fm.owner.getResource(ResourceType.COIN).get());
        assertEquals(blueMilitaryPts, fm.owner.getResource(ResourceType.MILITARYPOINTS).get());
        assertEquals(blueFaithPts, fm.owner.getResource(ResourceType.FAITHPOINTS).get());
        //CHEAT MODE player RED---------------------------------------------------------------------------------------
        b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.COIN).add(1); redCoin+=1;
        //end ninth move-----------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(15));//RED fm black + 1 servant in slotID 8, -3 coins, extracard slotID 6 -> fail cointax
        } catch (ActionAbortedException ae){
            exception = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, exception);
        assertEquals(redStone, b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.STONE).get());
        //CHEAT MODE player RED---------------------------------------------------------------------------------------
        b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.COIN).add(5); redCoin+=5;
        //re-try-------------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(16));//RED fm black + 1 servant in slotID 8, -3 coins,extra card slotID 7 (-3 cointax)
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
        assertEquals(3, fm.owner.getNumberOfCards(Card.CardType.CHARACTER));
        //-2 servants, +3 stone bonus, -8 coins, +5 fpts
        redServant-=2; redCoin-=8; redStone+=3; redFaithPts+=5;
        //Resources Test
        assertEquals(redServant, fm.owner.getResource(ResourceType.SERVANT).get());
        assertEquals(redCoin, fm.owner.getResource(ResourceType.COIN).get());
        assertEquals(redStone, fm.owner.getResource(ResourceType.STONE).get());
        assertEquals(redFaithPts, fm.owner.getResource(ResourceType.FAITHPOINTS).get());
        //CHEAT MODE player Blue---------------------------------------------------------------------------------------
        b.getPlayerByColor(Player.PlayerColor.BLUE).getResource(ResourceType.SERVANT).add(2); blueServant+=2;
        //end of tenth move--------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(17));//Blue tenta fm orange in slotID 7, exception carta già pescata
        } catch (ActionAbortedException ae){
            exception = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, exception);
        //re-try-------------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(18));//Blue fm orange ghost move
        } catch (Exception e){
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        //end of 11th move-----------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(19));//Red fm orange ghost move
        } catch (Exception e){
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        //end of 12th move---------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(20));//Blue fm white ghost move
        } catch (Exception e){
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        //end of 13th move---------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(21));//Red fm white ghost move
        } catch (Exception e){
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        //end of 14th move---------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(22));//Blue fm neutral ghost move
        } catch (Exception e){
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        //end of 15th move---------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(23));//Red fm neutral ghost move
        } catch (Exception e){
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        //Clean-Up test------------------------------------------------------------------------------------------------
         blueTurn = b.isPlayerTurn(Player.fromColorString("blue"));
        assertEquals(true, blueTurn);
        //check if all the family members are unused
        blueFMUsed = checkFamilyMemberUsed(b.getPlayerByColor(Player.PlayerColor.BLUE).getFamilyMembers());
        assertEquals(false, blueFMUsed);
        redFMUsed = checkFamilyMemberUsed(b.getPlayerByColor(Player.PlayerColor.RED).getFamilyMembers());
        assertEquals(false, redFMUsed);
        assertEquals(2, b.getEra());
        //END SECOND ROUND---------------------------------------------------------------------------------------------
        //CHEAT MODE player Blue---------------------------------------------------------------------------------------
        b.getPlayerByColor(Player.PlayerColor.BLUE).getResource(ResourceType.WOOD).add(2); blueWooD+=3;
        //START THIRD ROUND--------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(24));//Blue fm orange slotID 10, card cost 3 wood, legale
        } catch (Exception e){
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        // cost 3 wood
        blueWooD-=3;
        //CHEAT MODE layer RED-----------------------------------------------------------------------------------------
        b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.COIN).add(8); redCoin+=9;
        //end 17th move------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(25));//RED fm black in slot7 -> pesca slot6-> pesca slot2-> fallisce doveva essere buildings
        } catch (Exception e){
            exception = true;
            e.printStackTrace();
        }
        assertEquals(true, exception);
        assertEquals(redStone, b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.STONE).get());
        assertEquals(redCoin, b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.COIN).get());
        //re-try-------------------------------------------------------------------------------------------------------
        //CHEAT MODE layer RED-----------------------------------------------------------------------------------------
        b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.WOOD).add(3); redWood+=3;
        exception = false;
        try {
            b.makeMove(mosse.get(26));//RED fm black in slot7 -> pesca slot6-> pesca slot11-> fallisce per la coin tax
        } catch (Exception e){
            exception = true;
            e.printStackTrace();
        }
        assertEquals(true, exception);
        assertEquals(redStone, b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.STONE).get());
        assertEquals(redCoin, b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.COIN).get());
        assertEquals(redWood, b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.WOOD).get());
        //re-try-------------------------------------------------------------------------------------------------------
        //CHEAT MODE layer RED-----------------------------------------------------------------------------------------
        b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.SERVANT).add(1); redServant+=1;
        exception = false;
        try {
            b.makeMove(mosse.get(27));//RED fm black in slot7 -> pesca slot8-> pesca slot14-> fallisce per costo carta
            // che non viene soddisfatto a perchè si sceglie il privileges[0]
        } catch (Exception e){
            exception = true;
            e.printStackTrace();
        }
        assertEquals(true, exception);
        assertEquals(redStone, b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.STONE).get());
        assertEquals(redCoin, b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.COIN).get());
        assertEquals(redWood, b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.WOOD).get());
        assertEquals(redServant, b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.SERVANT).get());
        //re-try-------------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(27));//RED fm black in slot7 -> pesca slot8-> pesca slot14-> funziona
        } catch (Exception e){
            exception = true;
            e.printStackTrace();
        }
        fm= null;
        try {
            fm = b.getPlayerByColor(Player.PlayerColor.RED).getFamilyMemberFromColor("black");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, exception);
        assertEquals(true, fm.isUsed());
        assertEquals(5, fm.owner.getNumberOfCards(Card.CardType.CHARACTER));
        assertEquals(2, fm.owner.getNumberOfCards(Card.CardType.VENTURE));
        redServant-=1; redCoin-=8; redStone+=1; redWood-=2; redFaithPts+=2;
        //Resources Test
        assertEquals(redServant, fm.owner.getResource(ResourceType.SERVANT).get());
        assertEquals(redCoin, fm.owner.getResource(ResourceType.COIN).get());
        assertEquals(redStone, fm.owner.getResource(ResourceType.STONE).get());
        assertEquals(redWood, fm.owner.getResource(ResourceType.WOOD).get());
        assertEquals(redFaithPts, fm.owner.getResource(ResourceType.FAITHPOINTS).get());
        //end 18th move------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(28));//BLUE fm white in slot13
        } catch (Exception e){
            exception = true;
            e.printStackTrace();
        }
        fm= null;
        try {
            fm = b.getPlayerByColor(Player.PlayerColor.BLUE).getFamilyMemberFromColor("white");
        } catch (Exception e) {
            e.printStackTrace();
        }
        blueCoin-=4;
        assertEquals(false, exception);
        assertEquals(true, fm.isUsed());
        assertEquals(4, fm.owner.getNumberOfCards(Card.CardType.VENTURE));
        assertEquals(blueCoin, fm.owner.getResource(ResourceType.COIN).get());
        //end 18th move------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(29));//RED fm orange in slot2
        } catch (Exception e){
            exception = true;
            e.printStackTrace();
        }
        redServant+=1;
        assertEquals(false, exception);
        assertEquals(redServant, fm.owner.getResource(ResourceType.SERVANT).get());
        assertEquals(2, fm.owner.getNumberOfCards(Card.CardType.TERRITORY));
        //end 19th move------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(30));//Blue fm black ghost move
        } catch (Exception e){
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        //end 20th move------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(31));//RED fm neutral in slot1
        } catch (Exception e){
            exception = true;
            e.printStackTrace();
        }
        assertEquals(true, exception);
        assertEquals(redServant, b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.SERVANT).get());
        assertEquals(2,b.getPlayerByColor(Player.PlayerColor.RED).getNumberOfCards(Card.CardType.TERRITORY));
        //CHEAT MODE layer RED-----------------------------------------------------------------------------------------
        b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.MILITARYPOINTS).add(1); redServant+=3;
        //re-try-------------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(32));//RED fm neutral in slot1
        } catch (Exception e){
            exception = true;
            e.printStackTrace();
        }
        redServant-=1; redCoin+=1;
        assertEquals(false, exception);
        assertEquals(redServant, fm.owner.getResource(ResourceType.SERVANT).get());
        assertEquals(3, fm.owner.getNumberOfCards(Card.CardType.TERRITORY));
        assertEquals(redCoin, fm.owner.getResource(ResourceType.COIN).get());
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

    private void printStatus(){
        //BLUE
        System.out.println("bluecoin:"+blueCoin);
        System.out.println("blueservant:"+blueServant);
        System.out.println("blueStone:"+blueStone);
        System.out.println("blueWooD:"+blueWooD);
        System.out.println("blueMilitaryPts:"+blueMilitaryPts);
        System.out.println("blueFaithPts:"+blueFaithPts);
        System.out.println("blueVictoryPts:"+blueVictoryPts);
        //RED
        System.out.println("redCoin:"+redCoin);
        System.out.println("redServant:"+redServant);
        System.out.println("redStone:"+redStone);
        System.out.println("redWood:"+redWood);
        System.out.println("redMilitaryPts:"+redMilitaryPts);
        System.out.println("redFaithPts:"+redFaithPts);
        System.out.println("redVictoryPts:"+redVictoryPts);
    }
}