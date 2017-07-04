package it.polimi.ingsw.pc42;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Control.ResourceType;
import it.polimi.ingsw.pc42.Model.Board;
import it.polimi.ingsw.pc42.Model.FamilyMember;
import it.polimi.ingsw.pc42.Model.Player;
import it.polimi.ingsw.pc42.Utilities.GameInitializer;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Unit test for simple App.
 */

public class MoveTest
    extends TestCase
{
    public MoveTest(String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( MoveTest.class );
    }

    public void testMove() {
        JsonNode mosse = GameInitializer.readFile("src/res/mosse_per_moveTest.json").get("moves");

        Board b = GameInitializer.initBaseGame(false);
        /**
         * Le carte vengono pescate in ordine (cioè lo stesso ordine in cui sono scritte nel json), il mazzo non è mescolato
         *      (non so esattamente quale sia quell'ordine ma una volta che avremo scritto il metodo per ottenere lo stato della
         *      board potremo vederlo, e non essendo mescolato il mazzo le partite saranno sempre identiche, quindi possiamo
         *      scriverci i test)
         * L'ordine iniziale dei giocatori non è casuale, segue quello di prova_playerInit
         *      (quindi prima il rosso e poi il blu, ma nel caso modificatelo pure)
         *      (poi ovviamente se giocate nel consiglio l'ordine dei turni successivi viene modificato)
         * I dadi avranno sempre lo stesso valore: bianco = 1, arancione = 3, nero = 6
         * Se si prova ad eseguire una mossa illegale viene lanciata un'eccezione
         *      (quindi fate test anche sulle eccezioni scrivendo apposta mosse illegali)
         *      (tipo usare ID che non esistono, o provare a mettere familiari in spazi per cui non si hanno abbastanza risorse)
         * Il turno passa automaticamente al giocatore successivo (a parte quando viene lanciata un'eccezione, in quel caso
         * il turno rimane al giocatore corrente)
         *
         * Il vaticano per ora non esiste, ma il resto (avanzamento di era e fine del gioco) avviene automaticamente
         */
        String message = "";
        boolean exception= false;
        try {
            b.makeMove(mosse.get(0)); //Turno del giocatore rosso, gioca nel market e ottiene 5 monete
        } catch (ActionAbortedException ae){
            exception = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, exception);
        FamilyMember fm= null;
        try {
            fm = b.getPlayerByColor(Player.PlayerColor.RED).getFamilyMemberFromColor("neutral");
        } catch (Exception e) {

            e.printStackTrace();
        }
        assertEquals(true, fm.isUsed());
        //le 5 monete iniziali più le 5 appena prese
        assertEquals(10 ,fm.owner.getResource(ResourceType.COIN).get());
        //3 iniziali meno 1
        assertEquals(2, fm.owner.getResource(ResourceType.SERVANT).get());
        //end 1st move--------------------------------------------------------------------------------------------
        exception=false;
        try {
            b.makeMove(mosse.get(1)); //blue, fm neutral e servant 0 -> exception
        }catch (ActionAbortedException ae){
            exception = true;
            message = ae.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, exception);
        assertEquals("Family Member's Action Value lower than requirement", message);
        //re-try---------------------------------------------------------------------------------------------------
        exception=false;
        try{
            b.makeMove(mosse.get(2)); //gioca di nuovo il blu e prende 5 servants nel market
        } catch (ActionAbortedException ae){
            exception = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, exception);
        //3 +5 servants
        assertEquals(8, b.getPlayerByColor(Player.PlayerColor.BLUE).getResource(ResourceType.SERVANT).get());
        //end of 2nd move---------------------------------------------------------------------------------------------
        exception=false;
        try{
            b.makeMove(mosse.get(3)); // Red sul mercato, slotID 22 -> exception (2 players game mode)
        } catch (ActionAbortedException ae){
            exception = true;
            message = ae.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, exception);
        //Resources not added?
        assertEquals(0, b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.MILITARYPOINTS).get());
        assertEquals(10, b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.COIN).get());
        assertEquals("Family Member can't be place in this Area or this Action Space is not active in 2 players game-mode", message);
        //re-try-------------------------------------------------------------------------------------------------------
        exception=false;
        try{
            b.makeMove(mosse.get(4));//gioca il rosso e fm orange nel council, sceglie 2 servants e 1 coin
        } catch (ActionAbortedException ae){
            exception = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, exception);
        // +2 servants
        assertEquals(4, b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.SERVANT).get());
        //+1 coin
        assertEquals(11, b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.COIN).get());
        //end of 3rd move--------------------------------------------------------------------------------------------
        exception=false;
        try{
            b.makeMove(mosse.get(5)); //Blue fm white council, sceglie 1 faithpoint e 1 coin
        } catch (ActionAbortedException ae){
            exception = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, exception);
        //+1 coin
        assertEquals(7, b.getPlayerByColor(Player.PlayerColor.BLUE).getResource(ResourceType.COIN).get());
        assertEquals(1, b.getPlayerByColor(Player.PlayerColor.BLUE).getResource(ResourceType.FAITHPOINTS).get());
        //end of 4th move----------------------------------------------------------------------------------------------
        exception = false;
        try{
            b.makeMove(mosse.get(6)); //gioca il rosso slot 23 -> exception (2 players game mode)
        }  catch (ActionAbortedException ae){
            exception = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, exception);
        //Resources not added
        assertEquals(2, b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.WOOD).get());
        assertEquals(2, b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.STONE).get());
        assertEquals(0, b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.MILITARYPOINTS).get());
        //re-try-------------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(7));//RED, exception neutral is used
        }  catch (ActionAbortedException ae){
            exception = true;
            message = ae.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, exception);
        assertEquals("This Family Member is used", message);
        //Resources not added?
        assertEquals(0, b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.FAITHPOINTS).get());
        //re-try-------------------------------------------------------------------------------------------------------
        exception = false;
        try {
            b.makeMove(mosse.get(8));//RED fm white council e 1 faithpts e 1 coin
        }  catch (ActionAbortedException ae){
            exception = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, exception);
        //+1 coin e 1 faithpt
        assertEquals(12, b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.COIN).get());
        assertEquals(1, b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.FAITHPOINTS).get());
        //end of 5th move----------------------------------------------------------------------------------------------
        //undo test
        exception = false;
        try {
            b.makeMove(mosse.get(9));//Blue fm orange con servant in council, sceglie coins: +3, ma è solo checking
        }  catch (ActionAbortedException ae){
            exception = true;
            message = ae.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, exception);
        assertEquals("checking move", message);
        //Resources not added?
        assertEquals(7, b.getPlayerByColor(Player.PlayerColor.BLUE).getResource(ResourceType.COIN).get());
    }

}
