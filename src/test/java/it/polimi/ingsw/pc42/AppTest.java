package it.polimi.ingsw.pc42;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.polimi.ingsw.pc42.DevelopmentCards.Card;
import it.polimi.ingsw.pc42.DevelopmentCards.ImmediateBonusChoice;
import it.polimi.ingsw.pc42.DevelopmentCards.ResourceImmediateBonus;
import it.polimi.ingsw.pc42.DevelopmentCards.iCard;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.logging.Logger;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        App a = new App();
        String [] s = {"lol", "asd"};
        App.main(s);
        assertEquals(a.asd(), 3);
    }

    public void testCard(){
        iCard c= new Card(2, "Boh", Card.CardType.BUILDING);
        c= new ResourceImmediateBonus(ResourceType.STONE, 3, c);
        c= new ResourceImmediateBonus(ResourceType.SERVANT, 5, c);

        Player p =new Player();
        p.getResource(ResourceType.SERVANT).get();
        assertEquals (p.getResource(ResourceType.SERVANT).get(), 0);
        assertEquals (p.getResource(ResourceType.STONE).get(), 0);
        c.applyDrawEffect(p);
        assertEquals (p.getResource(ResourceType.SERVANT).get(), 5);
        assertEquals (p.getResource(ResourceType.STONE).get(), 3);
    }


    public void testChoice(){
        iCard c= new Card(2, "Boh", Card.CardType.BUILDING);
        ImmediateBonusChoice choiceDec = new ImmediateBonusChoice(c);
        choiceDec.addChoice();
        c=new ResourceImmediateBonus(ResourceType.COIN, 3, choiceDec.choices.get(0));
        choiceDec.choices.set(0, c);

    }

    public void testJSON(){
        ObjectMapper mapper=new ObjectMapper();
        try {
            JsonNode json = mapper.readTree("res/prova_carta.json");
            assertEquals(json.get("era").asInt(), 2);
            assertEquals(json.get("costs"). get(1).get("coins").asInt(), 4);
        } catch (Exception e) {

        }
    }
}
