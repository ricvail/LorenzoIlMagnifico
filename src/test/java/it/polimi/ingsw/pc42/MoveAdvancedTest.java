package it.polimi.ingsw.pc42;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.Card;
import it.polimi.ingsw.pc42.Control.ResourceType;
import it.polimi.ingsw.pc42.Model.Board;
import it.polimi.ingsw.pc42.Model.Dice;
import it.polimi.ingsw.pc42.Model.FamilyMember;
import it.polimi.ingsw.pc42.Model.Player;
import it.polimi.ingsw.pc42.Utilities.GameInitializer;
import it.polimi.ingsw.pc42.Utilities.myException;
import it.polimi.ingsw.pc42.View.OutputStringGenerator;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;

import static it.polimi.ingsw.pc42.Control.MoveManager.nodeGhostMove;

public class MoveAdvancedTest extends TestCase {

    public MoveAdvancedTest(String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( MoveAdvancedTest.class );
    }

    private int blueServant = 3;  private int blueStone = 2;  private int blueWooD = 2;  private int blueCoin = 5;
    private int blueMilitaryPts = 0; private int blueFaithPts = 0; private int blueVictoryPts = 0;
    private int redServant = 3;  private int redStone = 2;  private int redWood = 2;  private int redCoin = 6;
    private int redMilitaryPts = 0; private int redFaithPts = 0; private int redVictoryPts = 0;
    //BONUS TILE: 1 militaryPoint, 2 coins | 1 wood, 1 stone, 1 servant

    public void testAdvancedMove(){
        ArrayList<Player> players = new ArrayList<>();
        players.add(Player.createPlayer("blue"));
        players.add(Player.createPlayer("red"));

        Board board = GameInitializer.initBaseGame(players , false, true);
        JsonNode mosse = GameInitializer.readFile("src/res/testing/mosse_per_moveAdvancedTest.json").get("moves");
        boolean exception = false;
        String message = "";
        printFullStatus(board);
        //1st move----------------------------------------------------------------------------------------------------
        try {
            board.makeMove(mosse.get(0));//Blue fm orange slotID 2, card harvest val 2 ->1 wood
        } catch (Exception e) {
            e.printStackTrace();
        }
        blueWooD+=1;
        //end 1st move-------------------------------------------------------------------------------------------------
        try {
            board.makeMove(mosse.get(1));//Red fm black slotID 7, card +2 bonus draw char e discount 1 coin
        } catch (Exception e) {
            e.printStackTrace();
        }
        redCoin-=4; redStone+=1;
        //end 2nd move-------------------------------------------------------------------------------------------------
        try {
            board.makeMove(mosse.get(2));//Blue fm black fm black slotID 10, card vict foreach terr-> production val 5
        } catch (Exception e) {
            e.printStackTrace();
        }
        blueVictoryPts+=5; blueStone-=1; blueWooD-=3;
        //end of 3rd---------------------------------------------------------------------------------------------------
        try {
            board.makeMove(nodeGhostMove("orange"));//Red ghost orange
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            board.makeMove(nodeGhostMove("white"));//Blue ghost white
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            board.makeMove(nodeGhostMove("white"));//Red ghost white
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            board.makeMove(nodeGhostMove("neutral"));//Blue ghost neutral
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            board.makeMove(nodeGhostMove("neutral"));//Red ghost neutral
        } catch (Exception e) {
            e.printStackTrace();
        }

        //end of first round-------------------------------------------------------------------------------------------
        try {
            board.makeMove(mosse.get(3));//Blue fm black slotID 3, card harvest val 5 -> 2 mil 1 stone
        } catch (Exception e) {
            e.printStackTrace();
        }
        blueWooD+=1;
        //end 9th move------------------------------------------------------------------------------------------------
        //CHEAT MODE player Red---------------------
        board.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.COIN).add(1); redCoin+=1;
        //------------------------------------------
        try {
            board.makeMove(mosse.get(4));//RED fm orange slotID 6, card +2 production val
        } catch (Exception e) {
            e.printStackTrace();
        }
        redCoin-=3;
        //end 10th move-----------------------------------------------------------------------------------------------
        try {
            board.makeMove(mosse.get(5));//Blue fm orange + 2 serv in slotID 7, -5 coins -> divieto 5 e 7 per sempre
        } catch (Exception e) {
            e.printStackTrace();
        }
        blueServant-=2; blueCoin-=5; blueFaithPts+=4; blueStone+=1;
        //end 11th move----------------------------------------------------------------------------------------------
        try {
            board.makeMove(mosse.get(6));//Red fm black +1 serv slotId 12 card prod val 1 -> 1 coin per 1 privileges
        } catch (Exception e) {
            e.printStackTrace();
        }
        redStone-=2; redMilitaryPts+=2; redServant-=1; redVictoryPts+=1;
        //end 12th move-----------------------------------------------------------------------------------------------
        //CHEAT MODE player Blue---------------------
        board.getPlayerByColor(Player.PlayerColor.BLUE).getResource(ResourceType.SERVANT).add(3); blueServant+=3;
        board.getPlayerByColor(Player.PlayerColor.BLUE).getResource(ResourceType.MILITARYPOINTS).add(5); blueMilitaryPts+=5;
        //-------------------------------------------
        try {
            board.makeMove(mosse.get(7));//Blue tries fm white + servants slotID 15 -> exception
        } catch (ActionAbortedException ae){
            exception = true;
            message = ae.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, exception);
        FamilyMember fm = null;
        try {
            fm = board.getCurrentPlayer().getFamilyMemberFromColor("white");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, fm.isUsed());
        assertEquals(blueServant, board.getCurrentPlayer().getResource(ResourceType.SERVANT).get());
        //re-try-----------------------------------------------------------------------------------------------------
        exception=false;
        try {
            board.makeMove(mosse.get(8)); //Blue fm white + 4 servants, harvest slotID 19
        } catch (ActionAbortedException ae){
            ae.printStackTrace();
            exception = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, exception);
        fm = null;
        try {
            fm = board.getPlayerByColor(Player.PlayerColor.BLUE).getFamilyMemberFromColor("white");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, fm.isUsed());
        blueServant-=3; blueStone+=2; blueWooD+=2; blueMilitaryPts+=2;
        //Test
        assertEquals(blueServant, fm.owner.getResource(ResourceType.SERVANT).get());
        assertEquals(blueStone, fm.owner.getResource(ResourceType.STONE).get());
        assertEquals(blueWooD, fm.owner.getResource(ResourceType.WOOD).get());
        assertEquals(blueMilitaryPts, fm.owner.getResource(ResourceType.MILITARYPOINTS).get());

