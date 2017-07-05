package it.polimi.ingsw.pc42.View;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.ExtraCard;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by diego on 04/07/2017.
 */
public class OutputStringGenerator {

    public static String ArrayToString(ArrayList<String> a){
        String s= "";
        for (String e: a){
            s+=e;
        }
        return s;
    }

    public static ArrayList<String> generateOutputStringOf_B(JsonNode board){
        ArrayList<String> out = new ArrayList<>();

        Iterator<JsonNode> areas= board.get("spaces").elements();
        while (areas.hasNext()){
            JsonNode area = areas.next();
            out.add(area.get("area").asText());
            Iterator<JsonNode> spaces = area.get("actionSpaces").elements();
            while (spaces.hasNext()){
                JsonNode space = spaces.next();
                out.add("\tID: "+ space.get("id").asInt()+", Action Value: "+ space.get("actionValue").asInt());
            }
        }
        return out;
    }

    public static ArrayList<String>  generateOutputStringOf_A(JsonNode board, String inputArea) throws ActionAbortedException {
        ArrayList<String> out = new ArrayList<>();

        Iterator<JsonNode> areas= board.get("spaces").elements();
        while (areas.hasNext()){
            JsonNode area = areas.next();
            if (area.get("area").asText().equalsIgnoreCase(inputArea)) {
                Iterator<JsonNode> spaces = area.get("actionSpaces").elements();
                while (spaces.hasNext()){
                    JsonNode space = spaces.next();
                    out.add("\nID: "+ space.get("id").asInt()+"\tAction value: "+space.get("actionValue").asInt());
                    if (space.get("familyMembers").size()>0){
                        out.add("\tNumber of family members: "+space.get("familyMembers").size());
                    }
                    Iterator<JsonNode> immediateEffects = space.get("immediateResourceEffect").elements();
                    if (space.has("immediateResourceEffect")){
                        JsonNode immediateEffect = space.get("immediateResourceEffect");
                        ArrayList<String> effects =parseResources(immediateEffect);
                        if (effects.size()>0) {
                            out.add("\n\tImmediate bonus effect: ");
                            out.addAll(effects);
                        }
                    }
                    if (space.has("card")){
                        JsonNode card = space.get("card");
                        if (card.asText().equalsIgnoreCase("none")){
                            out.add("\n\tCard: None");
                        } else {
                            out.add("\n\tCard: " + card.get("name").asText() + "\n\t\t" + "Type: " + card.get("type").asText());
                            if (card.has("activationCost")) {
                                out.add("\n\t\t" + "Activation cost: " + card.get("activationCost").asText());
                            }
                            if (card.has("costs")) {
                                if (card.get("costs").size() == 1) {
                                    JsonNode cost = card.get("costs").get(0);
                                    out.add("\n\t\tCard cost: ");
                                    out.addAll(parseResources(cost));
                                } else {
                                    Iterator<JsonNode> costs = card.get("costs").elements();
                                    while (costs.hasNext()) {
                                        out.add("\n\t\tPayment option: ");
                                        JsonNode costchoice = costs.next();
                                        out.addAll(parseResources(costchoice));
                                    }
                                }
                            }
                            if (card.has("immediateEffect")) {
                                JsonNode immediateEffect = card.get("immediateEffect");
                                out.add("\n\t\tImmediate card's effect: ");
                                out.addAll(parseResources(immediateEffect));
                            }
                        }
                    }/*
                    Iterator<JsonNode> cards = space.get("card").elements();
                    while (cards.hasNext()){
                        JsonNode card = cards.next();
                        out.add("Card: " + card.get("name").asText()+ "\n\t" +"Type: "+ card.get("type").asText()+
                                "\n\t" + "Activation cost: "+ card.get("activationCost").asText());
                        if (card.get("costs").size()==1){
                            JsonNode cost = card.get("costs").get(0);
                            out.addAll(parseResources(cost));
                        } else {
                            out.add("Payment options: ");
                            Iterator<JsonNode> costs = card.get("costs").elements();
                            while (costs.hasNext()){
                                JsonNode costchoice = costs.next();
                                out.addAll(parseResources(costchoice));
                            }
                        }
                        JsonNode immediateEffect = card.get("immediateEffect");
                        out.addAll(parseResources(immediateEffect));
                    }*/
                    out.add("\n");
                }
                return out;
            }
        }throw new ActionAbortedException(false, "No such area");
    }

