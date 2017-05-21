package it.polimi.ingsw.pc42;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.pc42.DevelopmentCards.Card;
import it.polimi.ingsw.pc42.DevelopmentCards.ImmediateBonusChoice;
import it.polimi.ingsw.pc42.DevelopmentCards.ResourceImmediateBonus;
import it.polimi.ingsw.pc42.DevelopmentCards.iCard;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class CardParser {

    public static iCard createCard(JsonNode root){

        iCard c = new Card(root.get("era").asInt(),
            root.get("name").asText(),
            stringToCardType(root.get("type").asText()));

        if (root.has("immediateEffect")) {
            JsonNode jsonNode = root.get("immediateEffect");
            magicIterator(jsonNode, c);
        }

        if (root.has("costs")) {
            JsonNode jsonNode = root.get("costs");
            magicIterator(jsonNode, c);
        }
        return c;
    }

// needed the  match between json and enum strings

    private static ResourceType stringToResourceType(String rt) {
        for (ResourceType resourceType : ResourceType.values()) {
            if (resourceType.getRTString().equals(rt)) {
                return resourceType;
            }
        }
        return null;
    }

    private static Card.CardType stringToCardType(String ct) {
        for (Card.CardType cardType : Card.CardType.values()) {
            if (cardType.getCTString().equals(ct)) {
                return cardType;
            }
        }
        return null;
    }

    private static void magicIterator(JsonNode jsonNode, iCard c){
        if (jsonNode.isArray()) {
            ImmediateBonusChoice choiceDec = new ImmediateBonusChoice(c);
            int choiceCounter = 0;
            for (JsonNode arrayNode : jsonNode) {
                choiceDec.addChoice();
                Iterator<String> it = arrayNode.fieldNames();
                while (it.hasNext()) {
                    String key = it.next();
                    c = new ResourceImmediateBonus(stringToResourceType(key),
                            arrayNode.get(key).asInt(),
                            choiceDec.choices.get(choiceCounter));
                    choiceDec.choices.set(choiceCounter, c);
                }
                choiceCounter++;
            }
        } else {
            Iterator<String> it = jsonNode.fieldNames();
            while (it.hasNext()) {
                String key = it.next();
                c = new ResourceImmediateBonus(stringToResourceType(key),
                        jsonNode.get(key).asInt(), c);
            }
        }
    }



    public static void main(String[] args) {


        try {
            ObjectMapper mapper = new ObjectMapper();
            //complete path in order to run the json
            JsonNode json = mapper.readTree(new File("res\\prova_carta.json"));
            iCard c;
            c = createCard(json);

            /*Iterator<String> it = json.fieldNames();
            while (it.hasNext()) {
                String key = it.next();
                System.out.println("key:  " + key);
            }*/
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

