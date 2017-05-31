package it.polimi.ingsw.pc42;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.polimi.ingsw.pc42.DevelopmentCards.Card;
import it.polimi.ingsw.pc42.DevelopmentCards.ImmediateBonusChoice;
import it.polimi.ingsw.pc42.DevelopmentCards.ResourceImmediateBonus;
import it.polimi.ingsw.pc42.DevelopmentCards.iCard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class CardParser {

    public static iCard createCard(JsonNode root){

        try {
            isJsonValid(root);
        } catch (Exception e){
            e.printStackTrace();

            final String message = e.getMessage();
            System.out.println(message);
            System.out.println(root);
        }

        iCard c = new Card(root.get("era").asInt(),
                root.get("name").asText(),
                Card.CardType.fromString(root.get("type").asText()),
                root);

        //decorate immediate effect
        if (root.has("immediateEffect")) {
            JsonNode immediateEffectNode = root.get("immediateEffect");
            c = decoEffectIterator(immediateEffectNode, c);
        }

        //decorate costs
        if (root.has("costs")) {
            ArrayNode costsNode = (ArrayNode) root.get("costs");
            c = decoCostIterator(costsNode, c);
        }

        return c;
    }

    private static iCard decoEffectIterator(JsonNode jsonNode, iCard c){
        if (jsonNode.isArray()){
            //permanent effect
        } else {
            c = resourceIterator(jsonNode, c);
        }
        return c;
    }

    private static iCard decoCostIterator(JsonNode jsonNode, iCard c){
        if (jsonNode.size()>1){
            c = choiceIterator(jsonNode, c);
        } else {
            c = resourceIterator(jsonNode, c);
        }
        return c;
    }


    private static iCard resourceIterator(JsonNode jsonNode, iCard c){
        Iterator<String> it = jsonNode.fieldNames();
        while (it.hasNext()) {
            String key = it.next();
            try {
                ResourceType rt = ResourceType.fromString(key);
                c = new ResourceImmediateBonus(rt, jsonNode.get(key).asInt(), c);
            } catch (IllegalArgumentException e){
                //something wrong with resource type
            }
        }
        return c;
    }

    private static iCard choiceIterator(JsonNode jsonNode, iCard c){
        ImmediateBonusChoice choiceDec = new ImmediateBonusChoice(c);
        int choiceCounter = 0;
        for (JsonNode arrayNode : jsonNode) {
            choiceDec.addChoice();
            Iterator<String> it = arrayNode.fieldNames();
            while (it.hasNext()) {
                String key = it.next();
                try {
                    ResourceType rt = ResourceType.fromString(key);
                    c = new ResourceImmediateBonus(rt,
                            arrayNode.get(key).asInt(),
                            choiceDec.choices.get(choiceCounter));
                    choiceDec.choices.set(choiceCounter, c);
                } catch (IllegalArgumentException e){
                    //something wrong with resource type
                }
            }
            choiceCounter++;
        }
        return c;
    }

    private static void isJsonValid(JsonNode root) throws Exception{
        if (!(root.has("era")&&
                root.has("name")&&
                root.has("type"))){
            throw new Exception("missing base fieldname");
        } else if (root.has("immediateEffect")){
            if (!(root.get("immediateEffect").isObject())) {
                throw new Exception("wrong type of immediateEffect");
            }
        } else if (root.has("costs")) {
            if (!(root.get("costs").isArray())) {
                throw new Exception("wrong type of costs");
            }
        }
    }



    public static void main(String[] args) {

        ArrayList<iCard> cards = new ArrayList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            //complete path in order to run the json
            JsonNode json = mapper.readTree(new File("src/res/developmentCards.json"));

            Iterator<JsonNode> jsonNodeIterator = json.get("developmentCards").elements();
            while (jsonNodeIterator.hasNext()){
                iCard c;
                c = createCard(jsonNodeIterator.next());
                cards.add(c);
            }


            /*iCard c;
            c = createCard(json);
            System.out.println(json.isObject());
            ObjectNode obj = (ObjectNode) json;
            JsonNode j = mapper.readTree("[1,2,3]");
            obj.set("new property", j);
            json = obj;

            Iterator<String> it = json.fieldNames();
            while(it.hasNext()){
                String key = it.next();
                System.out.println(key);
            }
            System.out.println(json.get("new property").isArray()); */

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

