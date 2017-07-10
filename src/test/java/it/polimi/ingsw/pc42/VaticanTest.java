package it.polimi.ingsw.pc42;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.polimi.ingsw.pc42.Control.ResourceType;
import it.polimi.ingsw.pc42.Model.Board;
import it.polimi.ingsw.pc42.Model.Player;
import it.polimi.ingsw.pc42.Utilities.GameInitializer;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import static it.polimi.ingsw.pc42.Control.MoveManager.nodeGhostMove;

/**
 * Created by diego on 04/07/2017.
 */
public class VaticanTest extends TestCase
{
    public VaticanTest(String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( VaticanTest.class );
    }

    public void testVaticanPhase(){
        JsonNode mosse = GameInitializer.readFile("src/res/testing/vaticanPhaseTestMove.json").get("moves");
        Board b = GameInitializer.initBaseGame(false);
        boolean exception=false;
        try {
            b.makeMove(nodeGhostMove("black"));//Red black ghost move
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            b.makeMove(nodeGhostMove("white"));//Blue white ghost move
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            b.makeMove(nodeGhostMove("white"));//Red white ghost move
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            b.makeMove(nodeGhostMove("black"));//Blue black ghost move
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            b.makeMove(nodeGhostMove("orange"));//Red orange ghost move
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            b.makeMove(nodeGhostMove("orange"));//Blue orange ghost move
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            b.makeMove(nodeGhostMove("neutral"));//Red neutral ghost move
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            b.makeMove(nodeGhostMove("neutral"));//Blue neutral ghost move
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            b.makeMove(nodeGhostMove("black"));//Red black ghost move
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            b.makeMove(nodeGhostMove("white"));//Blue white ghost move
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            b.makeMove(nodeGhostMove("white"));//Red white ghost move
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            b.makeMove(nodeGhostMove("black"));//Blue black ghost move
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            b.makeMove(nodeGhostMove("orange"));//Red orange ghost move
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            b.makeMove(nodeGhostMove("orange"));//Blue orange ghost move
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            b.makeMove(nodeGhostMove("neutral"));//Red neutral ghost move
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            b.makeMove(nodeGhostMove("neutral"));//Blue neutral ghost move
        } catch (Exception e) {
            e.printStackTrace();
        }

        //VATICAN PHASE
        //cheat mode
        b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.FAITHPOINTS).add(2);
        //start
        exception = false;
        try {
            b.makeMove(mosse.get(1));//VATICAN CHOICE RED fail couse has only 2 FaithPoints
        } catch (Exception e) {
            exception = true;
            //e.printStackTrace();
        }
        assertEquals(true, exception);
        assertEquals(true, b.isVatican());
        assertEquals(2, b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.FAITHPOINTS).get());
        //cheat mode
        b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.FAITHPOINTS).add(1);
        //start
        exception = false;
        try {
            b.makeMove(mosse.get(1));//VATICAN CHOICE RED, success
        } catch (Exception e) {
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        assertEquals(true, b.isVatican());
        assertEquals(0, b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.FAITHPOINTS).get());
        //start
        exception = false;
        try {
            b.makeMove(mosse.get(0));//VATICAN CHOICE BLUE, success
        } catch (Exception e) {
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        assertEquals(false, b.isVatican());
        assertEquals(0, b.getPlayerByColor(Player.PlayerColor.BLUE).getResource(ResourceType.FAITHPOINTS).get());

    }
}
