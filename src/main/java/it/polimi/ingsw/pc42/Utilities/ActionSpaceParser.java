package it.polimi.ingsw.pc42.Utilities;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionSpace.CardDecorator;
import it.polimi.ingsw.pc42.Control.ActionSpace.privilegesActionSpaceDecorator;
import it.polimi.ingsw.pc42.Control.ActionSpace.*;
import it.polimi.ingsw.pc42.Control.ActionSpace.ActionDecorator;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.Card;
import it.polimi.ingsw.pc42.Control.ResourceType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.ArrayList;
import java.util.Iterator;

public class ActionSpaceParser {
    private static Logger logger= LogManager.getLogger();

    /**
     *Factory method that adds the action space, tied to a board, to the spaces list, delegating the decoration and
     * loading the needed data from a JSON file.
     *
     * @param root is the highest node of a single action space object in the JSON file
     * @param bp  is a wrapper for a board object
     * @param spaces list of objects that implement the action space interface
     * @throws Exception if the JSON object for the action space, or the effect specification, is not valid
     */
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
                        logger.info(e);
                        if ("harvest".equalsIgnoreCase(effect)) {
                            actionSpace = new ActionDecorator(ActionDecorator.ActionType.HARVEST, actionSpace);
                        } else if ("production".equalsIgnoreCase(effect)) {
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
                        logger.info(e);
                        if ("privileges".equalsIgnoreCase(key)) {
                            int q = actionSpaceJson.get("immediateResourceEffect").get(key).asInt();
                            actionSpace = new privilegesActionSpaceDecorator(q, actionSpace);
                        } else if ("effect".equalsIgnoreCase(key)) {

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

    /**
     * Iterates through the JSON file of the action spaces until it finds a set council, otherwise throws an exception.
     *
     * @param areaList is the highest node of a single action space object in the JSON file, that contains info for area ...
     * @return the index of the council, if it exist
     * @throws Exception if it doesn't find any council, after reaching the end of the list of action spaces
     */
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

    /**
     *  Checks in the JSON file for the base fields of the action space and it initializes one.
     *
     * @param root is the highest node of a single action space object in the JSON file, that contains info for area ...
     * @param actionSpaceJson is the sub-node that contains specific data like ID, min value ...
     * @param bp is a wrapper for a board object
     * @return an object that implements the action space interface, with the base values
     */
    private static iActionSpace buildBaseActionSpace(JsonNode root, JsonNode actionSpaceJson, BoardProvider bp){
        Area area=Area.fromString(root.get("area").asText());
        int id=actionSpaceJson.get("id").asInt();
        int actionValue=actionSpaceJson.get("actionValue").asInt();
        int minPlayers=actionSpaceJson.get("minPlayers").asInt();
        return new ActionSpace(bp, area, id, actionValue, minPlayers);

    }

    /**
     * Does a basic check on the JSON file fields needed to initialize the action space, returns the success (or
     * not) of the control as <code>boolean</code>.
     *
     * @param root is the highest node of a single action space object in the JSON file
     * @return <code>false</code> if a basic field is missing
     */
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
                actionSpace.has("actionValue"))){
                return false;
            }
        }
        return true;
    }
}

