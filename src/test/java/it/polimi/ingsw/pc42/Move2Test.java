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
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;

import static it.polimi.ingsw.pc42.Control.MoveManager.nodeGhostMove;

public class Move2Test extends TestCase
{
    public Move2Test(String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( Move2Test.class );
    }

    private int blueServant = 3;  private int blueStone = 2;  private int blueWooD = 2;  private int blueCoin = 6;
    private int blueMilitaryPts = 0; private int blueFaithPts = 0; private int blueVictoryPts = 0;
    private int redServant = 3;  private int redStone = 2;  private int redWood = 2;  private int redCoin = 5;
    private int redMilitaryPts = 0; private int redFaithPts = 0; private int redVictoryPts = 0;


    public void testMove2() throws Exception {
        JsonNode mosse = GameInitializer.readFile("src/res/mosse_per_moveTest2.json").get("moves");
        Board b = GameInitializer.initBaseGame(false);
        //RED and BLUE playing; servants=3, wood=stone=2, coins=5+i
        String message = "";
        //first move--------------------------------------------------------------------------------------------------
        boolean exception = false;
        try {
            b.makeMove(mosse.get(0)); //Rosso pesca carta territorio slot 1 con fm white
        } catch (ActionAbortedException ae) {
            exception = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, exception);
        FamilyMember fm = null;
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
        } catch (ActionAbortedException ae) {
            exception = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, exception);
        //re-try-------------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(2));//Blue piazza fm in slot 12 con fm black e 1 servant, legal -> 2 mpoints e card
        } catch (Exception e) {
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        fm = null;
        try {
            fm = b.getPlayerByColor(Player.PlayerColor.BLUE).getFamilyMemberFromColor("black");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, fm.isUsed());
        assertEquals(1, fm.owner.getNumberOfCards(Card.CardType.BUILDING));
        //+2 immediate actionspace bonus, card cost: 2 wood, 2 coins, card bonus: 6 victory points
        blueServant -= 1;
        blueMilitaryPts += 2;
        blueWooD -= 2;
        blueCoin -= 2;
        blueVictoryPts += 6;
        //Resources Test
        assertEquals(blueServant, fm.owner.getResource(ResourceType.SERVANT).get());
        assertEquals(blueMilitaryPts, fm.owner.getResource(ResourceType.MILITARYPOINTS).get());
        assertEquals(blueWooD, fm.owner.getResource(ResourceType.WOOD).get());
        assertEquals(blueCoin, fm.owner.getResource(ResourceType.COIN).get());
        assertEquals(blueVictoryPts, fm.owner.getResource(ResourceType.VICTORYPOINTS).get());
        //CHEAT MODE player BLUE----------------------------------------------------------------------------------------
        //prima la carta toglieva 1 wood, ora ne toglie 2 quindi noi ne aggiungiamo artificialmente un altro
        //in modo da avere lo stesso risultato finale (1 wood in meno)
        b.getPlayerByColor(Player.PlayerColor.BLUE).getResource(ResourceType.WOOD).add(1);
        blueWooD += 1;
        b.getPlayerByColor(Player.PlayerColor.BLUE).getResource(ResourceType.COIN).add(-2);
        blueCoin -= 2;
        //end second move----------------------------------------------------------------------------------------------
        exception = false;
        try{
            b.makeMove(mosse.get(42));//Turno g rosso, mette fm nero(=6) nello slotID 7, +1 stone, missing privileges choice
        } catch (ActionAbortedException ae) {
            exception = true;
            message = ae.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, exception);
        assertEquals("Missing privileges choices", message);
        //re-try-------------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(3));//Rosso mette fm black(=6) + 1 servant nello slotID 8, +2 stone e card, legal
        } catch (Exception e) {
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        fm = null;
        try {
            fm = b.getPlayerByColor(Player.PlayerColor.RED).getFamilyMemberFromColor("black");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, fm.isUsed());
        assertEquals(1, fm.owner.getNumberOfCards(Card.CardType.CHARACTER));
        //+2 stone immediate action space bonus, card cost: 2 coins, card effect: 1 privileges -> 2 servant (-1)
        redStone += 2;
        redCoin -= 2;
        redServant += 1;
        //Resources Test
        assertEquals(redServant, fm.owner.getResource(ResourceType.SERVANT).get());
        assertEquals(redStone, fm.owner.getResource(ResourceType.STONE).get());
        assertEquals(redCoin, fm.owner.getResource(ResourceType.COIN).get());
        //end third move-----------------------------------------------------------------------------------------------
        //start 4th turn-----------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(4));//Blue slotID 13, orange, cost carta > resources -> exception
        } catch (ActionAbortedException ae) {
            exception = true;
            message = ae.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, exception);
        assertEquals("Not enough coins to draw the card", message);
        //re-try-------------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(5));//Blue slotID 14, cost ok -> legale, fm white + 2 servants
        } catch (Exception e) {
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        fm = null;
        try {
            fm = b.getPlayerByColor(Player.PlayerColor.BLUE).getFamilyMemberFromColor("white");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, fm.isUsed());
        assertEquals(1, fm.owner.getNumberOfCards(Card.CardType.VENTURE));
        // -2 servants, card cost: stone=coin=wood=1, card effect: 1 faithpoint
        blueServant -= 2;
        blueStone -= 1;
        blueCoin -= 1;
        blueWooD -= 1;
        blueFaithPts += 1;
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
        } catch (ActionAbortedException ae) {
            exception = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, exception);
        //check if servant is unused again
        FamilyMember redNeutral = null;
        try {
            redNeutral = b.getPlayerByColor(Player.PlayerColor.RED).getFamilyMemberFromColor("neutral");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(0, redNeutral.getValue());
        assertEquals(redServant, b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.SERVANT).get());
        //re-try-------------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(7));//Rosso ritenta mossa precedente in slotID 9
        } catch (Exception e) {
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        fm = null;
        try {
            fm = b.getPlayerByColor(Player.PlayerColor.RED).getFamilyMemberFromColor("neutral");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, fm.isUsed());
        assertEquals(1, fm.owner.getNumberOfCards(Card.CardType.BUILDING));
        // -1 servant, cointax: 3, card cost: 1 wood 3 stone, card effect: 5 victory points
        redServant -= 1;
        redCoin -= 3;
        redWood -= 1;
        redStone -= 3;
        redVictoryPts += 5;
        //Resources Test
        assertEquals(redServant, fm.owner.getResource(ResourceType.SERVANT).get());
        assertEquals(redCoin, fm.owner.getResource(ResourceType.COIN).get());
        assertEquals(redStone, fm.owner.getResource(ResourceType.STONE).get());
        assertEquals(redWood, fm.owner.getResource(ResourceType.WOOD).get());
        assertEquals(redVictoryPts, fm.owner.getResource(ResourceType.VICTORYPOINTS).get());
        //CHEAT MODE Player blue---------------------------------------------------------------------------------------
        b.getPlayerByColor(Player.PlayerColor.BLUE).getResource(ResourceType.COIN).add(3);
        blueCoin += 3;
        b.getPlayerByColor(Player.PlayerColor.BLUE).getResource(ResourceType.STONE).add(2);
        blueStone += 2;
        b.getPlayerByColor(Player.PlayerColor.BLUE).getResource(ResourceType.SERVANT).add(6);
        blueServant += 6;
        //end fifth move-----------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(8));//Blue slotID 15 con fm neutral +5 servants, +1 coins actionspace, -3 cointax
        } catch (Exception e) {
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        fm = null;
        try {
            fm = b.getPlayerByColor(Player.PlayerColor.BLUE).getFamilyMemberFromColor("neutral");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, fm.isUsed());
        assertEquals(2, fm.owner.getNumberOfCards(Card.CardType.VENTURE));
        //-5 servant, +1-3 coin bonus e tax, card cost: 3 stone, card effect: 2 military points, 1 privilege -> 2 mlpts
        blueServant -= 5;
        blueCoin -= 2;
        blueStone -= 3;
        blueMilitaryPts += 4;
        //Resources Test
        assertEquals(blueServant, fm.owner.getResource(ResourceType.SERVANT).get());
        assertEquals(blueCoin, fm.owner.getResource(ResourceType.COIN).get());
        assertEquals(blueStone, fm.owner.getResource(ResourceType.STONE).get());
        assertEquals(blueMilitaryPts, fm.owner.getResource(ResourceType.MILITARYPOINTS).get());
        //end sixth move-----------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(9));//Rosso tenta di mettere fm orange in slotID 2, cointax -> exception
        } catch (ActionAbortedException ae) {
            exception = true;
            message = ae.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals("You don't have enough coins to pay the Tax", message);
        assertEquals(true, exception);
        //CHEAT MODE player RED----------------------------------------------------------------------------------------
        b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.WOOD).add(1);
        redWood += 1;
        b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.STONE).add(1);
        redStone += 1;
        b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.SERVANT).add(3);
        redServant += 3;
        b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.COIN).add(3);
        redCoin += 3;
        //re-try-------------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(10));//Rosso ritenta fm orange in slot 16, 2 privileges-> exception 2 privileges uguali
        } catch (ActionAbortedException ae) {
            exception = true;
            message = ae.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, exception);
        assertEquals("You've chosen the same privilege more than once", message);
        //check if servant is unused again
        redNeutral = null;
        try {
            redNeutral = b.getPlayerByColor(Player.PlayerColor.RED).getFamilyMemberFromColor("orange");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(3, redNeutral.getValue());
        assertEquals(redServant, b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.SERVANT).get());
        //re-try-------------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(11));//Rosso ritenta fm orange in slot 16, 3 cointax, 2 privileges -> legale
        } catch (Exception e) {
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        fm = null;
        try {
            fm = b.getPlayerByColor(Player.PlayerColor.RED).getFamilyMemberFromColor("orange");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, fm.isUsed());
        assertEquals(1, fm.owner.getNumberOfCards(Card.CardType.VENTURE));
        //-4 servants, +2-3 coins bonus, card cost: wood=stone=2, card effect: 2 privileges-> 1 wood=stone, 1 faithpoint
        redServant -= 4;
        redCoin -= 1;
        redStone -= 1;
        redWood -= 1;
        redFaithPts += 1;
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
        } catch (Exception e) {
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        fm = null;
        try {
            fm = b.getPlayerByColor(Player.PlayerColor.BLUE).getFamilyMemberFromColor("neutral");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //1 servant e privilege-> 2 coins
        blueServant -= 1;
        blueCoin += 3;
        //Resources Test
        assertEquals(blueServant, fm.owner.getResource(ResourceType.SERVANT).get());
        assertEquals(blueCoin, fm.owner.getResource(ResourceType.COIN).get());
        //END OF FIRST ROUND-------------------------------------------------------------------------------------------
        //Clean-Up test
        boolean blueTurn = b.isPlayerTurn(Player.createPlayer("blue"));
        assertEquals(true, blueTurn);
        //check if all the family members are unused
        boolean blueFMUsed = checkFamilyMemberUsed(b.getPlayerByColor(Player.PlayerColor.BLUE).getFamilyMembers());
        assertEquals(false, blueFMUsed);
        boolean redFMUsed = checkFamilyMemberUsed(b.getPlayerByColor(Player.PlayerColor.RED).getFamilyMembers());
        assertEquals(false, redFMUsed);
        //TODO check cards cleanup
        assertEquals(1, b.getRound());
        //CHEAT MODE player Blue---------------------------------------------------------------------------------------
        b.getPlayerByColor(Player.PlayerColor.BLUE).getResource(ResourceType.SERVANT).add(1);
        blueServant += 1;
        //START SECOND ROUND-------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(13));//Blue fm black +1 servant, + 2coins, payment 1 -> fallisce per le resources
        } catch (ActionAbortedException ae) {
            exception = true;
            message = ae.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, exception);
        assertEquals("Not enough stone to draw the card", message);
        //undo of immediate bonus
        assertEquals(blueCoin, b.getPlayerByColor(Player.PlayerColor.BLUE).getResource(ResourceType.COIN).get());
        //re-try-------------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(14));//Blue fm black +1 servant, + 2coins, payment 0-> legale
        } catch (Exception e) {
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        fm = null;
        try {
            fm = b.getPlayerByColor(Player.PlayerColor.BLUE).getFamilyMemberFromColor("black");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, fm.isUsed());
        assertEquals(3, fm.owner.getNumberOfCards(Card.CardType.VENTURE));
        // +2 immediate bonus coins, -1 servant, card cost: mpts 4 needed -2, effect: 3 fpts
        blueServant -= 1;
        blueCoin += 2;
        blueMilitaryPts -= 2;
        blueFaithPts += 3;
        //Resources Test
        assertEquals(blueServant, fm.owner.getResource(ResourceType.SERVANT).get());
        assertEquals(blueCoin, fm.owner.getResource(ResourceType.COIN).get());
        assertEquals(blueMilitaryPts, fm.owner.getResource(ResourceType.MILITARYPOINTS).get());
        assertEquals(blueFaithPts, fm.owner.getResource(ResourceType.FAITHPOINTS).get());
        //CHEAT MODE player RED---------------------------------------------------------------------------------------
        b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.COIN).add(1);
        redCoin += 1;
        //end ninth move-----------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(15));//RED fm black + 1 servant in slotID 8, -3 coins, extracard slotID 6 -> fail cointax
        } catch (ActionAbortedException ae) {
            exception = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, exception);
        assertEquals(redStone, b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.STONE).get());
        //CHEAT MODE player RED---------------------------------------------------------------------------------------
        b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.COIN).add(5);
        redCoin += 5;
        //re-try-------------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(16));//RED fm black + 1 servant in slotID 8, -3 coins,extra card slotID 7 (-3 cointax)
        } catch (Exception e) {
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        fm = null;
        try {
            fm = b.getPlayerByColor(Player.PlayerColor.RED).getFamilyMemberFromColor("black");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, fm.isUsed());
        assertEquals(3, fm.owner.getNumberOfCards(Card.CardType.CHARACTER));
        //-2 servants, +3 stone bonus, -8 coins, +5 fpts
        redServant -= 2;
        redCoin -= 8;
        redStone += 3;
        redFaithPts += 5;
        //Resources Test
        assertEquals(redServant, fm.owner.getResource(ResourceType.SERVANT).get());
        assertEquals(redCoin, fm.owner.getResource(ResourceType.COIN).get());
        assertEquals(redStone, fm.owner.getResource(ResourceType.STONE).get());
        assertEquals(redFaithPts, fm.owner.getResource(ResourceType.FAITHPOINTS).get());
        //CHEAT MODE player Blue---------------------------------------------------------------------------------------
        b.getPlayerByColor(Player.PlayerColor.BLUE).getResource(ResourceType.SERVANT).add(2);
        blueServant += 2;
        //end of tenth move--------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(17));//Blue tenta fm orange in slotID 7, exception carta già pescata (empty)
        } catch (ActionAbortedException ae) {
            exception = true;
            message =ae.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, exception);
        assertEquals("You already have 6 characters cards or the tower's Action Space is empty", message);
        //re-try-------------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(18));//Blue fm orange ghost move
        } catch (Exception e) {
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        //end of 11th move-----------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(19));//Red fm orange ghost move
        } catch (Exception e) {
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        //end of 12th move---------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(20));//Blue fm white ghost move
        } catch (Exception e) {
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        //end of 13th move---------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(21));//Red fm white ghost move
        } catch (Exception e) {
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        //end of 14th move---------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(22));//Blue fm neutral ghost move
        } catch (Exception e) {
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        //end of 15th move---------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(23));//Red fm neutral ghost move
        } catch (Exception e) {
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        //Clean-Up test------------------------------------------------------------------------------------------------
        blueTurn = b.isPlayerTurn(Player.createPlayer("blue"));
        assertEquals(true, blueTurn);
        //check if all the family members are unused
        blueFMUsed = checkFamilyMemberUsed(b.getPlayerByColor(Player.PlayerColor.BLUE).getFamilyMembers());
        assertEquals(false, blueFMUsed);
        redFMUsed = checkFamilyMemberUsed(b.getPlayerByColor(Player.PlayerColor.RED).getFamilyMembers());
        assertEquals(false, redFMUsed);
        assertEquals(2, b.getEra());
        //END SECOND ROUND---------------------------------------------------------------------------------------------
        assertEquals(2, b.getRound());

        //VATICAN PHASE------------------------------------------------------------------------------------------------
        //cheat mode
        exception = false;
        try {
            b.makeMove(mosse.get(41));//VATICAN CHOICE BLU
        } catch (Exception e) {
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        assertEquals(true, b.isVatican());

        exception = false;
        try {
            b.makeMove(mosse.get(41));//VATICAN CHOICE RED
        } catch (Exception e) {
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        assertEquals(false, b.isVatican());
        //END VATICAN PHASE--------------------------------------------------------------------------------------------

        //CHEAT MODE player Blue---------------------------------------------------------------------------------------
        b.getPlayerByColor(Player.PlayerColor.BLUE).getResource(ResourceType.WOOD).add(3);
        blueWooD += 3;
        //START THIRD ROUND--------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(24));//Blue fm orange slotID 10, card cost 3 wood, legale
        } catch (ActionAbortedException ae) {
            exception = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, exception);
        // cost 3 wood, effect: 4 vpts
        blueWooD -= 3;
        blueVictoryPts += 4;
        //CHEAT MODE layer RED-----------------------------------------------------------------------------------------
        b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.COIN).add(8);
        redCoin += 8;
        //end 17th move------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(25));//RED fm black in slot7 -> pesca slot6-> pesca slot2-> exception needed buildings
        } catch (ActionAbortedException ae) {
            exception = true;
            message = ae.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //TODO finisce in una exception un po' vaga?
        assertEquals("Family Member can't be place in this Area or this Action Space is not active in 2 players game-mode", message);
        assertEquals(redStone, b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.STONE).get());
        assertEquals(redCoin, b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.COIN).get());
        //CHEAT MODE player RED-----------------------------------------------------------------------------------------
        b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.WOOD).add(3);
        redWood += 3;
        //re-try-------------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(26));//RED fm black in slot7 -> pesca slot6-> pesca slot11-> fallisce per la coin tax
        } catch (ActionAbortedException ae) {
            exception = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, exception);
        assertEquals(redStone, b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.STONE).get());
        assertEquals(redCoin, b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.COIN).get());
        assertEquals(redWood, b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.WOOD).get());
        //CHEAT MODE layer RED-----------------------------------------------------------------------------------------
        b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.SERVANT).add(1);
        redServant += 1;
        //re-try-------------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(27));//RED fm black in slot7 -> pesca slot8-> pesca slot14-> fallisce per costo carta
            // che non viene soddisfatto perchè si sceglie il privileges[0]
        } catch (ActionAbortedException ae) {
            exception = true;
            message = ae.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals("Not enough coins to draw the card", message);
        assertEquals(true, exception);
        assertEquals(redStone, b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.STONE).get());
        assertEquals(redCoin, b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.COIN).get());
        assertEquals(redWood, b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.WOOD).get());
        assertEquals(redServant, b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.SERVANT).get());
        //re-try-------------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(28));//RED fm black in slot7 -> pesca slot8-> pesca slot14-> legale
        } catch (ActionAbortedException ae) {
            exception = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        fm = null;
        try {
            fm = b.getPlayerByColor(Player.PlayerColor.RED).getFamilyMemberFromColor("black");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, exception);
        assertEquals(true, fm.isUsed());
        assertEquals(5, fm.owner.getNumberOfCards(Card.CardType.CHARACTER));
        assertEquals(2, fm.owner.getNumberOfCards(Card.CardType.VENTURE));
        // stone +3 bonus -2 cost, servant -1, coins (((((-3)-3)-2)+2)-2), wood -2
        redServant -= 1;
        redCoin -= 8;
        redStone += 1;
        redWood -= 2;
        redFaithPts += 2;
        //Resources Test
        assertEquals(redServant, fm.owner.getResource(ResourceType.SERVANT).get());
        assertEquals(redCoin, fm.owner.getResource(ResourceType.COIN).get());
        assertEquals(redStone, fm.owner.getResource(ResourceType.STONE).get());
        assertEquals(redWood, fm.owner.getResource(ResourceType.WOOD).get());
        assertEquals(redFaithPts, fm.owner.getResource(ResourceType.FAITHPOINTS).get());
        //end 18th move------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(29));//BLUE fm white in slot13
        } catch (ActionAbortedException ae) {
            exception = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        fm = null;
        try {
            fm = b.getPlayerByColor(Player.PlayerColor.BLUE).getFamilyMemberFromColor("white");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //card cost: coin -6, effect: +6 mpts
        blueCoin -= 6;
        blueMilitaryPts += 6;
        assertEquals(false, exception);
        assertEquals(true, fm.isUsed());
        assertEquals(4, fm.owner.getNumberOfCards(Card.CardType.VENTURE));
        //end 18th move------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(30));//RED fm orange in slot2
        } catch (ActionAbortedException ae) {
            exception = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, exception);
        try {
            fm = b.getPlayerByColor(Player.PlayerColor.RED).getFamilyMemberFromColor("orange");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(2, fm.owner.getNumberOfCards(Card.CardType.TERRITORY));
        //effect: +1 servant
        redServant += 1;
        assertEquals(redServant, fm.owner.getResource(ResourceType.SERVANT).get());
        //end 19th move------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(31));//Blue fm black ghost move
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, exception);
        //CHEAT MODE player RED-----------------------------------------------------------------------------------------
        b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.COIN).add(3);
        redCoin += 3;
        //end 20th move------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(32));//RED fm neutral in slot1 -> exception not enough militaryPoints
        } catch (ActionAbortedException ae) {
            exception = true;
            message = ae.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals("Not enough Military Points to draw this card", message);
        assertEquals(true, exception);
        assertEquals(redServant, b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.SERVANT).get());
        assertEquals(2, b.getPlayerByColor(Player.PlayerColor.RED).getNumberOfCards(Card.CardType.TERRITORY));
        //CHEAT MODE player RED-----------------------------------------------------------------------------------------
        b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.MILITARYPOINTS).add(3);
        redMilitaryPts += 3;
        //re-try-------------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(33));//RED fm neutral in slot1
        } catch (ActionAbortedException ae) {
            exception = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, exception);
        assertEquals(3, fm.owner.getNumberOfCards(Card.CardType.TERRITORY));
        //-1 servant, effect: +1 coin; -3 coinTax
        redServant -= 1;
        redCoin -= 2;
        //Resources Test
        assertEquals(redServant, fm.owner.getResource(ResourceType.SERVANT).get());
        assertEquals(redCoin, fm.owner.getResource(ResourceType.COIN).get());
        //end of 21st move---------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(nodeGhostMove("neutral")); // Blue, neutral ghost move
        } catch (ActionAbortedException ae){
            exception = true;
            ae.getMessage();
            ae.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, exception);
        assertEquals(true, b.getPlayerByColor(Player.PlayerColor.BLUE).getFamilyMemberFromColor("neutral").isUsed());
        //end of 23th move---------------------------------------------------------------------------------------------
        try {
            b.makeMove(nodeGhostMove("white")); // Red, white ghost move
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(3, b.getRound());
        //END OF THIRD ROUND-------------------------------------------------------------------------------------------
        for (Dice.DiceColor diceColor : Dice.DiceColor.values()){
            if (!"ghost".equalsIgnoreCase(diceColor.getDiceColorString())){
                try {
                    b.makeMove(nodeGhostMove(diceColor.getDiceColorString())); //blue
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    b.makeMove(nodeGhostMove(diceColor.getDiceColorString())); //red
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        //Clean-Up test------------------------------------------------------------------------------------------------
        //check if all the family members are unused
        blueFMUsed = checkFamilyMemberUsed(b.getPlayerByColor(Player.PlayerColor.BLUE).getFamilyMembers());
        assertEquals(false, blueFMUsed);
        redFMUsed = checkFamilyMemberUsed(b.getPlayerByColor(Player.PlayerColor.RED).getFamilyMembers());
        assertEquals(false, redFMUsed);
        assertEquals(3, b.getEra());
        assertEquals(4, b.getRound());
        //CHEAT MODE player Blue---------------------------------------------------------------------------------------
        b.getPlayerByColor(Player.PlayerColor.BLUE).getResource(ResourceType.COIN).add(14); blueCoin+=14;
        //END OF FOURTH ROUND------------------------------------------------------------------------------------------

        //VATICAN PHASE------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(41));//VATICAN CHOICE BLU
        } catch (Exception e) {
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        assertEquals(true, b.isVatican());

        exception = false;
        try {
            b.makeMove(mosse.get(41));//VATICAN CHOICE RED
        } catch (Exception e) {
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        assertEquals(false, b.isVatican());
        // END VATICAN PHASE------------------------------------------------------------------------------------------

        //START OF FIFTH ROUND----------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(34));//BLUE in slotID 8, black + 1 servant, +2 stone -> legal
        } catch (ActionAbortedException ae){
            exception = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, exception);
        try {
            fm = b.getPlayerByColor(Player.PlayerColor.BLUE).getFamilyMemberFromColor("black");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, fm.isUsed());
        assertEquals(1, fm.owner.getNumberOfCards(Card.CardType.CHARACTER));
        //-1 servant, +2 stone, -6 coins, +8 victorypoints
        blueServant-=1;  blueStone+=2;  blueCoin-=6;  blueVictoryPts+=8;
        //Resources Test
        assertEquals(blueServant, fm.owner.getResource(ResourceType.SERVANT).get());
        assertEquals(blueStone, fm.owner.getResource(ResourceType.STONE).get());
        assertEquals(blueCoin, fm.owner.getResource(ResourceType.COIN).get());
        assertEquals(blueVictoryPts, fm.owner.getResource(ResourceType.VICTORYPOINTS).get());
        //CHEAT MODE Player Red---------------------------------------------------------------------------------------
        b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.COIN).add(9); redCoin+=9;
        //end of 33th move---------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(35));//Red in slotID 7, fm black, +1 stone, -3 cointax -> legal
        } catch (ActionAbortedException ae){
            exception = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, exception);
        try {
            fm = b.getPlayerByColor(Player.PlayerColor.RED).getFamilyMemberFromColor("black");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, fm.isUsed());
        assertEquals(6, fm.owner.getNumberOfCards(Card.CardType.CHARACTER));
        //+1 stone, -10 coins, +12 victorypoints
        redStone+=1;  redCoin-=10; redVictoryPts+=12;
        //Resources Test
        assertEquals(redStone, fm.owner.getResource(ResourceType.STONE).get());
        assertEquals(redCoin, fm.owner.getResource(ResourceType.COIN).get());
        assertEquals(redVictoryPts, fm.owner.getResource(ResourceType.VICTORYPOINTS).get());
        //end of 34th move---------------------------------------------------------------------------------------------
        // teoricamente redvictory = 15
        exception = false;
        try {
            b.makeMove(mosse.get(36));//Blue in slotID 5, fm neutral, -1 servant, -3 cointax -> legal
        } catch (ActionAbortedException ae){
            exception = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, exception);
        try {
            fm = b.getPlayerByColor(Player.PlayerColor.BLUE).getFamilyMemberFromColor("neutral");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, fm.isUsed());
        assertEquals(2, fm.owner.getNumberOfCards(Card.CardType.CHARACTER));
        //-1 servant, -6 coins -3 cointax, +0 victorypoints
        blueServant-=1;  blueCoin-=9;
        //Resources Test
        assertEquals(blueServant, fm.owner.getResource(ResourceType.SERVANT).get());
        assertEquals(blueCoin, fm.owner.getResource(ResourceType.COIN).get());
        assertEquals(blueVictoryPts, fm.owner.getResource(ResourceType.VICTORYPOINTS).get());
        //end of 35th move---------------------------------------------------------------------------------------------
        try {
            b.makeMove(nodeGhostMove("neutral"));//Red, neutral ghost move
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            b.makeMove(nodeGhostMove("white"));//Blue, white ghost move
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            b.makeMove(nodeGhostMove("white"));//Red, white ghost move
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            b.makeMove(nodeGhostMove("orange"));//Blue, orange ghost move
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            b.makeMove(nodeGhostMove("orange"));//Red, orange ghost move
        } catch (Exception e) {
            e.printStackTrace();
        }
        //CHEAT MODE player Blue--------------------------------------------------------------------------------------
        b.getPlayerByColor(Player.PlayerColor.BLUE).getResource(ResourceType.COIN).add(12); blueCoin+=12;
        b.getPlayerByColor(Player.PlayerColor.BLUE).getResource(ResourceType.SERVANT).add(3); blueServant+=3;
        //END OF FIFTH ROUND-------------------------------------------------------------------------------------------
        assertEquals(5, b.getRound());
        //Clean-Up test------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(37));//Blue in slotID 5, fm neutral, -1 servant -> legal
        } catch (ActionAbortedException ae){
            exception = true;
            ae.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, exception);
        try {
            fm = b.getPlayerByColor(Player.PlayerColor.BLUE).getFamilyMemberFromColor("neutral");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, fm.isUsed());
        assertEquals(3, fm.owner.getNumberOfCards(Card.CardType.CHARACTER));
        //-1 servant, -4 coins, harvest: 1 wood, 1 stone, 1 servant, 2 faith  points
        blueCoin-=4; blueWooD+=1; blueStone+=1; blueFaithPts+=2;
        //Resources Test
        assertEquals(blueServant, fm.owner.getResource(ResourceType.SERVANT).get());
        assertEquals(blueCoin, fm.owner.getResource(ResourceType.COIN).get());
        assertEquals(blueStone, fm.owner.getResource(ResourceType.STONE).get());
        assertEquals(blueFaithPts, fm.owner.getResource(ResourceType.FAITHPOINTS).get());
        //CHEAT MODE Player Red---------------------------------------------------------------------------------------
        b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.COIN).add(8); redCoin+=8;
        b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.SERVANT).add(3); redServant+=3;
        //end of 41th move---------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(38));//Red fm orange in slot 6 -> exception già 6 carte character
        } catch (ActionAbortedException ae){
            exception = true;
            message = ae.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals("You already have 6 characters cards or the tower's Action Space is empty", message);
        assertEquals(true, exception);
        assertEquals(redCoin, b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.COIN).get());
        assertEquals(6, b.getPlayerByColor(Player.PlayerColor.RED).getNumberOfCards(Card.CardType.CHARACTER));
        //re-try-------------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(39));//RED retry fm orange in slotID 14, cost:3 servants, 4 coins -> legal
        } catch (ActionAbortedException ae){
            exception = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, exception);
        try {
            fm = b.getPlayerByColor(Player.PlayerColor.RED).getFamilyMemberFromColor("orange");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, fm.isUsed());
        //-3 servants, -4 coins, production: 1 military points, 2 coins
        redServant-=3;  redCoin-=2; redMilitaryPts+=1;
        assertEquals(3, fm.owner.getNumberOfCards(Card.CardType.VENTURE));
        //Resources Test
        assertEquals(redServant, fm.owner.getResource(ResourceType.SERVANT).get());
        assertEquals(redCoin, fm.owner.getResource(ResourceType.COIN).get());
        assertEquals(redMilitaryPts, fm.owner.getResource(ResourceType.MILITARYPOINTS).get());
        //end of 42th move---------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(40));//Blue fm black, in slotID 7, +1 stone -cointax -> legal
        } catch (ActionAbortedException ae){
            exception = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, exception);
        try {
            fm = b.getPlayerByColor(Player.PlayerColor.BLUE).getFamilyMemberFromColor("black");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, fm.isUsed());
        assertEquals(4, fm.owner.getNumberOfCards(Card.CardType.CHARACTER));
        //+1 stone, -5 coins -3 cointax, effect: 2 milpts -> 1 vicpts (5 totale)
        blueStone+=1; blueCoin-=8; blueVictoryPts+=5;
        //Resources Test
        assertEquals(blueStone, fm.owner.getResource(ResourceType.STONE).get());
        assertEquals(blueCoin, fm.owner.getResource(ResourceType.COIN).get());
        assertEquals(blueVictoryPts, fm.owner.getResource(ResourceType.VICTORYPOINTS).get());
        //CHEAT MODE Player Red---------------------------------------------------------------------------------------
        b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.SERVANT).add(1); redServant+=1;
        //end of 43th move---------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(43));//Red fm neutral + 1 servant in slotID 13, not enough military points -> exception
        } catch (ActionAbortedException ae){
            exception = true;
            message = ae.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, exception);
        assertEquals("You don't have enough Military Points", message);
        //re-try-------------------------------------------------------------------------------------------------------
        try {
            b.makeMove(nodeGhostMove("neutral"));//Red neutral ghost move
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            b.makeMove(nodeGhostMove("orange"));//Blue orange ghost move
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
        } catch (Exception e){
            e.printStackTrace();
        }

        //VATICAN PHASE------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(41));//VATICAN CHOICE BLU
        } catch (Exception e) {
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        assertEquals(true, b.isVatican());

        exception = false;
        try {
            b.makeMove(mosse.get(41));//VATICAN CHOICE RED
        } catch (Exception e) {
            exception = true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        assertEquals(false, b.isVatican());
        // END VATICAN PHASE-------------------------------------------------------------------------------------------
        //END OF THE GAME - FINAL CHECK--------------------------------------------------------------------------------
        /* to check:
        * -number of characters cards
        * -number of territories cards
        * -military points for first e second
        * -faith points
        * -(wood,stone,coins,servant)/5
        * -victory points */
        //BLUE check
        int finalVictoryPoints = blueVictoryPts;
        finalVictoryPoints+=10; //for char cards
        finalVictoryPoints+=5; //first for military points
        finalVictoryPoints+=7; //6 faith points
        finalVictoryPoints+=((blueWooD+blueStone+blueServant+blueCoin)/5);
        assertEquals(finalVictoryPoints, b.getPlayerByColor(Player.PlayerColor.BLUE).getResource(ResourceType.VICTORYPOINTS).get());

        //RED check
        finalVictoryPoints = redVictoryPts;
        finalVictoryPoints+=21; //for 6 char cards
        finalVictoryPoints+=1; //3 territory cards
        finalVictoryPoints+=2; //second for military points
        finalVictoryPoints+=11; //8 faith points
        finalVictoryPoints+=((redWood+redStone+redServant+redCoin)/5); //2
        assertEquals(finalVictoryPoints, b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.VICTORYPOINTS).get());

        //printResources(b.getPlayerByColor(Player.PlayerColor.RED));
        //printResources(b.getPlayerByColor(Player.PlayerColor.BLUE));
        //printStatus();

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