package it.polimi.ingsw.pc42.View;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Utilities.myException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by diego on 04/07/2017.
 */
public class OutputStringGenerator {
    private static Logger logger= LogManager.getLogger();

    /**
     * Converts an ArrayList of strings in a string.
     * @param a ArrayList of be converted
     * @return resulted string
     */
    public static String ArrayToString(ArrayList<String> a){
        String s= "";
        for (String e: a){
            s+=e;
        }
        return s;
    }
    //ciao paolo

    /**
     * Generates the list of all inputs, that could be sent by the user during the game, with their meaning.
     * @return the arrayList of the commands
     */
    public static ArrayList<String> generateMenuCommands(){
        ArrayList<String> out = new ArrayList<>();
        out.add("B: board status\nTT: territory tower description\nCT: character tower description\n" +
                "BT: building tower description\nVT: venture tower description\nC: council status\n" +
                "P: production status\nH: harvest status\nMK: market description\nRP: red player status\n" +
                "BP: blue player status\nYP: yellow player status\nGP: green player status\n");
        return out;
    }

    /**
     * Generate an ArrayList of strings about the value of dice, the turn order and the current status
     * of the board specifying if the action spaces are empty and, if they aren't, the attributes
     * of the family member placed there
     * @param board json of the board
     * @return  an arrayList with the description
     */
    public static ArrayList<String> generateOutputStringOf_B(JsonNode board){
        ArrayList<String> out = new ArrayList<>();
        Iterator<JsonNode> dice = board.get("dices").elements();
        out.add("DICE");
        while (dice.hasNext()){
            JsonNode die = dice.next();
            out.add("\n\t" + die.get("color").asText()+" dice has value " +die.get("value").asInt());
        }
        Iterator<JsonNode> playerOrder = board.get("players").elements();
        out.add(("\n\nTURN ORDER"));
        int counter=1;
        while (playerOrder.hasNext()){
            JsonNode turnOrder=playerOrder.next();
            out.add("\n\t" + counter +"°: "+ turnOrder.get("color").asText());
            counter++;
        }
        Iterator<JsonNode> areas= board.get("spaces").elements();
        while (areas.hasNext()){
            JsonNode area = areas.next();
            out.add("\n\n"+area.get("area").asText());
            Iterator<JsonNode> spaces = area.get("actionSpaces").elements();
            while (spaces.hasNext()){
                JsonNode space = spaces.next();
                out.add("\n\tID: "+ space.get("id").asInt()+"\n\tAction Value: "+ space.get("actionValue").asInt());
                if (space.get("familyMembers").size()>0){
                    Iterator<JsonNode> fms = space.get("familyMembers").elements();
                    while (fms.hasNext()){
                        JsonNode fm = fms.next();
                        out.add("\n\t\tFamily member color: "+ fm.get("color").asText()+
                                "\n\t\tFamily member owner: "+ fm.get("playerColor").asText());
                    }
                }
            }
        }
        out.add("\n");
        return out;
    }

