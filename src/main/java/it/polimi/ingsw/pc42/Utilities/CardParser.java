package it.polimi.ingsw.pc42.Utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import it.polimi.ingsw.pc42.Control.ActionSpace.Area;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.*;
import it.polimi.ingsw.pc42.Control.ResourceType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Iterator;

public class CardParser {

    public static iCard createCard(JsonNode root, BoardProvider bp){

        iCard c = null;
        try {
            isJsonValid(root);

            c = new Card(root.get("era").asInt(),
                root.get("name").asText(),
                Card.CardType.fromString(root.get("type").asText()),
                root, bp);

            //decorate immediate effect
            if (root.has("immediateEffect")) {
                JsonNode immediateEffectNode = root.get("immediateEffect");
                c = immediateEffectIterator(immediateEffectNode, c);
            }

            if (root.has("permanentEffects")) {
                JsonNode permanentEffectNode = root.get("permanentEffects");
                //c = permanentEffectIterator(permanentEffectNode, c);
            }

            //decorate costs
            if (root.has("costs")) {
                ArrayNode costsNode = (ArrayNode) root.get("costs");
                c = costIterator(costsNode, c);
            }

        } catch (Exception e){
            System.out.println(root.get("name").asText());
            e.printStackTrace();
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
                    if (jsonNode.get(key).isInt()){
                        c= new PrivilegeImmediateBonus (jsonNode.get(key).asInt(), c);
                    } else{
                        throw new Exception("Invalid privileges field");
                    }
                } else if (key.equalsIgnoreCase("card")){
                    c = applyExtraCardBonus(c, jsonNode.get("card"));

                } else if (key.equalsIgnoreCase("foreach")){
                    c = applyForeachImmediate(jsonNode.get("foreach"), c);
                } else {
                    throw new Exception("Invalid immediate effect: "+ key);
                }
            }
        }
        return c;
    }

    private static iCard applyExtraCardBonus(iCard c, JsonNode node) throws Exception {
        if (!(node.has("value")&&node.has("type"))){
            throw new Exception("missing value or type");
        }
        ArrayList<Area> areas = new ArrayList<>();
        ArrayList<ExtraCard.bonus> bonuses = new ArrayList<>();
        int value = 0;
        Iterator<String> keys = node.fieldNames();
        while ((keys.hasNext())){
            String key = keys.next();
            try {
                ResourceType rt = ResourceType.fromString(key);
                bonuses.add(new ExtraCard.bonus(rt, node.get(key).asInt()));
            } catch (Exception e){
                if ("value".equalsIgnoreCase(key)){
                    value=node.get(key).asInt();
                } else if ("type".equalsIgnoreCase(key)){
                    String type = node.get(key).asText();
                    try {
                        Area a = Area.fromString(type);
                        areas.add(a);
                    } catch (Exception ex) {
                        if ("all".equalsIgnoreCase(type)) {
                            areas.add(Area.TERRITORY);
                            areas.add(Area.BUILDING);
                            areas.add(Area.CHARACTER);
                            areas.add(Area.VENTURE);
                        } else {
                            throw new Exception("Not a valid card type: " + type);
                        }
                    }
                }
            }
        }
        return new ExtraCard(c, areas, bonuses, value);
    }

    private static iCard applyForeachImmediate(JsonNode jsonNode, iCard c) throws Exception{
        ResourceType obtained = ResourceType.fromString(jsonNode.get("left").asText());
        try {
            ResourceType toBeCounted = ResourceType.fromString(jsonNode.get("right").asText());
            return new ForeachImmediate(c, obtained, jsonNode.get("ratio").asInt(), toBeCounted);
        } catch (Exception e){
            Card.CardType toBeCounted = Card.CardType.fromString(jsonNode.get("right").asText());
            return new ForeachImmediate(c, obtained, jsonNode.get("ratio").asInt(), toBeCounted);
        }
    }

    private static iCard singleCostIterator(JsonNode jsonNode, iCard c) throws Exception {
        Iterator<String> it = jsonNode.fieldNames();
        while (it.hasNext()) {
            String key = it.next();
            try {
                c = applyResource(key, jsonNode.get(key).asInt()*-1, c);
            } catch (IllegalArgumentException e){
                if (key.equalsIgnoreCase("militaryPointsRequired")){
                    if (jsonNode.get(key).isInt()&&
                            jsonNode.has("militaryPointsSubtracted")&&
                            jsonNode.get("militaryPointsSubtracted").isInt()){
                        c=new militaryCost(c, jsonNode.get("militaryPointsRequired").asInt(),
                                jsonNode.get("militaryPointsSubtracted").asInt());
                    } else {
                        throw new Exception("Invalid military cost");
                    }
                } else if (key.equalsIgnoreCase("militaryPointsSubtracted")){
                    //ignore, already added above
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
}

