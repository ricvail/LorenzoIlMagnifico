package it.polimi.ingsw.pc42.Utilities;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Model.Board;
import it.polimi.ingsw.pc42.Control.ActionSpace.*;
import it.polimi.ingsw.pc42.Control.ActionSpace.ToDo.ActionDecorator;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.Card;
import it.polimi.ingsw.pc42.Control.ResourceType;


import java.util.Iterator;

public class ActionSpaceParser {

    public static void actionSpace(JsonNode root, Board b) throws Exception {

        if (!isJsonValid(root)){
            throw new Exception("ActionSpace JSON is not valid");
        }

        Iterator<JsonNode> iterator=root.get("actionSpaces").elements();
        while (iterator.hasNext()){
            JsonNode actionSpaceJson= iterator.next();

            iActionSpace actionSpace= buildBaseActionSpace(root, actionSpaceJson, b);

            if (actionSpaceJson.has("immediateResourceEffect")) {

                if (actionSpaceJson.get("immediateResourceEffect").has("effect")) {
                    String effect = actionSpaceJson.get("immediateResourceEffect").get("effect").asText();

                    try {
                        Card.CardType cardType = Card.CardType.fromString(effect);
                        actionSpace = new CardDecorator(cardType, b, actionSpace);
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
                b.setCouncilID(actionSpaceJson.get("id").asInt());
            }
            if (actionSpaceJson.has("singleFamilyMember")&&
                    actionSpaceJson.get("singleFamilyMember").asBoolean()){
                actionSpace=new singleFamilyMemberDecorator(actionSpace);
            }
            if (root.has("oneFamilyMemberPerPlayer")&&
                    root.get("oneFamilyMemberPerPlayer").asBoolean()){
                actionSpace=new oneFamilyMemberPerPlayer(actionSpace, b);
            }
            if (root.has("actionValuePenaltyForSecondPlayer")){
                int q = root.get("actionValuePenaltyForSecondPlayer").asInt();
                actionSpace=new ActionValuePenaltyForSecondPlayer(q, actionSpace);
            }
            if (root.has("additionalCoinsTax")){
                int q = root.get("additionalCoinsTax").asInt();
                actionSpace=new additionalCoinsTax(q, actionSpace);
            }
            b.getActionSpaces().add(actionSpace);
        }





        /*
        if (root.has("additionalCoinsTax")){
            int additionalCoinsTax = root.get("additionalCoinsTax").asInt();
        }

        if (root.has("actionValuePenaltyForSecondPlayer")){
            int actionValuePenaltyForSecondPlayer = root.get("actionValuePenaltyForSecondPlayer").asInt();
        }

        if (root.has("oneFamilyMemberPerPlayer")){
            Boolean oneFamilyMemberPerPlayer = root.get("oneFamilyMemberPerPlayer").asBoolean();
        }

        }

        JsonNode actionSpaceNode = root.path("actionSpaces");
        for (JsonNode node:actionSpaceNode) {
            int minPlayers = node.path("minPlayers").asInt();
            int actionValue = node.path("actionValue").asInt();
            boolean singleFamilyMember = node.path("singleFamilyMember").asBoolean();
            if (root.has("immediateResourceEffect")) {
                JsonNode jsonNode = root.get("immediateResourceEffect");
                immediateResourceEffectIterator(jsonNode, actionSpace);
            }
        }
        return null;*/
    }


    private static iActionSpace buildBaseActionSpace(JsonNode root, JsonNode actionSpaceJson, Board b){
        Area area=Area.fromString(root.get("area").asText());
        int id=actionSpaceJson.get("id").asInt();
        int actionValue=actionSpaceJson.get("actionValue").asInt();
        return new ActionSpace(b, area, id, actionValue);

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

