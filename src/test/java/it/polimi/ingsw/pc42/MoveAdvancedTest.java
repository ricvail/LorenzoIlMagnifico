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
        JsonNode mosse = GameInitializer.readFile("src/res/mosse_per_moveAdvancedTest.json").get("moves");
        boolean exception = false;
        String message = "";
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
        /*
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
        System.out.println(message);
        */
        //re-try-----------------------------------------------------------------------------------------------------

        try {
            board.makeMove(mosse.get(8)); //Blue fm white + 4 servants, harvest slotIID 19
        } catch (ActionAbortedException ae){
            exception = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, exception);
        FamilyMember fm = null;
        try {
            fm = board.getPlayerByColor(Player.PlayerColor.BLUE).getFamilyMemberFromColor("white");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, fm.isUsed());
        blueServant-=3; blueStone+=2; blueWooD+=2; blueMilitaryPts+=2;
        //Test
        assertEquals(blueServant, fm.owner.getResource(ResourceType.SERVANT).get());
        //assertEquals(blueStone, fm.owner.getResource(ResourceType.STONE).get());
        //assertEquals(blueWooD, fm.owner.getResource(ResourceType.WOOD).get());
        //assertEquals(blueMilitaryPts, fm.owner.getResource(ResourceType.MILITARYPOINTS).get());

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
        try {
            board.makeMove(nodeGhostMove("neutral"));//Blue ghost neutral
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        /*
        try {
            board.makeMove(mosse.get(12));//Red fm fm black slotID 6, draw slot Id 11 ma exception -> vietato val 5
        } catch (ActionAbortedException ae){
            exception = true;
            message = ae.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, exception);
        System.out.println(message);
        fm = null;
        try {
            fm = board.getCurrentPlayer().getFamilyMemberFromColor("black");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, fm.isUsed());*/
        //re-try------------------------------------------------------------------------------------------------------
        try {
            board.makeMove(nodeGhostMove("black"));//Red ghost white
        } catch (Exception e) {
            e.printStackTrace();
        }
        //end  18th move----------------------------------------------------------------------------------------------
        try {
            board.makeMove(mosse.get(13));//Blue harvest val 3 con fm orange
        }catch (ActionAbortedException ae){
            exception = true;
            message = ae.getMessage();
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
        //assertEquals(blueStone, fm.owner.getResource(ResourceType.STONE).get());
        //assertEquals(blueWooD, fm.owner.getResource(ResourceType.WOOD).get());
        //end 19th move------------------------------------------------------------------------------------------------



        //zappata <5 e una production
        //production, provare che la scelta 0 non fa niente, provare una scelta, provare quella con privileges e che
        //le resource per le trade siano controllate tutte prima, non può usare quelle di privileges per quella dopo
        //prendere un paio di venture con final victory



        printStatus();
        printResources(board.getPlayerByColor(Player.PlayerColor.RED));
        printResources(board.getPlayerByColor(Player.PlayerColor.BLUE));

    }



    private void printStatus(){
        //BLUE
        System.out.println();
        System.out.print(" bluecoin:"+blueCoin);
        System.out.print(" blueservant:"+blueServant);
        System.out.print(" blueStone:"+blueStone);
        System.out.print(" blueWooD:"+blueWooD);
        System.out.print(" blueMilitaryPts:"+blueMilitaryPts);
        System.out.print(" blueFaithPts:"+blueFaithPts);
        System.out.print(" blueVictoryPts:"+blueVictoryPts);
        //RED
        System.out.println();
        System.out.print(" redCoin:"+redCoin);
        System.out.print(" redServant:"+redServant);
        System.out.print(" redStone:"+redStone);
        System.out.print(" redWood:"+redWood);
        System.out.print(" redMilitaryPts:"+redMilitaryPts);
        System.out.print(" redFaithPts:"+redFaithPts);
        System.out.print(" redVictoryPts:"+redVictoryPts);
    }

    private void printResources(Player player){
        System.out.println();
        for(ResourceType rt : ResourceType.values()){
            System.out.print(" #"+player.getColor().getPlayerColorString());
            System.out.print(rt.getString()+":"+player.getResource(rt).get());
        }

    }
}