    /**
     * Generates an arrayList of strings that describes the status of a specific area and of the actions spaces
     * place there. For each action spaces specifies all details and, if them contain a card, all the specifications
     * about that card.
     * @param board json of the board
     * @param inputArea area of which generate the description
     * @return an arrayList with the description
     * @throws ActionAbortedException
     */
    public static ArrayList<String>  generateOutputStringOf_A(JsonNode board, String inputArea) throws ActionAbortedException {
        ArrayList<String> out = new ArrayList<>();

        Iterator<JsonNode> areas= board.get("spaces").elements();
        while (areas.hasNext()){
            JsonNode area = areas.next();
            if (area.get("area").asText().equalsIgnoreCase(inputArea)) {
                Iterator<JsonNode> spaces = area.get("actionSpaces").elements();
                while (spaces.hasNext()) {
                    JsonNode space = spaces.next();
                    if (space.has("locked")&&space.get("locked").asBoolean()){
                        out.add("\nID: "+ space.get("id").asInt()+ "[LOCKED]\n");
                    }else {
                        out.add("\nID: " + space.get("id").asInt() + "\tAction value: " + space.get("actionValue").asInt());
                        if (space.get("familyMembers").size() > 0) {
                            out.add("\tNumber of family members: " + space.get("familyMembers").size());
                        }
                        if (space.has("immediateResourceEffect")) {
                            if (space.get("immediateResourceEffect").size() == 1 &&
                                    space.get("immediateResourceEffect").has("effect")) {
                            } else {
                                JsonNode immediateEffect = space.get("immediateResourceEffect");
                                ArrayList<String> effects = parseResources(immediateEffect);
                                if (effects.size() > 0) {
                                    out.add("\n\tImmediate bonus effect: ");
                                    out.addAll(effects);
                                }
                            }
                        }
                        if (space.has("card")) {
                            JsonNode card = space.get("card");
                            out.addAll(cardParser(card));
                        }
                    }
                }
                out.add("\n");
                return out;
            }
        }throw new ActionAbortedException(false, "No such area");
    }

