package it.polimi.ingsw.pc42.Utilities;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionSpace.ToDo.CardDecorator;
import it.polimi.ingsw.pc42.Control.ActionSpace.ToDo.privilegesActionSpaceDecorator;
import it.polimi.ingsw.pc42.Control.ActionSpace.*;
import it.polimi.ingsw.pc42.Control.ActionSpace.ToDo.ActionDecorator;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.Card;
import it.polimi.ingsw.pc42.Control.ResourceType;


import java.util.ArrayList;
import java.util.Iterator;

public class ActionSpaceParser {

    public static void actionSpace(JsonNode root, BoardProvider bp, ArrayList<iActionSpace> spaces) throws Exception {

        if (!isJsonValid(root)){
            throw new Exception("ActionSpace JSON is not valid");
        }

        Iterator<JsonNode> iterator=root.get("actionSpaces").elements();
        while (iterator.hasNext()){
            JsonNode actionSpaceJson= iterator.next();

            iActionSpace actionSpace= buildBaseActionSpace(root, actionSpaceJson, bp);

            if (actionSpaceJson.has("immediateResourceEffect")) {

                if (actionSpaceJson.get("immediateResourceEffect").has("effect")) {
                    String effect = actionSpaceJson.get("immediateResourceEffect").get("effect").asText();

                    try {
                        Card.CardType cardType = Card.CardType.fromString(effect);
                        actionSpace = new CardDecorator(cardType, actionSpace);
                    } catch (Exception e) {
                        if (effect.equalsIgnoreCase("harvest")) {
                            actionSpace = new ActionDecorator(ActionDecorator.ActionType.HARVEST, actionSpace);
                        } else if (effect.equalsIgnoreCase("production")) {
                            actionSpace = new ActionDecorator(ActionDecorator.ActionType.PRODUCTION, actionSpace);
                        } else {
                            throw new Exception("Invalid effect detected: "+effect);
                        }
                    }

                } // End of "effect" part

                Iterator<String> it = actionSpaceJson.get("immediateResourceEffect").fieldNames();
                while (it.hasNext()) {
                    String key = it.next();
                    try {
                        ResourceType rt = ResourceType.fromString(key);
                        int q = actionSpaceJson.get("immediateResourceEffect").get(key).asInt();
                        actionSpace = new ResourceImmediateBonus(rt, q, actionSpace);
                    } catch (IllegalArgumentException e) {
                        if (key.equalsIgnoreCase("privileges")) {
                            int q = actionSpaceJson.get("immediateResourceEffect").get(key).asInt();
                            actionSpace = new privilegesActionSpaceDecorator(q, actionSpace);
                        } else if (key.equalsIgnoreCase("effect")) {
                            //do nothing
                        } else {
                            throw new Exception("Invalid immediateResourceEffect detected: "+key);
                        }
                    }
                } //end of immediate resource effect
            }
            if (actionSpaceJson.has("turnOrderModifier")&&actionSpaceJson.get("turnOrderModifier").asBoolean()) {
                //Boolean turnOrderModifier = ;
                //b.setCouncilID(actionSpaceJson.get("id").asInt());
            }
            if (actionSpaceJson.has("singleFamilyMember")&&
                    actionSpaceJson.get("singleFamilyMember").asBoolean()){
                actionSpace=new singleFamilyMemberDecorator(actionSpace);
            }
            if (root.has("oneFamilyMemberPerPlayer")&&
                    root.get("oneFamilyMemberPerPlayer").asBoolean()){
                actionSpace=new oneFamilyMemberPerPlayer(actionSpace);
            }
            if (root.has("actionValuePenaltyForSecondPlayer")){
                int q = root.get("actionValuePenaltyForSecondPlayer").asInt();
                actionSpace=new ActionValuePenaltyForSecondPlayer(q, actionSpace);
            }
            if (root.has("additionalCoinsTax")){
                int q = root.get("additionalCoinsTax").asInt();
                actionSpace=new additionalCoinsTax(q, actionSpace);
            }
            spaces.add(actionSpace);
        }
    }

    public static int getCouncilID(JsonNode areaList) throws Exception {

        Iterator<JsonNode> roots = areaList.iterator();
        while (roots.hasNext()) {
            JsonNode root = roots.next();

            Iterator<JsonNode> iterator = root.get("actionSpaces").elements();
            while (iterator.hasNext()) {
                JsonNode actionSpaceJson = iterator.next();
                if (actionSpaceJson.has("turnOrderModifier") && actionSpaceJson.get("turnOrderModifier").asBoolean()) {
                    return actionSpaceJson.get("id").asInt();
                }
            }
        }
        throw new Exception("Council not found");
    }


    private static iActionSpace buildBaseActionSpace(JsonNode root, JsonNode actionSpaceJson, BoardProvider bp){
        Area area=Area.fromString(root.get("area").asText());
        int id=actionSpaceJson.get("id").asInt();
        int actionValue=actionSpaceJson.get("actionValue").asInt();
        return new ActionSpace(bp, area, id, actionValue);

    }

    private static boolean isJsonValid(JsonNode root){
        if (!(root.has("area")&&
            root.has("actionSpaces")&&
            root.get("actionSpaces").isArray())){
            return false;
        }
        Iterator<JsonNode> iterator=root.get("actionSpaces").elements();
        while (iterator.hasNext()){
            JsonNode actionSpace= iterator.next();
            if (!(actionSpace.has("id")&&
                actionSpace.has("actionValue")/*&&
                actionSpace.has("immediateResourceEffect")&&
                actionSpace.get("immediateResourceEffect").isObject()*/
            )){
                return false;
            }
        }
        return true;
    }

    public static JsonNode getActionSpaceJSONByArea(JsonNode jsonNode, String area)  throws Exception {
        Iterator<JsonNode> nodeIterator = jsonNode.elements();

        while (nodeIterator.hasNext()) {
            JsonNode arrNode = nodeIterator.next();
            if (arrNode.has("area") && area.equalsIgnoreCase(arrNode.get("area").asText())) {
                return arrNode;
            }
        }
        throw new Exception("invalid area string:" + area);
    }
}