    public static ArrayList<String> parseResources(JsonNode node){
        ArrayList<String> out = new ArrayList<>();
        Iterator<String> fields = node.fieldNames();
        while (fields.hasNext()){
            String field = fields.next();
            try {
                boolean plur = node.get(field).asInt()>1;
                out.add(getResourceName(field, plur)+": "+ node.get(field).asInt()+ "\t");
            } catch (Exception e) {
                if ("card".equalsIgnoreCase(field)){
                    int value=node.get(field).get("value").asInt();
                    String type=node.get(field).get("type").asText();
                    out.add("\n\t\t\tMake a move with: "+"\tValue: "+ value + "\tin: "+type);
                    if (node.get(field).has("wood")||node.get(field).has("stone")||
                            node.get(field).has("servants")||node.get(field).has("coins")) {
                        out.add("\n\t\t\tPrice discount: ");
                        out.addAll(parseResources(node.get(field)));
                    }
                }
                if ("foreach".equalsIgnoreCase(field)){
                    String left=node.get("foreach").get("left").asText();
                    String right= node.get("foreach").get("right").asText();
                    float ratio= (float) node.get("foreach").get("ratio").asDouble();
                    try {
                        if (ratio==0.5){
                            out.add("Earn 1 " + getResourceName(right, false)+" for every 2 "+ getResourceName(left, true));
                        }
                        if (ratio==1){
                            out.add("Earn 1 " + getResourceName(right, false) + " for each "+ getResourceName(left, false));
                        }
                        if (ratio==2){
                            out.add("Earn 2 " + getResourceName(right, true) + " for each "+ getResourceName(left, false));
                        }
                    } catch (Exception x){
                        System.out.println("invalid input");
                    }
                }
            }
        }
        return out;
    }

    public static String getResourceNameIgnoringCards(String field, boolean plural) throws Exception {

        if ("stone".equalsIgnoreCase(field)||"stones".equalsIgnoreCase(field)){
            return "Stone"+ (plural?"s":"");
        } else if ("wood".equalsIgnoreCase(field)){
            return "Wood";
        } else if ("servants".equalsIgnoreCase(field)){
            return "Servant"+ (plural?"s":"");
        } else if ("coins".equalsIgnoreCase(field)){
            return "Coin"+ (plural?"s":"");
        } else if ("militaryPointsRequired".equalsIgnoreCase(field)){
            return "Military points required";
        } else if ("militaryPointsSubtracted".equalsIgnoreCase(field)){
            return "Military points subtracted";
        } else if ("militaryPoints".equalsIgnoreCase(field)){
            return "Military point"+ (plural?"s":"");
        } else if ("faithPoints".equalsIgnoreCase(field)){
            return "Faith point"+ (plural?"s":"");
        } else if ("victoryPoints".equalsIgnoreCase(field)){
            return "Victory point"+ (plural?"s":"");
        } else if ("privileges".equalsIgnoreCase(field)){
            return "Privilege"+ (plural?"s":"");
        }else if ("harvest".equalsIgnoreCase(field)){
            return "Harvest";
        }else if ("production".equalsIgnoreCase(field)){
            return "Production";
        } else throw new Exception();
    }


        public static String getResourceName(String field, boolean plural) throws Exception {
        try {
            return getResourceNameIgnoringCards(field, plural);
        }catch (Exception e) {
            if ("territories".equalsIgnoreCase(field)) {
                return (plural ? "Territories" : "Territory");
            } else if ("buildings".equalsIgnoreCase(field)) {
                return "Building" + (plural ? "s" : "");
            } else if ("ventures".equalsIgnoreCase(field)) {
                return "Venture" + (plural ? "s" : "");
            } else if ("characters".equalsIgnoreCase(field)) {
                return "Character" + (plural ? "s" : "");
            }
            throw new Exception();
        }
    }

}