    /**
     * Generate an ArrayList of strings that parse all immediate effects of the cards generating a description
     * about their functions and values.
     * @param node jsonNode of the card's effects
     * @return an arrayList with the description
     */
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
                    logger.info(e);
                }
                if ("foreach".equalsIgnoreCase(field)){
                    String left=node.get("foreach").get("left").asText();
                    String right= node.get("foreach").get("right").asText();
                    double ratio= node.get("foreach").get("ratio").asDouble();
                    try {
                        if (Math.abs(ratio -0.5) < 0.01){
                            out.add("Earn 1 " + getResourceName(left, false)+" for every 2 "+ getResourceName(right, true));
                        }
                        if (Math.abs(ratio -1.0) < 0.01){
                            out.add("Earn 1 " + getResourceName(left, false) + " for each "+ getResourceName(right, false));
                        }
                        if (Math.abs(ratio -2.0) < 0.01){
                            out.add("Earn 2 " + getResourceName(left, true) + " for each "+ getResourceName(right, false));
                        }
                    } catch (Exception x){
                        System.out.println("invalid input");
                        logger.info(x);
                    }
                }
                if ("addValueToDice".equalsIgnoreCase(field)){
                    int value=node.get(field).get("value").asInt();
                    String type=node.get(field).get("type").asText();
                    out.add("Add +" + value +" to the value of your family member when you put it in "+ type+" area.\n");
                    if (node.get(field).has("wood")||node.get(field).has("stone")||
                            node.get(field).has("coins")){
                        out.add("\t\t\tThen you earn a discount of: ");
                        out.addAll(parseResources(node.get(field)));
                    }
                }
                if ("disableImmediateBonus".equalsIgnoreCase(field)){
                    out.add("Makes lose all immediate effects of the acton spaces in towers during all game");
                }
            }
        }
        //out.add("\n");
        return out;
    }


    public static ArrayList<String> parseResourcesIgnoringCards(JsonNode node){
        ArrayList<String> out = new ArrayList<>();
        Iterator<String> fields = node.fieldNames();
        while (fields.hasNext()){
            String field = fields.next();
            try {
                boolean plur = node.get(field).asInt()>1;
                out.add(getResourceNameIgnoringCards(field, plur)+": "+ node.get(field).asInt()+ "\n\t");
            } catch (Exception e) {
                logger.info(e);
            }
        }
        return out;
    }

    /**
     * Create a string of a resource name that could be singular or plural
     * @param field resource of which needs to take the string's name
     * @param plural plural
     * @return a string of the name
     * @throws Exception if doesn't enter in any check
     */
    public static String getResourceNameIgnoringCards(String field, boolean plural) throws myException {

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
        }else if ("finalVictoryPoint".equalsIgnoreCase(field)) {
            return "finalVictoryPoints";
        }else throw new myException();
    }

    /**
     * Creates a string with the name of a tower that could be singular or plural
     * @param field tower of which needs to take the string's name
     * @param plural plural
     * @return a string of the name
     * @throws Exception if doesn't enter in any check
     */
    public static String getResourceName(String field, boolean plural) throws myException {
    try {
        return getResourceNameIgnoringCards(field, plural);
    }catch (Exception e) {
        logger.info(e);
        if ("territories".equalsIgnoreCase(field)) {
            return (plural ? "Territories" : "Territory");
        } else if ("buildings".equalsIgnoreCase(field)) {
            return "Building" + (plural ? "s" : "");
        } else if ("ventures".equalsIgnoreCase(field)) {
            return "Venture" + (plural ? "s" : "");
        } else if ("characters".equalsIgnoreCase(field)) {
            return "Character" + (plural ? "s" : "");
        }
        throw new myException();
        }
    }

    /**
     * Generates an ArrayList of strings that describe all information about the state of the player,
     * his points, his resources and his cards.
     * @param board json of board
     * @param playerColor color of the player
     * @return  an arrayList with the description
     * @throws Exception
     */
    public static ArrayList<String> getPlayerStatus (JsonNode board, String playerColor) throws myException {
        ArrayList<String> out = new ArrayList<>();
        Iterator<JsonNode> players= board.get("players").elements();
        while (players.hasNext()){
            JsonNode player = players.next();
            if (playerColor.equalsIgnoreCase(player.get("color").asText())){
                JsonNode playerInfo = player;
                out.add("Color: " + playerInfo.get("color").asText()+"\n\t");
                ArrayList<String> resources = parseResourcesIgnoringCards(playerInfo);
                out.addAll(resources);
                Iterator<JsonNode> fmsJson = playerInfo.get("familyMembers").elements();
                out.add("Family members: ");
                while (fmsJson.hasNext()){
                    JsonNode fm = fmsJson.next();
                    if (fm.get("isUsed").asBoolean()) {
                        out.add("\n\t\t" + fm.get("familyMemberColor").asText() + " is used");
                    } else{
                        out.add("\n\t\t" + fm.get("familyMemberColor").asText() + " is  available");
                    }
                }
                JsonNode bonusTiles = playerInfo.get("bonusTiles").get("harvest");
                ArrayList<String> bonusBonusTiles = parseResources(bonusTiles);
                out.add("\n\nHarvest bonus tiles: ");
                out.addAll(bonusBonusTiles);
                Iterator<JsonNode> territories = playerInfo.get("territories").elements();
                out.add("\nTerritory cards:\n\t");
                while (territories.hasNext()) {
                    JsonNode territory = territories.next();
                    out.addAll(cardParser(territory));
                }
                territories = playerInfo.get("characters").elements();
                out.add("\n\nCharacter cards:\n\t");
                while (territories.hasNext()) {
                    JsonNode territory = territories.next();
                    out.addAll(cardParser(territory));
                }
                JsonNode bonusTiles2 = playerInfo.get("bonusTiles").get("production");
                ArrayList<String> bonusBonusTiles2 = parseResources(bonusTiles2);
                out.add("\n\nProduction bonus tiles: ");
                out.addAll(bonusBonusTiles2);
                territories = playerInfo.get("buildings").elements();
                out.add("\nBuilding cards:\n\t");
                while (territories.hasNext()) {
                    JsonNode territory = territories.next();
                    out.addAll(cardParser(territory));
                }
                territories = playerInfo.get("ventures").elements();
                out.add("\n\nVenture cards:\n\t");
                while (territories.hasNext()) {
                    JsonNode territory = territories.next();
                    out.addAll(cardParser(territory));
                }
                out.add("\n");
                return out;
            }
        }
        throw new myException();
    }

    public static ArrayList<String> getProducionChoice (JsonNode board, String playerColor, int index) throws myException {
        ArrayList<String> out = new ArrayList<>();
        Iterator<JsonNode> players = board.get("players").elements();
        while (players.hasNext()) {
            JsonNode player = players.next();
            if (playerColor.equalsIgnoreCase(player.get("color").asText())) {
                JsonNode buildings = player.get("buildings");
                out.add("\nBuilding card:\n\t");
                JsonNode permanentEffect = buildings.get(index).get("permanentEffects");
                out.add("Press 0 to refuse the activation");
                for (int i=1; i<permanentEffect.size(); i++){
                    JsonNode choice = permanentEffect.get(i);
                    out.add("\nFor this choice: "+parseResources(choice)+" press "+i);
                }
            }
        }
        return out;
    }



    /**
     * Generates the description about all information of a specific card.
     * @param card json of the card of which generate the description
     * @return an arrayList with the description
     */
    public static ArrayList<String> cardParser (JsonNode card){
        ArrayList<String> out = new ArrayList<>();
        if ("none".equalsIgnoreCase(card.asText())){
            out.add("\n\tCard: None");
        } else {
            out.add("\n\tCard: " + card.get("name").asText());
            out.add("\n\t\t" + "Type: " + card.get("type").asText());
            if (card.has("activationCost")) {
                out.add("\n\t\t" + "Activation cost: " + card.get("activationCost").asText());
            }
            if (card.has("costs")) {
                out.addAll(costParser(card));
            }
            if (card.has("immediateEffect")) {
                JsonNode immediateEffect = card.get("immediateEffect");
                out.add("\n\t\tImmediate card's effect: ");
                out.addAll(parseResources(immediateEffect));
            }
            if (card.has("permanentEffects")){
                out.add("\n\t\tPermanent card's effect: ");
                JsonNode permanentEffects = card.get("permanentEffects");
                if (permanentEffects.size()==1){
                    JsonNode permanentEffect = permanentEffects.get(0);
                    out.addAll(parseResources(permanentEffect));
                } else {
                    for (int i = 0; i<permanentEffects.size(); i++) {
                        out.add("\n\t\t\tChoice "+ i+ ": ");
                        JsonNode permanentEffect = permanentEffects.get(i);
                        out.addAll(parseResources(permanentEffect));
                    }
                }
            }
        }
        return out;
    }

    /**
     * Parses the costs of a card and generate their description.
     * @param card json of the card of which generate the cost's description
     * @return an arrayList with the description
     */
    public static ArrayList<String> costParser(JsonNode card){
        ArrayList<String> out= new ArrayList<>();
        if (card.get("costs").size() == 1) {
            JsonNode cost = card.get("costs").get(0);
            out.add("\n\t\tCard cost: ");
            out.addAll(parseResources(cost));
        } else {
            Iterator<JsonNode> costs = card.get("costs").elements();
            int counter=0;
            while (costs.hasNext()) {
                out.add("\n\t\tPayment option "+counter+": ");
                JsonNode costchoice = costs.next();
                out.addAll(parseResources(costchoice));
                counter ++;
            }
        }
        return out;
    }

    /**
     * Generates an ArrayList of strings with the message about the final rank and the end of the game.
     * @param board json of the board
     * @return an arrayList with the message's strings
     */
    public static ArrayList<String> theWinnerIs(JsonNode board){
        ArrayList<String> out= new ArrayList<>();
        Iterator<JsonNode> playerOrder = board.get("players").elements();
        out.add("\n\nFINAL RANK");
        int counter=1;
        while (playerOrder.hasNext()){
            JsonNode turnOrder=playerOrder.next();
            out.add("\n\t" + counter +"°: "+ turnOrder.get("color").asText()+ " with " +
            turnOrder.get("victoryPoints").asInt()+ " victory points");
            counter++;
        }
        out.add("\nThanks for playing!");
        return out;
    }


}