        //end 13th move----------------------------------------------------------------------------------------------
        //CHEAT MODE player Red---------------------
        board.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.SERVANT).add(3); redServant+=3;
        board.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.COIN).add(4); redCoin+=4;
        board.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.STONE).add(1); redStone+=1;
        //------------------------------------------
        try {
            board.makeMove(mosse.get(9));//Red fm neutral +3 servants slotID 10, card prod val 3 -> choice 1/2 stone per 3/5 coins
        } catch (Exception e) {
            e.printStackTrace();
        }
        // cost4 coins 2 stone, +2 victor
        redCoin-=4; redStone-=2; redVictoryPts+=2; redServant-=3;
        //end 14th move-----------------------------------------------------------------------------------------------
        //CHEAT MODE player Blue---------------------
        board.getPlayerByColor(Player.PlayerColor.BLUE).getResource(ResourceType.SERVANT).add(7); blueServant+=7;
        board.getPlayerByColor(Player.PlayerColor.BLUE).getResource(ResourceType.COIN).add(3); blueCoin+=3;
        //-------------------------------------------
        try {
            board.makeMove(mosse.get(16));//Blue fm neutral +7 servants slotID 4, card harvest val 6 -> privilege
        } catch (ActionAbortedException e) {
            exception = true;
            e.printStackTrace();
        } catch (myException e) {
            e.printStackTrace();
        }
        assertEquals(false, exception);
        blueServant-=7;
        assertEquals(blueCoin,board.getPlayerByColor(Player.PlayerColor.BLUE).getResource(ResourceType.COIN).get() );
        assertEquals(blueWooD, board.getPlayerByColor(Player.PlayerColor.BLUE).getResource(ResourceType.WOOD).get());
        //end 15th move-----------------------------------------------------------------------------------------------
        try {
            board.makeMove(nodeGhostMove("white"));//Red ghost white
        } catch (Exception e) {
            e.printStackTrace();
        }
        //end of second round-----------------------------------------------------------------------------------------
        //VATICAN PHASE------------------------------------------------------------------------------------------------
        //cheat mode
        exception = false;
        try {
            board.makeMove(mosse.get(10));//VATICAN CHOICE BLU
        } catch (Exception e) {
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        assertEquals(true, board.isVatican());

        exception = false;
        try {
            board.makeMove(mosse.get(10));//VATICAN CHOICE RED
        } catch (Exception e) {
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        assertEquals(false, board.isVatican());
        //END VATICAN PHASE--------------------------------------------------------------------------------------------

        //CHEAT MODE player Blue---------------------
        board.getPlayerByColor(Player.PlayerColor.BLUE).getResource(ResourceType.COIN).add(6); blueCoin+=6;
        //-------------------------------------------
        try {
            board.makeMove(mosse.get(11)); //Blue fm black slotID 13 , 5 final victory points
        } catch (Exception e) {
            e.printStackTrace();
        }
        //cost 6 coins, + 6 military
        blueMilitaryPts+=6; blueCoin-=6;
        //end 17th move------------------------------------------------------------------------------------------------
        //Red ha +2 su char ma lo testo dopo
        //CHEAT MODE player Red---------------------
        board.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.COIN).add(4); redCoin+=4;
        board.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.WOOD).add(1); redWood+=1;
        //------------------------------------------
        try {
            board.makeMove(mosse.get(12));//Red fm fm black slotID 6, draw slot Id 11 ma exception -> vietato val 5
        } catch (ActionAbortedException ae){
            exception = true;
            message = ae.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, exception);
        fm = null;
        try {
            fm = board.getCurrentPlayer().getFamilyMemberFromColor("black");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, fm.isUsed());
        //re-try------------------------------------------------------------------------------------------------------
        exception=false;
        try {
            board.makeMove(nodeGhostMove("black"));//Red ghost black
        } catch (Exception e) {
            e.printStackTrace();
        }
        //end  18th move----------------------------------------------------------------------------------------------
        try {
            board.makeMove(mosse.get(13));//Blue harvest val 3 con fm orange
        } catch (ActionAbortedException ae){
            exception = true;
            message = ae.getMessage();
            ae.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, exception);
        blueServant+=1; blueStone+=1; blueWooD+=2;
        fm = null;
        try {
            fm = board.getPlayerByColor(Player.PlayerColor.BLUE).getFamilyMemberFromColor("orange");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, fm.isUsed());
        assertEquals(blueServant, fm.owner.getResource(ResourceType.SERVANT).get());
        assertEquals(blueStone, fm.owner.getResource(ResourceType.STONE).get());
        assertEquals(blueWooD, fm.owner.getResource(ResourceType.WOOD).get());
        //end 19th move------------------------------------------------------------------------------------------------
        //scelgo di attivare i privileges (1 stone/wood) e la seconda scelta della scenda carta, fallisce per stone
        try {
            board.makeMove(mosse.get(14));//REd fm white production val 3 slotID 18 -> exception
        }  catch (ActionAbortedException ae){
            exception = true;
            message = ae.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, exception);
        fm = null;
        try {
            fm = board.getPlayerByColor(Player.PlayerColor.RED).getFamilyMemberFromColor("white");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, fm.isUsed());
        assertEquals(redCoin, fm.owner.getResource(ResourceType.COIN).get());
        assertEquals(redStone, fm.owner.getResource(ResourceType.STONE).get());
        assertEquals(redMilitaryPts, fm.owner.getResource(ResourceType.MILITARYPOINTS).get());
        //re-try------------------------------------------------------------------------------------------------------
        //CHEAT MODE player Red---------------------
        board.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.STONE).add(2); redStone+=2;
        //------------------------------------------
        exception=false;
        try {
            board.makeMove(mosse.get(14));//REd fm white production val 3 slotID 18
        }  catch (ActionAbortedException ae){
            exception = true;
            message = ae.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, exception);
        fm = null;
        try {
            fm = board.getPlayerByColor(Player.PlayerColor.RED).getFamilyMemberFromColor("white");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, fm.isUsed());
        //1 coin per 1 stone/1wood, 2 stone per 5 coins, base tile 1 milpts 2 coins
        redCoin+=6; redStone-=1; redMilitaryPts+=1; redWood+=1;
        //test
        assertEquals(redCoin, fm.owner.getResource(ResourceType.COIN).get());
        assertEquals(redStone, fm.owner.getResource(ResourceType.STONE).get());
        assertEquals(redWood, fm.owner.getResource(ResourceType.WOOD).get());
        assertEquals(redMilitaryPts, fm.owner.getResource(ResourceType.MILITARYPOINTS).get());
        //end of 20 th move--------------------------------------------------------------------------------------------
        try {
            board.makeMove(nodeGhostMove("white"));//Blue ghost white
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            board.makeMove(nodeGhostMove("orange"));//Red ghost orange
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            board.makeMove(nodeGhostMove("neutral"));//Blue ghost neutral
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            board.makeMove(nodeGhostMove("neutral"));//Red ghost neutral
        } catch (Exception e) {
            e.printStackTrace();
        }
        //end 3rd round-----------------------------------------------------------------------------------------------
        for (Dice.DiceColor diceColor : Dice.DiceColor.values()){
            if (!"ghost".equalsIgnoreCase(diceColor.getDiceColorString())){
                try {
                    board.makeMove(nodeGhostMove(diceColor.getDiceColorString())); //blue
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                try {
                    board.makeMove(nodeGhostMove(diceColor.getDiceColorString())); //red
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        //end 4th round------------------------------------------------------------------------------------------------

        //VATICAN PHASE------------------------------------------------------------------------------------------------
        //cheat mode
        exception = false;
        try {
            board.makeMove(mosse.get(10));//VATICAN CHOICE BLU
        } catch (Exception e) {
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        assertEquals(true, board.isVatican());

        exception = false;
        try {
            board.makeMove(mosse.get(10));//VATICAN CHOICE RED
        } catch (Exception e) {
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        assertEquals(false, board.isVatican());
        //END VATICAN PHASE--------------------------------------------------------------------------------------------


        for (Dice.DiceColor diceColor : Dice.DiceColor.values()){
            if (!"ghost".equalsIgnoreCase(diceColor.getDiceColorString())){
                try {
                    board.makeMove(nodeGhostMove(diceColor.getDiceColorString())); //blue
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    board.makeMove(nodeGhostMove(diceColor.getDiceColorString())); //red
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        //end 5th round-----------------------------------------------------------------------------------------------
        board.getCurrentPlayer().getResource(ResourceType.COIN).add(4);blueCoin+=4;
        board.getCurrentPlayer().getResource(ResourceType.SERVANT).add(3);blueServant+=3;
        exception=false;
        try {
            board.makeMove(mosse.get(15));//Blue slotID 14 -> production val 3, 5 final victory
        } catch (ActionAbortedException ae){
            exception = true;
            ae.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, exception);
        //cost 3 serv 4 coins
        blueServant-=3; blueCoin-=4;
        //harvest -> 2 victory points aggiungendo 2 servants
        blueServant-=2; blueCoin+=5; blueMilitaryPts+=1;
        fm = null;
        try {
            fm = board.getPlayerByColor(Player.PlayerColor.BLUE).getFamilyMemberFromColor("orange");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, fm.isUsed());
        assertEquals(blueCoin, fm.owner.getResource(ResourceType.COIN).get());
        assertEquals(blueServant, fm.owner.getResource(ResourceType.SERVANT).get());
        assertEquals(blueVictoryPts, fm.owner.getResource(ResourceType.VICTORYPOINTS).get());
       //end move-----------------------------------------------------------------------------------------------------
        try {
            board.makeMove(nodeGhostMove("orange"));//Red ghost orange
        } catch (Exception e) {
            e.printStackTrace();
        }
        //------------------------------------------------------------------------------------------------------------
        assertEquals(3, board.getCurrentPlayer().getNumberOfCards(Card.CardType.TERRITORY));
        try {
            board.makeMove(mosse.get(17));//Blue harvest val 6
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, exception);
        fm = null;
        try {
            fm = board.getPlayerByColor(Player.PlayerColor.BLUE).getFamilyMemberFromColor("black");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, fm.isUsed());
        blueServant+=1; blueStone+=3; blueWooD+=3; blueMilitaryPts+=2;
        assertEquals(blueServant, fm.owner.getResource(ResourceType.SERVANT).get());
        assertEquals(blueStone, fm.owner.getResource(ResourceType.STONE).get());
        assertEquals(blueWooD, fm.owner.getResource(ResourceType.WOOD).get());
        assertEquals(blueMilitaryPts, fm.owner.getResource(ResourceType.MILITARYPOINTS).get());
        //-----------------------------------------------------------------------------------------------------------
        try {
            board.makeMove(nodeGhostMove("white"));//Red ghost white
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            board.makeMove(nodeGhostMove("neutral"));//Blue ghost neutral
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            board.makeMove(nodeGhostMove("neutral"));//Red ghost neutral
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            board.makeMove(nodeGhostMove("white"));//Blue ghost white
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            board.makeMove(nodeGhostMove("black"));//Red ghost black
        } catch (Exception e) {
            e.printStackTrace();
        }
        //END OF THE GAME - FINAL CHECK--------------------------------------------------------------------------------
        /* to check:
        * -number of characters cards
        * -number of territories cards
        * -military points for first e second
        * -faith points
        * -(wood,stone,coins,servant)/5
        * -victory points +final victory points */
        //BLUE check

        int finalVictoryPoints = blueVictoryPts;
        finalVictoryPoints+=1 ; // for 1 char cards
        finalVictoryPoints+=5; // for military points
        finalVictoryPoints+=1; // territory cards
        finalVictoryPoints+=4; // faith points
        finalVictoryPoints+=10; // for final victory points
        finalVictoryPoints+=((blueWooD+blueStone+blueServant+blueCoin)/5); //2
        assertEquals(finalVictoryPoints, board.getPlayerByColor(Player.PlayerColor.BLUE).getResource(ResourceType.VICTORYPOINTS).get());

        //RED check
        finalVictoryPoints = redVictoryPts;
        finalVictoryPoints+=3; // for char cards
        finalVictoryPoints+=0; // territory cards
        finalVictoryPoints+=2; // for military points
        finalVictoryPoints+=0; // faith points
        finalVictoryPoints+=0; // for final victory points

        finalVictoryPoints+=((redWood+redStone+redServant+redCoin)/5); //3
        assertEquals(finalVictoryPoints, board.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.VICTORYPOINTS).get());
    }


    public void printFullStatus(Board b){
        JsonNode j = b.generateJsonDescription();
        try {
            System.out.println(OutputStringGenerator.ArrayToString(OutputStringGenerator.generateOutputStringOf_A(j, "TERRITORY")));
            System.out.println(OutputStringGenerator.ArrayToString(OutputStringGenerator.generateOutputStringOf_A(j, "BUILDING")));
            System.out.println(OutputStringGenerator.ArrayToString(OutputStringGenerator.generateOutputStringOf_A(j, "CHARACTER")));
            System.out.println(OutputStringGenerator.ArrayToString(OutputStringGenerator.generateOutputStringOf_A(j, "VENTURE")));
            System.out.println(OutputStringGenerator.ArrayToString(OutputStringGenerator.generateOutputStringOf_A(j, "HARVEST")));
            System.out.println(OutputStringGenerator.ArrayToString(OutputStringGenerator.generateOutputStringOf_A(j, "PRODUCTION")));
            System.out.println(OutputStringGenerator.ArrayToString(OutputStringGenerator.getPlayerStatus(j, "RED")));
            System.out.println(OutputStringGenerator.ArrayToString(OutputStringGenerator.getPlayerStatus(j, "BLUE")));
        } catch (ActionAbortedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
