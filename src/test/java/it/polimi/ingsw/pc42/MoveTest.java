package it.polimi.ingsw.pc42;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.iCard;
import it.polimi.ingsw.pc42.Control.ResourceType;
import it.polimi.ingsw.pc42.Model.Board;
import it.polimi.ingsw.pc42.Model.FamilyMember;
import it.polimi.ingsw.pc42.Model.Player;
import it.polimi.ingsw.pc42.Utilities.GameInitializer;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.Iterator;

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

        boolean exception= false;
        try {
            b.makeMove(mosse.get(0)); //Turno del giocatore rosso, gioca nel market e ottiene 5 monete
        } catch (Exception e) {
            exception=true; //questa parte di codice non dovrebbe venire eseguita
            e.printStackTrace();
        }
        assertEquals(false, exception);
        assertEquals(10 ,b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.COIN).get()); //le 5 monete iniziali più le 5 appena prese

        assertEquals(b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.SERVANT).get(), 2); //3 iniziali meno 1

        FamilyMember fm= null;
        try {
            fm = b.getPlayerByColor(Player.PlayerColor.RED).getFamilyMemberFromColor("neutral");
        } catch (Exception e) {

            e.printStackTrace();
        }
        assertEquals(true, fm.isUsed());

        //Fine prima mossa--------------------------------------------------------------------------------------------
        exception=false;
        try {
            b.makeMove(mosse.get(1)); //turno del giocatore blu, questa mossa non è legale quindi mi aspetto che avvenga un eccezione
        } catch (Exception e){
            exception=true;
            //e.printStackTrace();
        }
        assertEquals(true, exception);

        //fine seconda mossa-------------------------------------------------------------------------------------------
        exception=false;
        try{
            b.makeMove(mosse.get(2));
        } catch (Exception e){
            exception=true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        assertEquals(8, b.getPlayerByColor(Player.PlayerColor.BLUE).getResource(ResourceType.SERVANT).get());

        //--------
        exception=false;
        try{
            b.makeMove(mosse.get(3));

        } catch (Exception e){
            exception=true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        assertEquals(11,b.getPlayerByColor(Player.PlayerColor.RED).getResource(ResourceType.COIN).get());
        //-----------
        exception=false;
        try{
            b.makeMove(mosse.get(3));
        } catch (Exception e){
            exception=true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        assertEquals("blue", b.getPlayerArrayList().get(1).getColor().getPlayerColorString());
        //---------------
        exception=false;
        try{
            b.makeMove(mosse.get(3));
        } catch (Exception e){
            exception=true;
            e.printStackTrace();
        }
        assertEquals(true, exception);
        assertEquals("red", b.getPlayerArrayList().get(0).getColor().getPlayerColorString());
        //------------
        exception=false;
        try{
            b.makeMove(mosse.get(5));
        } catch (Exception e){
            exception=true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
        assertEquals(3, b.getCurrentPlayer().getResource(ResourceType.MILITARYPOINTS).get());
        assertEquals(13, b.getCurrentPlayer().getResource(ResourceType.COIN).get());
        //------------
        exception=false;
        try{
            b.makeMove(mosse.get(4));
        } catch (Exception e){
            exception=true;
            e.printStackTrace();
        }
        assertEquals(false, exception);
       assertEquals("blue", b.getPlayerArrayList().get(0).getColor().getPlayerColorString());
       assertEquals("red", b.getPlayerArrayList().get(1).getColor().getPlayerColorString());
    }

}
