package it.polimi.ingsw.pc42;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.polimi.ingsw.pc42.DevelopmentCards.Card;
import it.polimi.ingsw.pc42.DevelopmentCards.ImmediateBonusChoice;
import it.polimi.ingsw.pc42.DevelopmentCards.ResourceImmediateBonus;
import it.polimi.ingsw.pc42.DevelopmentCards.iCard;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class CardParser {

    public static iCard createCard(JsonNode root){

        try {
            isJsonValid(root);
        } catch (Exception e){
            final String message = e.getMessage();
            System.out.println(message);
        }

        iCard c = new Card(root.get("era").asInt(),
                root.get("name").asText(),
                Card.CardType.fromString(root.get("type").asText()),
                root);

        //decorate immediate effect
        JsonNode immediateEffectNode = root.get("immediateEffect");
        c = decoIterator(immediateEffectNode, c);

        //decorate costs
        JsonNode costsNode = root.get("costs");
        c = decoIterator(costsNode, c);

        return c;
    }


    //TODO extra card handler
    private static iCard decoIterator(JsonNode jsonNode, iCard c){
        //check if has multiple effect
        if (jsonNode.isArray()) {
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
        } else {
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
        }
        return c;
    }

    private static void isJsonValid(JsonNode root) throws Exception{
        if (!(root.has("era")&&
                root.has("name")&&
                root.has("type"))){
            throw new Exception("missing base fieldname");
        } else if (!(root.get("immediateEffect").isObject())){
            throw new Exception("wrong type of immediateEffect");
        } else if (!(root.get("costs").isArray()
                    ||root.get("costs").isObject())){
            throw new Exception("wrong type of costs");
        }
    }



    public static void main(String[] args) {


        try {
            ObjectMapper mapper = new ObjectMapper();
            //complete path in order to run the json
            JsonNode json = mapper.readTree(new File("C:\\Users\\Paolo\\IdeaProjects\\LorenzoIlMagnifico\\src\\res\\prova_carta.json"));
            //iCard c;
            //c = createCard(json);
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

            System.out.println(json.get("new property").isArray());

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

