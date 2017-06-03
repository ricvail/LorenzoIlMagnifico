package it.polimi.ingsw.pc42;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
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

        iCard c = null;
        try {
            isJsonValid(root);

            c = new Card(root.get("era").asInt(),
                root.get("name").asText(),
                Card.CardType.fromString(root.get("type").asText()),
                root);

            //decorate immediate effect
            if (root.has("immediateEffect")) {
                JsonNode immediateEffectNode = root.get("immediateEffect");
                c = immediateEffectIterator(immediateEffectNode, c);
            }

            if (root.has("permanentEffects")) {
                JsonNode immediateEffectNode = root.get("permanentEffects");
                //c = immediateEffectIterator(immediateEffectNode, c);
            }

            //decorate costs
            if (root.has("costs")) {
                ArrayNode costsNode = (ArrayNode) root.get("costs");
                c = costIterator(costsNode, c);
            }

        } catch (Exception e){
            System.out.println(root.get("name").asText());
            //e.printStackTrace();
        }
        return c;
    }

    static iCard applyResource(String s, int q, iCard c){
        ResourceType rt = ResourceType.fromString(s);
        c = new ResourceImmediateBonus(rt, q, c);
        return c;

    }

    private static iCard immediateEffectIterator(JsonNode jsonNode, iCard c) throws Exception {
        Iterator<String> it = jsonNode.fieldNames();
        while (it.hasNext()) {
            String key = it.next();
            try {
                c = applyResource(key, jsonNode.get(key).asInt(), c);
            } catch (IllegalArgumentException e){
                if (key.equalsIgnoreCase("privileges")){

                } else if (key.equalsIgnoreCase("card")){

                } else if (key.equalsIgnoreCase("foreach")){

                } else if (key.equalsIgnoreCase("harvest")){

                } else if (key.equalsIgnoreCase("production")){

                } else {
                    throw new Exception("Invalid immediate effect: "+ key);
                }
            }
        }
        return c;
    }

    private static iCard singleCostIterator(JsonNode jsonNode, iCard c) throws Exception {
        Iterator<String> it = jsonNode.fieldNames();
        while (it.hasNext()) {
            String key = it.next();
            try {
                c = applyResource(key, jsonNode.get(key).asInt()*-1, c);
            } catch (IllegalArgumentException e){
                if (key.equalsIgnoreCase("militaryPointsRequired")){

                } else if (key.equalsIgnoreCase("militaryPointsSubtracted")){

                } else {
                    throw new Exception("Invalid cost: "+ key);
                }
            }
        }
        return c;
    }

    private static iCard costIterator(JsonNode jsonNode, iCard c) throws Exception {
        if (jsonNode.size()>1){
            c = choiceIterator(jsonNode, c);
        } else {
            c = singleCostIterator(jsonNode.get(0), c);
        }
        return c;
    }



    private static iCard choiceIterator(JsonNode jsonNode, iCard c) throws Exception {
        ImmediateBonusChoice choiceDec = new ImmediateBonusChoice(c);
        for (int i = 0; i<jsonNode.size(); i++){
            choiceDec.addChoice();
            choiceDec.choices.set(i, singleCostIterator(jsonNode.get(i), choiceDec.choices.get(i)));
        }
        /*int choiceCounter = 0;
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
        } */
        return choiceDec;
    }

    private static void isJsonValid(JsonNode root) throws Exception{
        if (!(root.has("era")&&root.get("era").isInt()&&
            root.has("name")&&
            root.has("type")
            //&& root.has("id")&&root.get("id").isInt()
        )){
            throw new Exception("missing base field");
        }

        Card.CardType type = Card.CardType.fromString(root.get("type").asText());
        if (type== Card.CardType.TERRITORY || type== Card.CardType.BUILDING){
            if (!(root.has("activationCost")&&root.get("activationCost").isInt())){
                throw new Exception("missing activation cost");
            }
        }

        if (root.has("immediateEffect")&&
            !root.get("immediateEffect").isObject()) {
            throw new Exception("wrong type of immediateEffect");
        }

        if (root.has("permanentEffects")&&
                !root.get("permanentEffects").isArray()) {
            throw new Exception("wrong type of permanentEffects");
        }

        if (root.has("costs")&&
            !root.get("costs").isArray()) {
            throw new Exception("wrong type of costs");
        }
    }



    public static void main(String[] args) {

        ArrayList<iCard> cards = new ArrayList<>();


        ObjectMapper mapper = new ObjectMapper();
        //complete path in order to run the json
        JsonNode json = null;
        try {
            json = mapper.readTree(new File("src/res/developmentCards.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Iterator<JsonNode> jsonNodeIterator = json.get("developmentCards").elements();
        while (jsonNodeIterator.hasNext()){
            iCard c;
            c = createCard(jsonNodeIterator.next());
            cards.add(c);
        }

        System.out.print(
                cards
        );


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


    }
}

