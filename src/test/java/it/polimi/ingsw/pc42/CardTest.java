package it.polimi.ingsw.pc42;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.pc42.DevelopmentCards.Card;
import it.polimi.ingsw.pc42.DevelopmentCards.iCard;

import static it.polimi.ingsw.pc42.CardParser.createCard;
import static org.junit.Assert.*;
import org.junit.Test;

import java.io.File;
import java.io.IOException;


public class CardTest {

    @Test
    public void cardCreationTest(){

        iCard card =new Card(1, "test1", Card.CardType.fromString("ventures"));
        iCard card2 =new Card(2, "test2", Card.CardType.fromString("buildings"));
        iCard card3 =new Card(3, "test3", Card.CardType.fromString("characters"));
        iCard card4 =new Card(1, "test4", Card.CardType.fromString("territories"));

        assertEquals(card.getCardType(), Card.CardType.VENTURE);
        assertEquals(card2.getCardType(), Card.CardType.BUILDING);
        assertEquals(card3.getCardType(), Card.CardType.CHARACTER);
        assertEquals(card4.getCardType(), Card.CardType.TERRITORY);

    }

    @Test(expected = IllegalArgumentException.class)
    public void cardCreationFailureTest(){
        iCard card = new Card(1,"test", Card.CardType.fromString("venture"));
    }

    @Test
    public void cardFromJSONTest(){
        ObjectMapper mapper = new ObjectMapper();
        Player player = new Player();
        try {
            File file = new File("src/res/test_card.json");
            JsonNode jsonNode = mapper.readTree(file);
            iCard card = createCard(jsonNode);

            assertEquals(card.getCardType(), Card.CardType.CHARACTER );
            assertEquals(card.getName(), "Preacher");
            assertEquals(card.getEra(), 1);
            //card.applyDrawEffect();

        } catch (IOException e){
            e.printStackTrace();
        }
    }



}
