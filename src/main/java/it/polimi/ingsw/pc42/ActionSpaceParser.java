package it.polimi.ingsw.pc42;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.ActionSpace.iActionSpace;
import it.polimi.ingsw.pc42.ActionSpace.ActionSpace;
import it.polimi.ingsw.pc42.ActionSpace.Area;
import it.polimi.ingsw.pc42.ActionSpace.ResourceImmediateBonus;

import java.util.Iterator;

public class ActionSpaceParser {
    public static iActionSpace actionSpace(JsonNode root){
        iActionSpace actionSpace=new ActionSpace(stringToArea(root.get("area").asText()), root.get("id").asInt());

        if (root.has("additionalCoinsTax")){
            int additionalCoinsTax = root.get("additionalCoinsTax").asInt();
        }

        if (root.has("actionValuePenaltyForSecondPlayer")){
            int actionValuePenaltyForSecondPlayer = root.get("actionValuePenaltyForSecondPlayer").asInt();
        }

        if (root.has("oneFamilyMemberPerPlayer")){
            Boolean oneFamilyMemberPerPlayer = root.get("oneFamilyMemberPerPlayer").asBoolean();
        }

        if (root.has("turnOrderModifier")){
            Boolean turnOrderModifier = root.get("turnOrderModifier").asBoolean();
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
        return null;
    }

    private static Area stringToArea(String a){
        for (Area area : Area.values()) {
            if (area.getAreaString().equals(a)) {
                return area;
            }
        }
        return null;
    }

    private static ResourceType stringToResourceType(String rt) {
        for (ResourceType resourceType : ResourceType.values()) {
            if (resourceType.getRTString().equals(rt)) {
                return resourceType;
            }
        }
        return null;
    }

    private static void immediateResourceEffectIterator(JsonNode jsonNode, iActionSpace iActionSpace){
        Iterator<String> it = jsonNode.fieldNames();
        while (it.hasNext()) {
            String key = it.next();
            iActionSpace = new ResourceImmediateBonus(stringToResourceType(key),
                    jsonNode.get(key).asInt(), iActionSpace);
        }
    }
}

